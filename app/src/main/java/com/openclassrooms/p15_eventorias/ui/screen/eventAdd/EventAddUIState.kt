package com.openclassrooms.p15_eventorias.ui.screen.eventAdd

sealed class EventAddUIState {

    data object IsLoading : EventAddUIState()

    data object Success : EventAddUIState()

    data class Error(val sError : String?) : EventAddUIState()

}