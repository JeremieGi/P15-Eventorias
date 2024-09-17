package com.openclassrooms.p15_eventorias.repository.event

import com.openclassrooms.p15_eventorias.R
import com.openclassrooms.p15_eventorias.model.Event
import com.openclassrooms.p15_eventorias.repository.InjectedContext
import com.openclassrooms.p15_eventorias.repository.ResultCustom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val eventApi: EventApi,
    private val injectedContext: InjectedContext // Contexte connu par injection de dépendance (Permet de vérifier l'accès à Internet et aussi d'accéder aux ressources chaines)
){

    /**
     * Liste d'évènement qui sera écouté
     */
    private var _flowEvents : Flow<ResultCustom<List<Event>>> = eventApi.loadAllEvents()
    val flowEvents : Flow<ResultCustom<List<Event>>>
        get() = _flowEvents


    // Charge tous les évènements dans le Flow du repository
    fun loadAllEvents() {

        _flowEvents = if (!injectedContext.isInternetAvailable()) {
            // Créer un flux d'erreur si Internet n'est pas disponible
            flow {
                emit(
                    ResultCustom.Failure(
                        injectedContext.getInjectedContext().getString(R.string.no_network)
                    )
                )
            }
        } else {
            // si Internet est disponible
            eventApi.loadAllEvents()
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
    fun addEvent(event: Event): Flow<ResultCustom<String>> {

        if (!injectedContext.isInternetAvailable()){
            return flow {
                emit(
                    ResultCustom.Failure(
                        injectedContext.getInjectedContext().getString(R.string.no_network)
                    )
                )
            }
        }
        else{
            return eventApi.addEvent(event)
        }

    }



}