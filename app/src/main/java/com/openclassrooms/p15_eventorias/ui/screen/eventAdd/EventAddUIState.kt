package com.openclassrooms.p15_eventorias.ui.screen.eventAdd

import com.openclassrooms.p15_eventorias.model.Event

data class EventAddUIState (

    val addEventResult: EventAddResultUIState? = null,   // Résultat de l'ajout d'événement

    val formError: FormErrorAddEvent? = null,           // Erreurs de formulaire

    val currentEvent: Event = Event()                   // Événement en cours de saisie


)


sealed class EventAddResultUIState {

    data object AddIsLoading : EventAddResultUIState()
    data object AddSuccess : EventAddResultUIState()
    data class AddError(val sError : String?) : EventAddResultUIState()

}