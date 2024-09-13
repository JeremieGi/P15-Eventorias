package com.openclassrooms.p15_eventorias.ui.screen.eventItem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.p15_eventorias.repository.ResultCustom
import com.openclassrooms.p15_eventorias.repository.event.EventRepository
import com.openclassrooms.p15_eventorias.ui.screen.eventsList.EventListUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventItemViewModel @Inject constructor (
    private val eventRepository: EventRepository
): ViewModel() {

    // UI state - Chargement par défaut
    private val _uiState = MutableStateFlow<EventItemUIState>(EventItemUIState.IsLoading)
    val uiState: StateFlow<EventItemUIState> = _uiState.asStateFlow() // Accès en lecture seule de l'extérieur


    fun loadEventByID (idEvent : String) {

        viewModelScope.launch {

            eventRepository.loadEventByID(idEvent).collect{ resultFlow ->

                // En fonction du résultat
                when (resultFlow) {

                    // Echec
                    is ResultCustom.Failure ->
                        // Propagation du message d'erreur
                        _uiState.value = EventItemUIState.Error(Exception(resultFlow.errorMessage))

                    // En chargement
                    is ResultCustom.Loading -> {
                        // Propagation du chargement
                        _uiState.value = EventItemUIState.IsLoading
                    }

                    // Succès
                    is ResultCustom.Success -> {
                        val event = resultFlow.value
                        _uiState.value = EventItemUIState.Success(event)

                    }


                }

            }

        }


    }



}
