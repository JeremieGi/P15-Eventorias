package com.openclassrooms.p15_eventorias.ui.screen.eventAdd

sealed class FormErrorAddEvent {

    data object TitleError : FormErrorAddEvent()
    data object DescriptionError : FormErrorAddEvent()
    data class DatetimeError(val error: String) : FormErrorAddEvent()
    data class AdressError(val error: String) : FormErrorAddEvent()
    data class PhotoError(val error: String) : FormErrorAddEvent()

}