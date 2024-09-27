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


        val queryEvents = requestListEvent(/*sFilterTitleP,*/bOrderByDatetimeP)

        // Cette méthode crée un Flow qui est basé sur des callbacks, ce qui est idéal pour intégrer des API asynchrones comme Firestore.
        return callbackFlow {

            trySend(ResultCustom.Loading)

            queryEvents.get()
                .addOnSuccessListener { documents ->

                    val eventsDTOList = documents.map { document ->
                        document.toObject(FirebaseEventDTO::class.java)
                    }

                    var events = eventsDTOList.map {
                        it.toModel()
                    }

                    if (sFilterTitleP.isNotEmpty()){
                        events = events.filter { it.sTitle.contains(sFilterTitleP) }
                    }

                    trySend(ResultCustom.Success(events)) // Émettre la liste des évènements

                }
                .addOnFailureListener { exception ->
                    trySend(ResultCustom.Failure(exception.message))
                }

            // awaitClose : Permet d'exécuter du code quand le flow n'est plus écouté
            awaitClose {
                //listenerRegistration.remove() // Ferme le listener pour éviter une fuite mémoire
            }

        }
    }

    private fun requestListEvent(
        //sFilterTitleP: String,
        bOrderByDatetimeP: Boolean?
    ): Query {

        // TODO JG : Recherche 'Contient' ne fonctionnent pas, essayer * + il faut créer un index

        var result: Query = FirebaseFirestore.getInstance().collection(COLLECTION_EVENTS)

//        if (sFilterTitleP.isNotEmpty()){
//            // Il n'y a pas de condition "content" en firebase (je vais donc filter lors de la lecture de la requête)
//            result = result.whereEqualTo(FirebaseEventDTO.COLLECTION_EVENT_TITLE,sFilterTitleP)
//        }

        when (bOrderByDatetimeP){
            null -> {}  // Pas de tri
            true -> result = result.orderBy(FirebaseEventDTO.COLLECTION_EVENT_DATETIME) // Ascendant
            false -> result = result.orderBy(FirebaseEventDTO.COLLECTION_EVENT_DATETIME, Query.Direction.DESCENDING) // Descendant
        }

        return result

    }

    override fun addEvent(event: Event): Flow<ResultCustomAddEvent<Event>> {

        // Cette méthode crée un Flow qui est basé sur des callbacks, ce qui est idéal pour intégrer des API asynchrones comme Firestore.
        return callbackFlow {

            // On rentre ici, que si le Flow est écouté

            uploadImageAndSaveEvent(event,event.sURLEventPicture).collect{ resultEventPhoto ->
                trySend(resultEventPhoto)
            }

            // awaitClose : Permet d'exécuter du code quand le flow n'est plus écouté
            awaitClose {

            }
        }


    }

    private fun uploadImageAndSaveEvent(
        eventP : Event,
        sURLP : String) = callbackFlow {
        try {

            // Utilisation de l'ID du post pour créer une référence de document
            val eventDocument = getEventsCollection().document(eventP.id)


            // Récupération du content (content://media/picker/0/com.android.providers.media.photopicker/media/1000000035)
            // dans une URI
            // la photo est obligatoire
            val uri = Uri.parse(sURLP)

            // Référence vers le fichier distant
            val storageRefImage = _storageRef.child("images/PhotoEvent${eventP.id}.jpg")

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
                            val updatedEvent : Event = eventP.copy(sURLEventPicture = uri.toString())

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