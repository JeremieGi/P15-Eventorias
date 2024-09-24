package com.openclassrooms.p15_eventorias.repository.user

import android.content.Context
import com.google.android.gms.tasks.Task
import com.openclassrooms.p15_eventorias.model.User
import com.openclassrooms.p15_eventorias.repository.ResultCustom
import kotlinx.coroutines.flow.Flow


interface UserApi {

    // Méthodes synchrones
    fun getCurrentUserID() : String
    fun getCurrentUserAvatar() : String

    fun setNotificationEnabled(bNotificationEnabled: Boolean)

    /**
     * Chargement asynchrone d'un utilisateur
     */
    fun loadCurrentUser(): Flow<ResultCustom<User>>

    /**
     * Enregistre les données de l'utilisateur courant (après sa première identification)
     */
    fun insertCurrentUser()


    fun signOut(context : Context) : Task<Void>
}
