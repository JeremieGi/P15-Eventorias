package com.openclassrooms.p15_eventorias.repository.user

import android.content.Context
import com.google.android.gms.tasks.Task
import com.openclassrooms.p15_eventorias.R
import com.openclassrooms.p15_eventorias.model.User
import com.openclassrooms.p15_eventorias.repository.InjectedContext
import com.openclassrooms.p15_eventorias.repository.ResultCustom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userApi: UserApi,
    private val injectedContext: InjectedContext // Contexte connu par injection de dépendance (Permet de vérifier l'accès à Internet et aussi d'accéder aux ressources chaines)
) {

    fun getCurrentUserAvatar() : String {
        return userApi.getCurrentUserAvatar()
    }

    fun loadCurrentUser() : Flow<ResultCustom<User>> {

        if (!injectedContext.isInternetAvailable()) {
            return flow {
                emit(
                    ResultCustom.Failure(
                        injectedContext.getInjectedContext().getString(R.string.no_network)
                    )
                )
            }
        } else {
            return userApi.loadCurrentUser()
        }

    }

    fun changeCurrentUserNotificationEnabled(bNotificationEnabled: Boolean) {

        userApi.setNotificationEnabled(bNotificationEnabled)

    }

    fun insertCurrentUser() {
        userApi.insertCurrentUser()
    }

    fun getCurrentUserID() : String {
        return userApi.getCurrentUserID()
    }

    /**
     * Log out current user
     */
    fun signOut(context : Context) : Task<Void> {
        return userApi.signOut(context)
    }
}