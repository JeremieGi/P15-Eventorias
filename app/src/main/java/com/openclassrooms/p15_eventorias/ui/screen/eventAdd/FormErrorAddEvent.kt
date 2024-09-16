package com.openclassrooms.p15_eventorias.ui.screen.eventAdd

sealed class FormErrorAddEvent {

    data object TitleError : FormErrorAddEvent()
    data object DescriptionError : FormErrorAddEvent()
    data object DatetimeError : FormErrorAddEvent() // TODO JG : Prévoir une vérification que la date soit dans le futur ?
    data class AdressError(val error: String) : FormErrorAddEvent()
    data class PhotoError(val error: String) : FormErrorAddEvent()

}