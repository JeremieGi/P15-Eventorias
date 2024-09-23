package com.openclassrooms.p15_eventorias.repository.user

import com.openclassrooms.p15_eventorias.model.User
import com.openclassrooms.p15_eventorias.repository.ResultCustom
import kotlinx.coroutines.flow.Flow


interface UserApi {

    fun getCurrentUserAvatar() : String

    fun changeCurrentUserNotificationEnabled(bNotificationEnabled: Boolean)

    /**
     * Return current user
     */
    fun getCurrentUser() : User?

    /**
     * Enregistre les données de l'utilisateur courant (après sa première identification)
     */
    fun insertCurrentUser()



    /*

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
