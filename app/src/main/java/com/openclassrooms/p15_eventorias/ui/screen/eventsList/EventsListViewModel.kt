package com.openclassrooms.p15_eventorias.ui.screen.eventsList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.p15_eventorias.repository.ResultCustom
import com.openclassrooms.p15_eventorias.repository.event.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventsListViewModel @Inject constructor(
    private val eventRepository: EventRepository
): ViewModel() {



    // UI state - Chargement par défaut
    private val _uiState = MutableStateFlow<EventListUIState>(EventListUIState.IsLoading)
    val uiState: StateFlow<EventListUIState> = _uiState.asStateFlow() // Accès en lecture seule de l'extérieur


    init {
        observeFlow()
    }

    private fun observeFlow() {

        viewModelScope.launch {

            eventRepository.flowEvents.collect { resultFlow ->

                // En fonction du résultat
                when (resultFlow) {

                    // Echec
                    is ResultCustom.Failure ->
                        // Propagation du message d'erreur
                        _uiState.value = EventListUIState.Error(resultFlow.errorMessage)

                    // En chargement
                    is ResultCustom.Loading -> {
                        // Propagation du chargement
                        _uiState.value = EventListUIState.IsLoading
                    }

                    // Succès
                    is ResultCustom.Success -> {
                        val listEvents = resultFlow.value
                        _uiState.value = EventListUIState.Success(listEvents)

                    }


                }

            }

        }
    }

    fun loadAllEvents(sFilterTitleP : String, bOrderByDatetime : Boolean?) {
        viewModelScope.launch {
            eventRepository.loadAllEvents(sFilterTitleP,bOrderByDatetime)
        }
    }



}
