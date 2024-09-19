package com.openclassrooms.p15_eventorias.ui.screen.eventAdd

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.p15_eventorias.model.Event
import com.openclassrooms.p15_eventorias.repository.ResultCustomAddEvent
import com.openclassrooms.p15_eventorias.repository.event.EventRepository
import com.openclassrooms.p15_eventorias.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EventAddViewModel @Inject constructor (
    private val eventRepository: EventRepository,
    /*private val */ userRepository: UserRepository // TODO Denis 1 : J'ai un warning injustifié ici : Warning:(21, 13) Constructor parameter is never used as a property
): ViewModel() {

    // TODO Denis : Possible de faire un seul UIState ? Bonne pratique ?

    // UI state - Résultat de la création de l'évènement
    private val _uiStateAddEventResult = MutableStateFlow<EventAddUIState?>(null)
    val uiStateAddEventResult: StateFlow<EventAddUIState?> = _uiStateAddEventResult.asStateFlow() // Accès en lecture seule de l'extérieur

    // State Flow pour les erreurs de formulaire
    private val _uiStateFormError = MutableStateFlow<FormErrorAddEvent?>(null)
    val uiStateFormError: StateFlow<FormErrorAddEvent?> = _uiStateFormError.asStateFlow() // Accès en lecture seule de l'extérieur

    // StateFlow pour conserver les données du formulaire
    private var _uiStateCurrentEvent = MutableStateFlow(
       Event(
           id = UUID.randomUUID().toString(),                       // ID
           sURLPhotoAuthor = userRepository.getCurrentUserAvatar()  // Avatar de l'utilisateur courant
       )
    )
    val uiStateCurrentEvent: StateFlow<Event> = _uiStateCurrentEvent.asStateFlow()


    // Récupère les saisies des différents champs
    fun onAction(formDataAddEvent : FormDataAddEvent) {

        // En fonction du champ, mise à jour de l'évènement courant
        when (formDataAddEvent) {

            is FormDataAddEvent.TitleChanged -> {
                _uiStateCurrentEvent.value = _uiStateCurrentEvent.value.copy(
                    sTitle = formDataAddEvent.title
                )
            }

            is FormDataAddEvent.DescriptionChanged -> {
                _uiStateCurrentEvent.value = _uiStateCurrentEvent.value.copy(
                    sDescription = formDataAddEvent.description
                )
            }
            is FormDataAddEvent.DateTimeChanged -> {
                _uiStateCurrentEvent.value = _uiStateCurrentEvent.value.copy(
                    lDatetime = formDataAddEvent.lDatetimeValue
                )
            }

            is FormDataAddEvent.AdressChanged -> {
                _uiStateCurrentEvent.value = _uiStateCurrentEvent.value.copy(
                    sAdress = formDataAddEvent.adress
                )
            }

            is FormDataAddEvent.PhotoChanged -> {
                _uiStateCurrentEvent.value = _uiStateCurrentEvent.value.copy(
                    sURLEventPicture = formDataAddEvent.url
                )
            }

        }

        // Vérifie les champs obligatoires
        checkFormError()

    }


    private fun displayError(): FormErrorAddEvent? {

        if (_uiStateCurrentEvent.value.sTitle.isEmpty()){
            return FormErrorAddEvent.TitleError
        }

        if (_uiStateCurrentEvent.value.sDescription.isEmpty()){
            return FormErrorAddEvent.DescriptionError
        }

        if (_uiStateCurrentEvent.value.lDatetime == 0L){
            return FormErrorAddEvent.DatetimeError
        }

        if (_uiStateCurrentEvent.value.sAdress.isEmpty()){
            return FormErrorAddEvent.AddressError("Mandatory Adress") // TODO JG : Utiliser les ressources de chaines
        }

        if (_uiStateCurrentEvent.value.sURLEventPicture.isEmpty()){
            return FormErrorAddEvent.PhotoError
        }

        return null

    }

    // Vérifie les erreurs du formulaire en cours de saisi
    private fun checkFormError() {

        // Mise à jour des erreurs
        val formError = displayError()
        if(formError==null){
            _uiStateFormError.value = null          // Pas d'erreur dans la saisie de formulaire
        }
        else{
            _uiStateFormError.value = formError     // erreur passé à un UIState dédié
        }

    }

    // Ajoute l'évènement courant
    fun addEvent(){

        // Récupération de l'évènement courant
        val eventToAdd = _uiStateCurrentEvent.value

        viewModelScope.launch {

            // Le Flow est retourné par la fonction du repository
            eventRepository.addEvent(eventToAdd).collect{ resultFlow ->

                // En fonction du résultat
                when (resultFlow) {

                    // Transmission au UIState dédié

                    // Echec du au réseau
                    is ResultCustomAddEvent.NetworkFailure -> {

                        // Récupération du message d'erreur
                        val sErrorNetwork = EventAddUIState.Error(resultFlow.errorNetwork).sError

                        // Affiche la fenêtre d'erreur
                        _uiStateAddEventResult.value = EventAddUIState.Error(sErrorNetwork)

                    }

                    // Echec du à une adresse incorrecte
                    is ResultCustomAddEvent.AdressFailure -> {

                        // Récupération du message d'erreur
                        val sErrorAddress = EventAddUIState.Error(resultFlow.errorAddress).sError

                        // J'affiche l'erreur sous le champ Adresse pour que l'utilisateur puisse la corriger
                        _uiStateFormError.value = FormErrorAddEvent.AddressError(sErrorAddress)

                        // Raffiche le formulaire
                        _uiStateAddEventResult.value = null

                    }


                    // En chargement
                    is ResultCustomAddEvent.Loading -> {
                        // Propagation du chargement
                        _uiStateAddEventResult.value = EventAddUIState.IsLoading
                    }

                    // Succès
                    is ResultCustomAddEvent.Success -> {
                        _uiStateAddEventResult.value = EventAddUIState.Success
                    }

                }

            }

        }

    }

    /**
     * Renvoie Vrai si le formulaire est complet
     * // TODO Denis 2 : Voir car cette fonction fait doublon avec la gestion des erreurs et elle est utile qu'au lancement de la fenêtre
     */
    fun formIsComplete(): Boolean {

        val currentEvent = _uiStateCurrentEvent.value

        return currentEvent.sTitle.isNotEmpty()
                && currentEvent.sDescription.isNotEmpty()
                && (currentEvent.lDatetime != 0L)
                && currentEvent.sAdress.isNotEmpty()
                && currentEvent.sURLEventPicture.isNotEmpty()


    }

}