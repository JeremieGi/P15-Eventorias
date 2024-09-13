package com.openclassrooms.p15_eventorias.ui.screen.eventItem

import com.openclassrooms.p15_eventorias.model.Event

sealed class EventItemUIState {

    data object IsLoading : EventItemUIState()

    data class Success(
        val event : Event
    ) : EventItemUIState()

    data class Error(val exception: Throwable) : EventItemUIState()

}