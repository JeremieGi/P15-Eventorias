package com.openclassrooms.p15_eventorias.ui.screen.eventsList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.p15_eventorias.repository.event.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventsListViewModel @Inject constructor(
    private val eventRepository: EventRepository
): ViewModel() {



    init {
        viewModelScope.launch {
            eventRepository.flowEvents.collect {

            }
        }
    }



}
