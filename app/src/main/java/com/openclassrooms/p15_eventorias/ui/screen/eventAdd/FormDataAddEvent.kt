package com.openclassrooms.p15_eventorias.ui.screen.eventAdd

/**
 * Modification des différents éléments du formulaire d'ajout d'évènement
 */
sealed class FormDataAddEvent {

    data class TitleChanged(val title: String) : FormDataAddEvent()
    data class DescriptionChanged(val description: String) : FormDataAddEvent()
    data class DateTimeChanged(val lDatetimeValue: Long) : FormDataAddEvent()
    data class AdressChanged(val adress: String) : FormDataAddEvent()
    data class PhotoChanged(val url: String) : FormDataAddEvent()


}