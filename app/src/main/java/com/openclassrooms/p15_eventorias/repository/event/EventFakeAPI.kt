package com.openclassrooms.p15_eventorias.repository.event

import com.openclassrooms.p15_eventorias.model.CoordinatesGPS
import com.openclassrooms.p15_eventorias.model.Event
import com.openclassrooms.p15_eventorias.repository.ResultCustom
import com.openclassrooms.p15_eventorias.repository.ResultCustomAddEvent
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow

class EventFakeAPI : EventApi {

    companion object {

        // J'utilise cette procédure pour les previews Compose
        fun initFakeEvents() : MutableList<Event> {

            val sPhotoUser1 = "https://xsgames.co/randomusers/assets/avatars/male/71.jpg"
            val sPhotoUser2 = "https://xsgames.co/randomusers/assets/avatars/female/1.jpg"

            val coordMontpellier = CoordinatesGPS(43.607933,3.879283)
            val coordParis = CoordinatesGPS(48.871341,2.303844)

            return mutableListOf(

                Event("1","Event1","Description de l'évent 1",1629858873000 /* 25/08/2021 */, "https://xsgames.co/randomusers/assets/avatars/male/71.jpg", "Place de la Comédie, 34000 Montpellier", coordMontpellier, sPhotoUser1),

                Event("2","Event2","Description de l'évent 2",1451638679000 /* 01/01/2016 */, "https://storage.canalblog.com/05/71/1016201/88287252_o.png", "84 Avenue des Champs Elysées, 75008 Paris, France", coordParis, sPhotoUser2),

                Event("3","Event3","sans avatar créateur",1451638679000 /* 01/01/2016 */, "", "", null, ""),
                )
        }

    }


    private var events = MutableStateFlow(
        initFakeEvents()
    )

    override fun loadAllEvents(): Flow<ResultCustom<List<Event>>> {

        return callbackFlow {

            trySend(ResultCustom.Loading)
            delay(1*1000)

            val list : List<Event>  = events.value
            trySend(ResultCustom.Success(list))

            // awaitClose : Permet de fermer le listener dès que le flow n'est plus écouté (pour éviter les fuites mémoire)
            awaitClose {

            }
        }

    }

    override fun addEvent(event: Event): Flow<ResultCustomAddEvent<String>> {

        return callbackFlow {

            trySend(ResultCustomAddEvent.Loading)
            delay(1*1000)

            events.value.add(0, event)
            trySend(ResultCustomAddEvent.Success(""))

            // awaitClose : Permet de fermer le listener dès que le flow n'est plus écouté (pour éviter les fuites mémoire)
            awaitClose {

            }

        }

    }

    override fun loadEventByID(idEvent: String): Flow<ResultCustom<Event>> {

        val post = loadByID(idEvent)

        return callbackFlow {

            trySend(ResultCustom.Loading)
            delay(1*1000)

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