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

    // TODo JG : Faire EventDTO

    // Variable globale de Firebase Storage (pour stocker les images)
    private val _storageRef = Firebase.storage.reference

    companion object {

        private const val COLLECTION_EVENTS: String = "events"

        private const val COLLECTION_EVENT_ID: String = "id"
        private const val COLLECTION_EVENT_TITLE: String = "sTitle"

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
                        // J'ai du ajouter des paramètres par défaut aux 2 data class
                        val events = snapshot.toObjects(Event::class.java)

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
            result.whereArrayContains(COLLECTION_EVENT_TITLE, sFilterTitleP)
        }

        when (bOrderByDatetimeP){
            null -> {}  // Pas de tri
            true -> result.orderBy("") // Ascendant
            false -> result.orderBy("", Query.Direction.DESCENDING) // Descendant
        }

        return result

    }

    override fun addEvent(event: Event): Flow<ResultCustomAddEvent<String>> {

        // TODO JG : Manque la sauvegarde de l'avatar de l'auteur

        // Cette méthode crée un Flow qui est basé sur des callbacks, ce qui est idéal pour intégrer des API asynchrones comme Firestore.
        return callbackFlow {

            try {

                // Utilisation de l'ID du post pour créer une référence de document
                val eventDocument = getEventsCollection().document(event.id)

                // On rentre ici, que si le Flow est écouté

                // Si une photo de l'évènement est présente, il faut l'uploader
                if (event.sURLEventPicture != null){

                    // Récupération du content (content://media/picker/0/com.android.providers.media.photopicker/media/1000000035)
                    // dans une URI
                    val uri = Uri.parse(event.sURLEventPicture)

                    // Référence vers le fichier distant
                    val storageRefImage = _storageRef.child("images/eventID${event.id}.jpg")

                    // Upload
                    val uploadTask = storageRefImage.putFile(uri)

                    // Observer les résultats de l'upload
                    uploadTask
                        .addOnFailureListener { exception ->

                            // Gestion des erreurs lors de l'upload
                            trySend(ResultCustomAddEvent.NetworkFailure("Upload failed: ${exception.message}"))

                        }
                        .addOnSuccessListener {

                            // Récupérer l'URL de téléchargement de l'image
                            storageRefImage.downloadUrl
                                .addOnSuccessListener { uri ->

                                    // Mettre à jour l'objet Event avec l'URL de l'image
                                    val updatedEvent = event.copy(sURLEventPicture = uri.toString())

                                    // Mise à jour dans la base de données Firestore
                                    eventDocument.set(updatedEvent)
                                        .addOnSuccessListener {
                                            // Succès de l'ajout dans Firestore
                                            trySend(ResultCustomAddEvent.Success("Event saved"))
                                        }
                                        .addOnFailureListener { firestoreException ->
                                            // Gestion des erreurs lors de l'ajout dans Firestore
                                            trySend(ResultCustomAddEvent.NetworkFailure("Failed to add post to Firestore: ${firestoreException.message}"))
                                        }

                                        .addOnCanceledListener {
                                            trySend(ResultCustomAddEvent.NetworkFailure("addOnCanceledListener"))
                                        }

                                }

                                .addOnFailureListener { urlException ->
                                    // Gestion des erreurs lors de la récupération de l'URL de téléchargement
                                    trySend(ResultCustomAddEvent.NetworkFailure("Failed to get download URL: ${urlException.message}"))
                                }

                                .addOnCanceledListener {
                                    trySend(ResultCustomAddEvent.NetworkFailure("addOnCanceledListener"))
                                }


                        }
                        .addOnCanceledListener {
                            trySend(ResultCustomAddEvent.NetworkFailure("addOnCanceledListener"))
                        }
                        .addOnPausedListener {
                            trySend(ResultCustomAddEvent.NetworkFailure("addOnPausedListener"))
                        }
//                        .addOnProgressListener {
//                            // Appelle en attendant le résultat
//                        }


                }
                else {
                    // Aucune photo

                    // Si aucune photo n'est présente, ajouter directement le post dans Firestore
                    //getPostCollection().add(post)
                    eventDocument.set(event)
                        .addOnSuccessListener {
                            // Succès de l'ajout dans Firestore
                            trySend(ResultCustomAddEvent.Success("Event saved"))
                        }
                        .addOnFailureListener { firestoreException ->
                            // Gestion des erreurs lors de l'ajout dans Firestore
                            trySend(ResultCustomAddEvent.NetworkFailure("Failed to add post to Firestore: ${firestoreException.message}"))
                        }
                        .addOnCanceledListener {
                            trySend(ResultCustomAddEvent.NetworkFailure("addOnCanceledListener"))
                        }


                }


            } catch (e: Exception) {
                trySend(ResultCustomAddEvent.NetworkFailure("Exception occurred: ${e.message}"))
            }



            // awaitClose : Permet d'exécuter du code quand le flow n'est plus écouté
            awaitClose {

            }
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
                        val event = documentSnapshot.toObject(Event::class.java)

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
            .whereEqualTo(COLLECTION_EVENT_ID, idEvent)

    }
}