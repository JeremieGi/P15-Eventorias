package com.openclassrooms.p15_eventorias.repository.user

import android.content.Context
import com.google.android.gms.tasks.Task
import com.openclassrooms.p15_eventorias.model.User
import com.openclassrooms.p15_eventorias.repository.ResultCustom
import kotlinx.coroutines.flow.Flow


interface UserApi {

    // Méthodes synchrones

    // Donne l'ID de l'utilisateur courant
    fun getCurrentUserID() : String

    // Donne l'URL de l'avatar de l'utilisateur courant
    fun getCurrentUserAvatar() : String

    // Modification du paramètrages des notifications
    fun setNotificationEnabled(bNotificationEnabled: Boolean)

    /**
     * Chargement asynchrone d'un utilisateur
     */
    fun loadCurrentUser(): Flow<ResultCustom<User>>

    /**
     * Enregistre les données de l'utilisateur courant (après sa première identification)
     */
    fun insertCurrentUser()

    /**
     * Déconnecte un utilisateur
     */
    fun signOut(context : Context) : Task<Void>
}
