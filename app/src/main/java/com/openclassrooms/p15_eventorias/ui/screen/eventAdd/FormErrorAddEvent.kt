package com.openclassrooms.p15_eventorias.ui.screen.eventAdd

/**
 * Différents types d'erreur sur le formulaire d'ajout d'évènement
 */
sealed class FormErrorAddEvent {

    data object TitleError : FormErrorAddEvent()
    data object DescriptionError : FormErrorAddEvent()
    data object DatetimeError : FormErrorAddEvent() // TODO JG : Prévoir une vérification que la date soit dans le futur ?
    //data object AdressError : FormErrorAddEvent()
    data class AddressError(val errorAddress: String?) : FormErrorAddEvent()
    data object PhotoError : FormErrorAddEvent()
    //data class PhotoError(val error: String) : FormErrorAddEvent()

}