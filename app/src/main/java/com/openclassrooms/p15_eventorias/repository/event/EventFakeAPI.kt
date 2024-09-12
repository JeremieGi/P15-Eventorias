package com.openclassrooms.p15_eventorias.repository.event

import com.openclassrooms.p15_eventorias.model.Event
import com.openclassrooms.p15_eventorias.model.User
import com.openclassrooms.p15_eventorias.repository.ResultCustom
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow

class EventFakeAPI : EventApi {

    private val userTest1 = User("1","Didier","didier@free.fr","https://xsgames.co/randomusers/assets/avatars/male/71.jpg")
    private val userTest2 = User("2","Laetitia","laetitia@free.fr","https://xsgames.co/randomusers/assets/avatars/female/1.jpg")

    private var events = MutableStateFlow(
        mutableListOf(
            Event("1","Event1","Description de l'évent 1",1629858873 /* 25/08/2021 */, "https://xsgames.co/randomusers/assets/avatars/male/71.jpg", "", userTest1),
            Event("2","Event2","Description de l'évent 2",1451638679 /* 01/01/2016 */, "https://storage.canalblog.com/05/71/1016201/88287252_o.png", "", userTest2),
            Event("3","Event3","sans avatar créateur",1451638679 /* 01/01/2016 */, "", "", null),
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