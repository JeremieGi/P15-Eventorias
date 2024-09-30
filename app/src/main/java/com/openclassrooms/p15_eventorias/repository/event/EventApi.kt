package com.openclassrooms.p15_eventorias.repository.event

import com.openclassrooms.p15_eventorias.model.Event
import com.openclassrooms.p15_eventorias.repository.ResultCustom
import com.openclassrooms.p15_eventorias.repository.ResultCustomAddEvent
import kotlinx.coroutines.flow.Flow

interface EventApi {

    // Chargement de tous les évènements (avec filtre ou tri)
    fun loadAllEvents(sFilterTitleP : String, bOrderByDatetimeP : Boolean?) : Flow<ResultCustom<List<Event>>>

    // Ajout d'un évènement
    fun addEvent(event: Event): Flow<ResultCustomAddEvent<Event>>

    // Chargement d'un évènement
    fun loadEventByID(idEvent: String): Flow<ResultCustom<Event>>


}
