package com.openclassrooms.p15_eventorias.repository.event

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.storage
import com.openclassrooms.p15_eventorias.model.Event
import com.openclassrooms.p15_eventorias.repository.ResultCustom
import com.openclassrooms.p15_eventorias.repository.ResultCustomAddEvent
import kotlinx.coroutines.channels.ChannelResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class EventFirestoreAPI : EventApi {

    // Variable globale de Firebase Storage (pour stocker les images)
    private val _storageRef = Firebase.storage.reference

    companion object {

        private const val COLLECTION_EVENTS: String = "events"

    }

    // Get the Collection Reference
    private fun getEventsCollection(): CollectionReference {
        // collection() permet de récupérer la référence d'une collection dont le chemin est renseignée en paramètre de la méthode.
        // ici, on récupère tous les events
        return FirebaseFirestore.getInstance().collection(COLLECTION_EVENTS)
    }

    override fun loadAllEvents(
        sFilterTitleP: String,
        bOrderByDatetimeP: Boolean?
    ): Flow<ResultCustom<List<Event>>> {


        val queryEvents = requestListEvent(sFilterTitleP,bOrderByDatetimeP)

        // Cette méthode crée un Flow qui est basé sur des callbacks, ce qui est idéal pour intégrer des API asynchrones comme Firestore.
        return callbackFlow {

            trySend(ResultCustom.Loading)

            // addSnapshotListener : Ajoute un listener pour écouter les mises à jour en temps réel sur la requête. Chaque fois qu'il y a un changement dans Firestore, ce listener est appelé.
            val listenerRegistration = queryEvents.addSnapshotListener { snapshot, firebaseException ->

                if (firebaseException != null) {

                    trySend(ResultCustom.Failure(firebaseException.message))

                    close(firebaseException) // Fermer le flux en cas d'erreur

                }
                else{

                    val result : ChannelResult<Unit>

                    if (snapshot != null && !snapshot.isEmpty) {

                        // Utiliser toObjects necessite un constructeur par défaut pour tous les objets associés
                        // J'ai du ajouter des paramètres par défaut aux data class
                        val eventsDTO = snapshot.toObjects(FirebaseEventDTO::class.java)

                        val events = eventsDTO.map {
                            it.toModel()
                        }

                        result = trySend(ResultCustom.Success(events)) // Émettre la liste des évènements

                    } else {

                        result = trySend(ResultCustom.Success(emptyList())) // Émettre une liste vide si aucun post n'est trouvé

                    }

                    if (result.isFailure) {
                        trySend(ResultCustom.Failure(result.toString()))
                        close(result.exceptionOrNull())
                    }

                }

            }

            // awaitClose : Permet d'exécuter du code quand le flow n'est plus écouté
            awaitClose {
                listenerRegistration.remove() // Ferme le listener pour éviter une fuite mémoire
            }

        }
    }

    private fun requestListEvent(
        sFilterTitleP: String,
        bOrderByDatetimeP: Boolean?
    ): Query {

        val result: Query = FirebaseFirestore.getInstance().collection(COLLECTION_EVENTS)


        if (sFilterTitleP.isNotEmpty()){
            result.whereArrayContains(FirebaseEventDTO.COLLECTION_EVENT_TITLE, sFilterTitleP)
        }

        when (bOrderByDatetimeP){
            null -> {}  // Pas de tri
            true -> result.orderBy(FirebaseEventDTO.COLLECTION_EVENT_DATETIME) // Ascendant
            false -> result.orderBy(FirebaseEventDTO.COLLECTION_EVENT_DATETIME, Query.Direction.DESCENDING) // Descendant
        }

        return result

    }

    override fun addEvent(event: Event): Flow<ResultCustomAddEvent<Event>> {

        // Cette méthode crée un Flow qui est basé sur des callbacks, ce qui est idéal pour intégrer des API asynchrones comme Firestore.
        return callbackFlow {

            // On rentre ici, que si le Flow est écouté

            // les deux appels se produisent de manière concurrente.

            uploadImageAndSaveEvent(event,event.sURLEventPicture,"PhotoEvent", bEventImage = true).collect{ resultEventPhoto ->

                trySend(resultEventPhoto)

                // TODO Denis : Je ne pense pas à devoir mettre les photos des avatars sur le cloud car l'adresse est une adresse non locale :
                // Pour google : https://lh3.googleusercontent.com/a/ACg8ocJ-lButtYyx-Tylf7WELXM4fom_WbxS3Bj3Xk4T8n91DEI9sNc=s96-c
                // Si il faut le faire, A voir comment on copie une adresse distante vers firebase

//                if (resultEventPhoto is ResultCustomAddEvent.Success){
//
//                    // L'avatar de l'utilisateur courant peut ne pas être exister (Identification par mail ou pas d'avatar dans son compte Google)
//                    if (event.sURLPhotoAuthor.isNotEmpty()) {
//
//                        val uploadedEvent = resultEventPhoto.value
//
//                        // Si il faut le faire, A voir comment on copie une adresse distante vers firebase
//                        uploadImageAndSaveEvent(uploadedEvent,event.sURLPhotoAuthor,"AvatarCreatorEvent", bEventImage = false).collect{ resultPhotoAuthor ->
//                            trySend(resultPhotoAuthor)
//                        }
//
//                    }
//
//                }
//                else{
//                    trySend(resultEventPhoto)
//                }

            }


            // awaitClose : Permet d'exécuter du code quand le flow n'est plus écouté
            awaitClose {

            }
        }


    }

    private fun uploadImageAndSaveEvent(
        eventP : Event,
        sURLP : String,
        sPrefixFileP : String,
        bEventImage : Boolean) = callbackFlow {

        try {

            // Utilisation de l'ID du post pour créer une référence de document
            val eventDocument = getEventsCollection().document(eventP.id)


            // Récupération du content (content://media/picker/0/com.android.providers.media.photopicker/media/1000000035)
            // dans une URI
            // la photo est obligatoire
            val uri = Uri.parse(sURLP)

            // Référence vers le fichier distant
            val storageRefImage = _storageRef.child("images/$sPrefixFileP${eventP.id}.jpg")

            // Upload
            val uploadTask = storageRefImage.putFile(uri)

            // Observer les résultats de l'upload
            uploadTask
                .addOnFailureListener { exception ->

                    // Gestion des erreurs lors de l'upload
                    trySend(ResultCustomAddEvent.Failure("Upload failed: ${exception.message}"))

                }
                .addOnSuccessListener {

                    // Récupérer l'URL de téléchargement de l'image
                    storageRefImage.downloadUrl
                        .addOnSuccessListener { uri ->

                            // Mettre à jour l'objet Event avec l'URL de l'image
                            val updatedEvent : Event
                            if (bEventImage){
                                updatedEvent = eventP.copy(sURLEventPicture = uri.toString())
                            }
                            else{
                                updatedEvent = eventP.copy(sURLPhotoAuthor = uri.toString())
                            }

                            // Mise à jour dans la base de données Firestore
                            val eventDTO = FirebaseEventDTO(updatedEvent)
                            eventDocument.set(eventDTO)
                                .addOnSuccessListener {
                                    // Succès de l'ajout dans Firestore
                                    trySend(ResultCustomAddEvent.Success(updatedEvent))
                                }
                                .addOnFailureListener { firestoreException ->
                                    // Gestion des erreurs lors de l'ajout dans Firestore
                                    trySend(ResultCustomAddEvent.Failure("Failed to add post to Firestore: ${firestoreException.message}"))
                                }

                                .addOnCanceledListener {
                                    trySend(ResultCustomAddEvent.Failure("addOnCanceledListener"))
                                }

                        }

                        .addOnFailureListener { urlException ->
                            // Gestion des erreurs lors de la récupération de l'URL de téléchargement
                            trySend(ResultCustomAddEvent.Failure("Failed to get download URL: ${urlException.message}"))
                        }

                        .addOnCanceledListener {
                            trySend(ResultCustomAddEvent.Failure("addOnCanceledListener"))
                        }


                }
                .addOnCanceledListener {
                    trySend(ResultCustomAddEvent.Failure("addOnCanceledListener"))
                }
                .addOnPausedListener {
                    trySend(ResultCustomAddEvent.Failure("addOnPausedListener"))
                }


        } catch (e: Exception) {
            trySend(ResultCustomAddEvent.Failure("Exception occurred: ${e.message}"))
        }

        awaitClose {

        }

    }

    override fun loadEventByID(idEvent: String): Flow<ResultCustom<Event>> {

        val queryEventByID = requestEventByID(idEvent)

        // Cette méthode crée un Flow qui est basé sur des callbacks, ce qui est idéal pour intégrer des API asynchrones comme Firestore.
        return callbackFlow {

            queryEventByID.get()
                .addOnSuccessListener { querySnapshot: QuerySnapshot ->

                    if (!querySnapshot.isEmpty) {
                        // Récupérer le premier document (puisque ID est unique)
                        val documentSnapshot = querySnapshot.documents[0]
                        val eventDTO = documentSnapshot.toObject(FirebaseEventDTO::class.java)
                        val event = eventDTO?.toModel()
                        if (event==null){
                            trySend(ResultCustom.Failure("Echec du toObject"))
                        }
                        else{
                            trySend(ResultCustom.Success(event))
                        }

                    } else {
                        trySend(ResultCustom.Failure("Aucun document trouvé"))
                    }
                }
                .addOnFailureListener { exception ->
                    trySend(ResultCustom.Failure(exception.message))
                }

            // awaitClose : Suspend la coroutine actuelle jusqu'à ce que le canal soit fermé ou annulé et appelle le bloc donné avant de reprendre la coroutine.
            awaitClose {

            }
        }

    }

    private fun requestEventByID(idEvent: String): Query {

        return this.getEventsCollection()
            .whereEqualTo(FirebaseEventDTO.COLLECTION_EVENT_ID, idEvent)

    }
}