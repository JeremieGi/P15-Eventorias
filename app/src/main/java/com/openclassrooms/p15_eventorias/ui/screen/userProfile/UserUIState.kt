package com.openclassrooms.p15_eventorias.ui.screen.userProfile


import com.openclassrooms.p15_eventorias.model.User


sealed class UserUIState {

    data object IsLoading : UserUIState()

    data class Success(
        val user : User,
        // val bNotificationEnabled : je stocke çà dans Firebase çà simplifiera mon code
    ) : UserUIState()

    data class Error(val sError: String?) : UserUIState()

}