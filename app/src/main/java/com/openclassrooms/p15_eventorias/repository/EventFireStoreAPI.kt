package com.openclassrooms.p15_eventorias.repository

import com.openclassrooms.p15_eventorias.model.Event
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow

class EventFireStoreAPI : EventApi {

    private var events = MutableStateFlow(
        mutableListOf(
            Event("1"),
            Event("2")
        )
    )

    override fun loadAllEvents(): Flow<ResultCustom<List<Event>>> {

        return callbackFlow {

            val list : List<Event>  = events.value
            trySend(ResultCustom.Success(list))

            // awaitClose : Permet de fermer le listener dès que le flow n'est plus écouté (pour éviter les fuites mémoire)
            awaitClose {

            }
        }

    }

    override fun addEvent(event: Event): Flow<ResultCustom<String>> {

        return callbackFlow {

            events.value.add(0, event)
            trySend(ResultCustom.Success(""))

            // awaitClose : Permet de fermer le listener dès que le flow n'est plus écouté (pour éviter les fuites mémoire)
            awaitClose {

            }

        }

    }

    override fun loadEventByID(idEvent: String): Flow<ResultCustom<Event>> {

        val post = loadByID(idEvent)

        return callbackFlow {

            if (post==null){
                trySend(ResultCustom.Failure("No event find with ID = $idEvent"))
            }
            else{
                trySend(ResultCustom.Success(post))
            }

            // awaitClose : Permet de fermer le listener dès que le flow n'est plus écouté (pour éviter les fuites mémoire)
            awaitClose {

            }
        }

    }

    private fun loadByID(idPost: String) : Event? {

        return events.value.find { it.id == idPost }

    }

}