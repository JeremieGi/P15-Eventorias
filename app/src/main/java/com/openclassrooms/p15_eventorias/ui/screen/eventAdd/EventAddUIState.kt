package com.openclassrooms.p15_eventorias.ui.screen.eventAdd

import com.openclassrooms.p15_eventorias.model.Event

sealed class EventAddUIState {

    data object IsLoading : EventAddUIState()

    data object Success : EventAddUIState()

    data class Error(val exception: Throwable) : EventAddUIState()

}