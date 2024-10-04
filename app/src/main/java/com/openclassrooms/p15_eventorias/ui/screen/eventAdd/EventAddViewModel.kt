package com.openclassrooms.p15_eventorias.ui.screen.eventAdd

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.p15_eventorias.TestEnvironment
import com.openclassrooms.p15_eventorias.repository.ResultCustomAddEvent
import com.openclassrooms.p15_eventorias.repository.event.EventFakeAPI
import com.openclassrooms.p15_eventorias.repository.event.EventRepository
import com.openclassrooms.p15_eventorias.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EventAddViewModel @Inject constructor (
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository // Paramètre car utilisé uniquement dans le constructeur
): ViewModel() {


    // UI state
    private val _uiState = MutableStateFlow(EventAddUIState())
    val uiState: StateFlow<EventAddUIState> = _uiState.asStateFlow() // Accès en lecture seule de l'extérieur

    init{

        // Obligé de faire çà pour affecter une URL au composable Image dans les tests instrumentés...
        if (TestEnvironment.isTesting) {

            val fakeListEvent = EventFakeAPI.initFakeEvents()

            _uiState.update { currentState ->
                currentState.copy(
                    currentEvent = currentState.currentEvent.copy(sURLEventPicture = fakeListEvent[0].sURLEventPicture)
                )
            }
        }

    }

    // Récupère les saisies des différents champs
    fun onAction(formDataAddEvent : FormDataAddEvent) {

        // En fonction du champ, mise à jour de l'évènement courant
        when (formDataAddEvent) {

            is FormDataAddEvent.TitleChanged -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        currentEvent = currentState.currentEvent.copy(sTitle = formDataAddEvent.title)
                    )
                }
            }

            is FormDataAddEvent.DescriptionChanged -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        currentEvent = currentState.currentEvent.copy(sDescription = formDataAddEvent.description)
                    )
                }
            }
            is FormDataAddEvent.DateTimeChanged -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        currentEvent = currentState.currentEvent.copy(lDatetime = formDataAddEvent.lDatetimeValue)
                    )
                }
            }

            is FormDataAddEvent.AdressChanged -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        currentEvent = currentState.currentEvent.copy(sAddress = formDataAddEvent.adress)
                    )
                }
            }

            is FormDataAddEvent.PhotoChanged -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        currentEvent = currentState.currentEvent.copy(sURLEventPicture = formDataAddEvent.url)
                    )
                }
            }

        }

        // Vérifie les champs obligatoires
        checkFormError()

    }


    fun getFormError (): FormErrorAddEvent? {

        if (_uiState.value.currentEvent.sTitle.isEmpty()){
            return FormErrorAddEvent.TitleError
        }

        if (_uiState.value.currentEvent.sDescription.isEmpty()){
            return FormErrorAddEvent.DescriptionError
        }

        if (_uiState.value.currentEvent.lDatetime == 0L){
            return FormErrorAddEvent.DatetimeError("")
        }

        if (_uiState.value.currentEvent.sAddress.isEmpty()){
            return FormErrorAddEvent.AddressError("")
        }

        if (_uiState.value.currentEvent.sURLEventPicture.isEmpty()){
            return FormErrorAddEvent.PhotoError
        }

        return null

    }

    // Vérifie les erreurs du formulaire en cours de saisie
    private fun checkFormError() {

        // Mise à jour des erreurs
        val formError = getFormError()
        if(formError==null){
             // Pas d'erreur dans la saisie de formulaire
            _uiState.update { currentState ->
                currentState.copy(
                    formError = null
                )
            }
        }
        else{

            // erreur passé à l'UIState dédié
            _uiState.update { currentState ->
                currentState.copy(
                    formError = formError
                )
            }
        }

    }

    // Ajoute l'évènement courant
    fun addEvent(){

        // Récupération de l'évènement courant
        val eventToAdd = _uiState.value.currentEvent

        // Ajout de l'ID et de l'avatar de l'utilisteur courant
        val eventToAddWithIDAndAvatar = eventToAdd.copy(
            id = UUID.randomUUID().toString(),
            sURLPhotoAuthor = userRepository.getCurrentUserAvatar()
        )


        viewModelScope.launch {

            // Le Flow est retourné par la fonction du repository
            eventRepository.addEvent(eventToAddWithIDAndAvatar).collect{ resultFlow ->

                // En fonction du résultat
                when (resultFlow) {

                    // Transmission au UIState dédié

                    // Echec du au réseau
                    is ResultCustomAddEvent.Failure -> {

                        // Récupération du message d'erreur
                        val sErrorNetwork = resultFlow.error

                        // Affiche la fenêtre d'erreur
                        _uiState.update { currentState ->
                            val updatedAddEventResult = EventAddResultUIState.AddError(sErrorNetwork)
                            currentState.copy(
                                addEventResult = updatedAddEventResult
                            )
                        }

                    }

                    // Echec du à une adresse incorrecte
                    is ResultCustomAddEvent.AdressFailure -> {

                        // Récupération du message d'erreur
                        val sErrorAddress = resultFlow.errorAddress

                        _uiState.update { currentState ->

                            // J'affiche l'erreur sous le champ Adresse pour que l'utilisateur puisse la corriger
                            val updatedFormError = FormErrorAddEvent.AddressError(sErrorAddress)

                            // Raffiche le formulaire
                            val updatedAddEventResult = null

                            currentState.copy(
                                addEventResult = updatedAddEventResult,
                                formError = updatedFormError
                            )
                        }

                    }

                    // Echec du à une date dans le futur
                    is ResultCustomAddEvent.DateFailure -> {

                        // Récupération du message d'erreur
                        val sErrorDate = resultFlow.errorDate

                        _uiState.update { currentState ->

                            // J'affiche l'erreur sous le champ Date pour que l'utilisateur puisse la corriger
                            val updatedFormError = FormErrorAddEvent.DatetimeError(sErrorDate)

                            // Raffiche le formulaire
                            val updatedAddEventResult = null

                            currentState.copy(
                                addEventResult = updatedAddEventResult,
                                formError = updatedFormError
                            )
                        }

                    }


                    // En chargement
                    is ResultCustomAddEvent.Loading -> {
                        // Propagation du chargement
                        _uiState.update { currentState ->
                            val updatedAddEventResult = EventAddResultUIState.AddIsLoading
                            currentState.copy(
                                addEventResult = updatedAddEventResult
                            )
                        }
                    }

                    // Succès
                    is ResultCustomAddEvent.Success -> {
                        _uiState.update { currentState ->
                            val updatedAddEventResult = EventAddResultUIState.AddSuccess
                            currentState.copy(
                                addEventResult = updatedAddEventResult
                            )
                        }
                    }

                }

            }

        }

    }


}