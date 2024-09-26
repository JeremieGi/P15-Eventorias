package com.openclassrooms.p15_eventorias.repository.user

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.openclassrooms.p15_eventorias.model.User
import com.openclassrooms.p15_eventorias.repository.ResultCustom
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UserFakeAPI : UserApi {


    companion object {

        // J'utilise cette procédure pour les previews Compose
        fun initFakeCurrentUser() : User {
            return User(
                id = "1",
                sName = "fake user",
                sEmail = "fakeuser@fake.fr",
                sURLAvatar = "https://xsgames.co/randomusers/assets/avatars/male/12.jpg",
                bNotificationEnabled = false
            )
        }

    }

    private val userFake = initFakeCurrentUser()

    override fun getCurrentUserID(): String {
        return userFake.id
    }


    override fun getCurrentUserAvatar(): String {
        return userFake.sURLAvatar
    }

    override fun loadCurrentUser(): Flow<ResultCustom<User>> {

        return callbackFlow {

            trySend(ResultCustom.Loading)
            //delay(1*1000)

            trySend(ResultCustom.Success(userFake))

            // awaitClose : Permet de fermer le listener dès que le flow n'est plus écouté (pour éviter les fuites mémoire)
            awaitClose {

            }
        }

    }

    override fun setNotificationEnabled(bNotificationEnabled: Boolean) {
        this.userFake.bNotificationEnabled = bNotificationEnabled
    }


    override fun insertCurrentUser() {
        // On ne fait rien dans la Fake API => car ce code n'est pas utile
    }

    override fun signOut(context: Context): Task<Void> {
        // On ne fait rien dans la Fake API => car ce code n'est pas utile
        return Tasks.forResult(null) // Crée une tâche terminée avec un résultat null
    }

}