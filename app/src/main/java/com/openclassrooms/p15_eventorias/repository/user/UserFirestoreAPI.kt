package com.openclassrooms.p15_eventorias.repository.user

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.p15_eventorias.model.User
import com.openclassrooms.p15_eventorias.repository.ResultCustom
import kotlinx.coroutines.flow.Flow


class UserFirestoreAPI : UserApi {

    // Companion object pertinent même dans un Singleton pour stocker les constantes indépendantes de l'instance
    companion object {
        private const val COLLECTION_USERS: String = "users"
    }

    // Get the Collection Reference
    private fun getUsersCollection(): CollectionReference {
        // collection() permet de récupérer la référence d'une collection dont le chemin est renseignée en paramètre de la méthode.
        // ici, on récupère tous les users
        return FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
    }

    private fun getCurrentFirebaseUser() : FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    override fun getCurrentUserAvatar(): String {
        return getCurrentFirebaseUser()?.photoUrl.toString()
    }


    override fun changeCurrentUserNotificationEnabled(bNotificationEnabled: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getCurrentUser(): User? {

        val userFirebase = getCurrentFirebaseUser()
        if (userFirebase != null) {
            return firebaseUserToUser(userFirebase)
        }
        else{
            return null
        }

    }

    override fun insertCurrentUser() {

        val userFirebase = getCurrentFirebaseUser()
        // Si un utilisateur est connecté
        if (userFirebase != null) {

            val userToCreate = firebaseUserToUser(userFirebase)

            // Utilisateur existant dans la base de données ?

            // Si l'utilisateur n'existe pas
            getUserData()?.addOnSuccessListener {

                // document() permet de récupérer la référence d'un document dont le chemin est renseigné en paramètre de la méthode
                // set() effectue le INSERT dans la base
                getUsersCollection().document(userToCreate.id).set(userToCreate)

            }


        }

    }

    private fun firebaseUserToUser(userFirebase: FirebaseUser): User {

        val uid = userFirebase.uid                      // Récupération de l'ID créé lors de l'authenfication Firebase
        val name = userFirebase.displayName ?: ""
        val email = userFirebase.email ?: ""
        // val avatar = user. ?: ""
        val userModel = User(
            id = uid,
            sName = name,
            sEmail = email,
            sURLAvatar = "", // TODO JG : Récup avatar
            bNotificationEnabled = false
        )

        return userModel
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


}