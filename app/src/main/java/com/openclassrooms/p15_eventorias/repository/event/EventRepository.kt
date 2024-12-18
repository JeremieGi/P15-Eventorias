package com.openclassrooms.p15_eventorias.repository.event

import com.openclassrooms.p15_eventorias.R
import com.openclassrooms.p15_eventorias.model.Event
import com.openclassrooms.p15_eventorias.repository.InjectedContext
import com.openclassrooms.p15_eventorias.repository.ResultCustom
import com.openclassrooms.p15_eventorias.repository.ResultCustomAddEvent
import com.openclassrooms.p15_eventorias.utils.isDateInFuture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val eventApi: EventApi,
    private val injectedContext: InjectedContext // Contexte connu par injection de dépendance (Permet de vérifier l'accès à Internet et aussi d'accéder aux ressources chaines)
){

    /**
     * Liste d'évènement qui sera écouté par les viewModels
     */
    private var _flowEvents = MutableSharedFlow<ResultCustom<List<Event>>>() //eventApi.loadAllEvents() //loadAllEvents() //  //=
    val flowEvents : SharedFlow<ResultCustom<List<Event>>>
        get() = _flowEvents


    // Charge tous les évènements dans le Flow du repository
    suspend fun loadAllEvents(sFilterTitleP : String, bOrderByDatetimeP : Boolean?) {

        withContext(Dispatchers.IO) {

            // Si pas d'Internet
            if (!injectedContext.isInternetAvailable()) {

                _flowEvents.emit(
                    ResultCustom.Failure(
                        injectedContext.getInjectedContext().getString(R.string.no_network)
                    )
                )

            }
            else{

                // On charge les évènements
                eventApi.loadAllEvents(sFilterTitleP, bOrderByDatetimeP).collect { result ->
                    _flowEvents.emit(result)
                }

            }


        }


    }

    // Charge un évènement
    fun loadEventByID(idEvent : String) : Flow<ResultCustom<Event>> {

        if (!injectedContext.isInternetAvailable()) {
            return flow {
                emit(
                    ResultCustom.Failure(
                        injectedContext.getInjectedContext().getString(R.string.no_network)
                    )
                )
            }
        } else {
            return eventApi.loadEventByID(idEvent)
        }
    }

    // Ajoute un évènement
    fun addEvent(event: Event): Flow<ResultCustomAddEvent<Event>> = flow {

        emit(ResultCustomAddEvent.Loading)
        //delay(2000)

        if (!injectedContext.isInternetAvailable()){

            emit(
                ResultCustomAddEvent.Failure(
                    injectedContext.getInjectedContext().getString(R.string.no_network)
                )
            )

        }
        else{

            // Date dans le futur
            if (isDateInFuture(event.lDatetime)){

                // Géolocation de l'adresse
                val sErrorGeolocation = event.geolocate( injectedContext.getInjectedContext() )
                if (sErrorGeolocation.isNotEmpty()) {

                    emit(
                        ResultCustomAddEvent.AdressFailure(
                            sErrorGeolocation
                        )
                    )

                }
                else{

                    // Emettre son propre Flow (avec les éventuelles erreurs ou succès)
                    eventApi.addEvent(event).collect { result ->
                        emit(result)
                    }

                }

            }
            else{

                emit(
                    ResultCustomAddEvent.DateFailure(
                        injectedContext.getInjectedContext().getString(R.string.the_event_date_must_be_in_the_future)
                    )
                )

            }




        }

    }.flowOn(Dispatchers.IO)  // Exécuter sur un thread d'entrée/sortie (IO)



}