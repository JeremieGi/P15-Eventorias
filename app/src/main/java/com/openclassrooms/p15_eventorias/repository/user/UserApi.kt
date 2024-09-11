package com.openclassrooms.p15_eventorias.repository.user

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference

interface UserApi {
/*
    /**
     * Return current user
     */
    fun getCurrentUser() : FirebaseUser?

    /**
     * @return : True => The current user is logged, else False
     */
    fun isCurrentUserLogged() : Boolean

    /**
     * Log out current user
     */
    fun signOut(context : Context) : Task<Void>

*/

}
