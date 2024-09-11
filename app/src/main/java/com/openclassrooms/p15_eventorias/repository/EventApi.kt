package com.openclassrooms.p15_eventorias.repository

import com.openclassrooms.p15_eventorias.model.Event
import kotlinx.coroutines.flow.Flow

interface EventApi {

    fun loadAllEvents() : Flow<ResultCustom<List<Event>>>

    fun addEvent(event: Event): Flow<ResultCustom<String>>

    fun loadEventByID(idEvent: String): Flow<ResultCustom<Event>>


}
