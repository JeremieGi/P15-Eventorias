package com.openclassrooms.p15_eventorias.repository.user


interface UserApi {

    fun getCurrentUserAvatar() : String

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
