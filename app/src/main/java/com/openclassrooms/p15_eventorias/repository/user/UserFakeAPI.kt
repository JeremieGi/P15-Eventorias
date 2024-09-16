package com.openclassrooms.p15_eventorias.repository.user

class UserFakeAPI : UserApi {


    override fun getCurrentUserAvatar(): String {
        return "https://xsgames.co/randomusers/assets/avatars/male/12.jpg"
    }

}