package com.openclassrooms.p15_eventorias.repository.event

import com.openclassrooms.p15_eventorias.model.Event
import com.openclassrooms.p15_eventorias.repository.ResultCustom
import com.openclassrooms.p15_eventorias.repository.ResultCustomAddEvent
import kotlinx.coroutines.flow.Flow

interface EventApi {

    fun loadAllEvents(sFilterTitleP : String, bOrderByDatetimeP : Boolean?) : Flow<ResultCustom<List<Event>>>

    fun addEvent(event: Event): Flow<ResultCustomAddEvent<Event>>

    fun loadEventByID(idEvent: String): Flow<ResultCustom<Event>>


}
