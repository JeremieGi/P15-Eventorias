package com.openclassrooms.p15_eventorias.repository.user

import com.openclassrooms.p15_eventorias.model.User
import com.openclassrooms.p15_eventorias.repository.ResultCustom
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
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


    override fun getCurrentUserAvatar(): String {
        return userFake.sURLAvatar
    }


    override fun changeCurrentUserNotificationEnabled(bNotificationEnabled: Boolean) {
        this.userFake.bNotificationEnabled = bNotificationEnabled
    }

    override fun getCurrentUser(): User? {
        return this.userFake
    }

    override fun insertCurrentUser() {
        // On ne fait rien dans la Fake API => car ce code n'est pas utile
    }

}