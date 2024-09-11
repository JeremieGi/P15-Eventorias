package com.openclassrooms.p15_eventorias.ui.screen.eventAdd

import androidx.lifecycle.ViewModel
import com.openclassrooms.p15_eventorias.repository.event.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EventAddViewModel @Inject constructor (
    private val eventRepository: EventRepository
): ViewModel() {



}