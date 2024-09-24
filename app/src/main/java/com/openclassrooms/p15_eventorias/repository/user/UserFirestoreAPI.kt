package com.openclassrooms.p15_eventorias.repository.user

import android.content.Context
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.p15_eventorias.model.User
import com.openclassrooms.p15_eventorias.repository.ResultCustom
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


class UserFirestoreAPI : UserApi {

    // Companion object pertinent même dans un Singleton pour stocker les constantes indépendantes de l'instance
    companion object {

        private const val COLLECTION_USERS: String = "users"

        // De préférence uniquement en minuscule (ici même nom que dans le DTO)
        private const val FIELD_NOTIFICATION_ENABLED: String = "bnotificationenabled"
    }

    private var _currentUser : User? = null

    // Get the Collection Reference
    private fun getUsersCollection(): CollectionReference {
        // collection() permet de récupérer la référence d'une collection dont le chemin est renseignée en paramètre de la méthode.
        // ici, on récupère tous les users
        return FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
    }

    private fun getCurrentFirebaseUser() : FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    override fun getCurrentUserID(): String {
        // L'id de l'utilisateur en base de données et celui de Firebase Auth est le même
        return getCurrentFirebaseUser()?.uid?:""
    }

    override fun getCurrentUserAvatar(): String {
        //return getCurrentFirebaseUser()?.photoUrl.toString()
        return _currentUser?.sURLAvatar?:""
    }


    override fun setNotificationEnabled(bNotificationEnabled: Boolean) {

        // let -> permet de travailler sur currentUser non null et aussi protège la variable des changements possibles depuis d'autres threads
        _currentUser?.let { currentUser ->

            // Récupère le document user en base de données
            val docUser = getUsersCollection().document(currentUser.id)

            docUser.update(FIELD_NOTIFICATION_ENABLED,bNotificationEnabled)
                .addOnSuccessListener {
                    _currentUser = currentUser.copy(
                        bNotificationEnabled = bNotificationEnabled
                    )
                }
//                .addOnFailureListener { e ->
//
//                }

        }

    }

    override fun loadCurrentUser(): Flow<ResultCustom<User>> {

        return callbackFlow {

//            // Utilisateur courant déjà en mémoire
//            _currentUser?.let { currentUser ->
//
//                // On le retourne
//                trySend(ResultCustom.Success(currentUser))
//
//
//            } ?: run {
                // Bloc de code à exécuter si _currentUser est null
                // (1er appel à la méthode)

                // On récupère dans Firebase
                val userFirebase = getCurrentFirebaseUser()
                if (userFirebase != null) {
                    // Puis en base de données pour les champs supplémentaires
                    val docUser = getUsersCollection().document(userFirebase.uid)
                    docUser.get()
                        .addOnSuccessListener { documentSnapshot ->
                            // Ici on est dans une coroutine

                            if (documentSnapshot.exists()) {
                                // Le snapshot contient des données valides

                                // Récupération du DTO
                                val userDTO = documentSnapshot.toObject(FirebaseUserDTO::class.java)

                                // Conversion en User Model
                                _currentUser = userDTO?.toModel()

                                _currentUser?.let {
                                    trySend(ResultCustom.Success(it))
                                }?:run{
                                    trySend(ResultCustom.Failure("documentSnapshot.toObject return null"))
                                }


                            } else {
                                trySend(ResultCustom.Failure("Firebase User not find in the database (Snapshot Error)"))
                            }

                        }
                        .addOnFailureListener { e ->
                            // Ici on est dans une coroutine
                            trySend(ResultCustom.Failure("Failure during database access : ${e.message}"))
                        }
                }
                else{
                    trySend(ResultCustom.Failure("Firebase User not find in Authentication"))
                }
        //    }

            // awaitClose : Permet d'exécuter du code quand le flow n'est plus écouté
            awaitClose {

            }
        }




    }

    /**
     * Crée un utilisateur en base de données à partir de données de Firebase
     */
    override fun insertCurrentUser() {

        // Récupération des données Firebase
        val userFirebase = getCurrentFirebaseUser()
        // Si un utilisateur est connecté
        if (userFirebase != null) {

            // Avec le DTO, je contrôle le nom des champs de base de données
            val userToCreate = firebaseUserToUserDTO(userFirebase)


            // Utilisateur existant dans la base de données ?

            // Si l'utilisateur n'existe pas
            getUserData()?.addOnSuccessListener {

                // document() permet de récupérer la référence d'un document dont le chemin est renseigné en paramètre de la méthode
                // set() effectue le INSERT dans la base
                getUsersCollection().document(userToCreate.id).set(userToCreate)

            }


        }

    }

    private fun firebaseUserToUserDTO(userFirebase: FirebaseUser): FirebaseUserDTO {

        val uid = userFirebase.uid                      // Récupération de l'ID créé lors de l'authenfication Firebase
        val name = userFirebase.displayName ?: ""
        val email = userFirebase.email ?: ""
        val avatar = userFirebase.photoUrl.toString()
        val userDTO = FirebaseUserDTO(
            id = uid,
            sName = name,
            sEmail = email,
            sURLAvatar = avatar, // TODO Denis : Ca récupère l'avatar du compte Google par exemple. on en reste là
            bNotificationEnabled = false
        )

        return userDTO
    }

    /**
     * Get User Data from Firestore
     */
    private fun getUserData(): Task<DocumentSnapshot>? {

        val uidCurrentUser : String? = this.getCurrentUserUID()

        return if (uidCurrentUser != null) {
            getUsersCollection().document(uidCurrentUser).get() // Renvoie l'utilisateur
        } else {
            null
        }

    }

    private fun getCurrentUserUID(): String? {
        return this.getCurrentFirebaseUser()?.uid
    }

    /**
     * Log out current user
     */
    override fun signOut(context : Context) : Task<Void> {
        return AuthUI.getInstance().signOut(context)
    }

}