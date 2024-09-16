package com.openclassrooms.p15_eventorias.ui.screen.eventAdd

sealed class FormErrorAddEvent {

    data class TitleError(val error: String) : FormErrorAddEvent()
    data class DescriptionError(val error: String) : FormErrorAddEvent()
    data class DatetimeError(val error: String) : FormErrorAddEvent()
    data class AdressError(val error: String) : FormErrorAddEvent()
    data class PhotoError(val error: String) : FormErrorAddEvent()

}