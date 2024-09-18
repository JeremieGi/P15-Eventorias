package com.openclassrooms.p15_eventorias.ui.screen.eventsList

import com.openclassrooms.p15_eventorias.model.Event

sealed class EventListUIState {

    data object IsLoading : EventListUIState()

    data class Success(
        val listEvents : List<Event>
    ) : EventListUIState()

    data class Error(val sError: String?) : EventListUIState()

}