package com.openclassrooms.p15_eventorias

import android.content.Context
import com.openclassrooms.p15_eventorias.model.Event
import com.openclassrooms.p15_eventorias.repository.InjectedContext
import com.openclassrooms.p15_eventorias.repository.ResultCustom
import com.openclassrooms.p15_eventorias.repository.ResultCustomAddEvent
import com.openclassrooms.p15_eventorias.repository.event.EventApi
import com.openclassrooms.p15_eventorias.repository.event.EventFakeAPI
import com.openclassrooms.p15_eventorias.repository.event.EventRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.test.runTest
import java.util.Calendar


class EventRepositoryTest {


    private lateinit var cutEventRepository : EventRepository // Class Under Test

    // Paramètres du repository
    private lateinit var mockAPI: EventApi
    private lateinit var mockInjectedContext : InjectedContext

    // Contexte du test (mocké)
    private lateinit var mockContext: Context

    /**
     * Création des mocks
     */
    @Before
    fun createRepositoryWithMockedParameters() {
        mockAPI = mockk()
        mockInjectedContext = mockk()
        mockContext = mockk()
        cutEventRepository = EventRepository(mockAPI,mockInjectedContext)
    }

    /**
     * Chargement de tous les évènements : Succès
     */
    @Test
    fun loadAllEvents_success()  = runTest {

        // definition des mocks

        // Connexion Internet OK
        coEvery {
            mockInjectedContext.isInternetAvailable()
        } returns true

        // Liste des évènements
        val mockListEvent  = EventFakeAPI.initFakeEvents()
        coEvery {
            mockAPI.loadAllEvents(any(),any())
        } returns flowOf(ResultCustom.Success(mockListEvent))

        // Créer le collecteur du flow du repository
        val resultList = mutableListOf<ResultCustom<List<Event>>>()
        val job = launch {
            cutEventRepository.flowEvents.collect { result ->
                resultList.add(result)
            }
        }

        // Test réel de la fonction
        val jobLoadAllEvents = launch {
            cutEventRepository.loadAllEvents("",true)
        }



        // Attend que toutes les couroutines en attente s'executent
        //advanceUntilIdle()
        jobLoadAllEvents.join()

        // coVerify : s'assure que les fonctions des mocks ont été appelées
        coVerify {
            mockInjectedContext.isInternetAvailable()
            mockAPI.loadAllEvents(any(),any())
        }

        // On attend les valeurs de mockAPI.loadAllEvents
        assertEquals(1, resultList.size)

        val expectedResult = ResultCustom.Success(mockListEvent)
        assertEquals(expectedResult,resultList[0])

        // Cancel the collection job
        job.cancel()
        jobLoadAllEvents.cancel()
    }


    /**
     * Chargement de tous les évènements : Pas de connexion Internet
     */
    @Test
    fun loadAllEvents_NoInternetConnexion() = runTest {

        // definition des mocks

        // Pas de connexion Internet
        coEvery {
            mockInjectedContext.isInternetAvailable()
        } returns false

        // Configurer le comportement de injectedContext pour getInjectedContext()
        coEvery {
            mockInjectedContext.getInjectedContext()
        } returns mockContext

        // Configurer le comportement de injectedContext pour getString()
        coEvery {
            mockContext.getString(R.string.no_network)
        } returns "No Network Connection"


        // Créer le collecteur du flow du repository
        val resultList = mutableListOf<ResultCustom<List<Event>>>()
        val job = launch {
            cutEventRepository.flowEvents.collect { result ->
                resultList.add(result)
            }
        }

        //Test réel de la fonction
        val jobLoadAllEvents = launch{
            cutEventRepository.loadAllEvents("",true)
        }



        // Attend que toutes les couroutines en attente s'executent
        //advanceUntilIdle()
        jobLoadAllEvents.join()

        // coVerify : s'assure que les fonctions des mocks ont été appelées
        coVerify {
            mockInjectedContext.isInternetAvailable()
            mockInjectedContext.getInjectedContext()
            mockContext.getString(any())
        }


        // On attend les valeurs de mockAPI.loadAllEvents
        assertEquals(1, resultList.size)
        assert(resultList[0] is ResultCustom.Failure)

        // Cancel the collection job
        job.cancel()
        jobLoadAllEvents.cancel()
    }

    /**
     * Chargement d'un évènement avec succès
     */
    @Test
    fun loadEventByID_success() = runTest {

        // definition des mocks

        // Connexion Internet OK
        coEvery {
            mockInjectedContext.isInternetAvailable()
        } returns true

        // evènement
        val mocklistEven = EventFakeAPI.initFakeEvents()
        val mockEvent  = mocklistEven[0]
        coEvery {
            mockAPI.loadEventByID(any())
        } returns flowOf(ResultCustom.Success(mockEvent))

        // Créer le collecteur du flow du repository
        val resultList = mutableListOf<ResultCustom<Event>>()
        val job = launch {
            cutEventRepository.loadEventByID("1").collect { result ->
                resultList.add(result)
            }
        }

        // Attend que toutes les couroutines en attente s'executent
        //advanceUntilIdle()
        job.join()

        // coVerify : s'assure que les fonctions des mocks ont été appelées
        coVerify {
            mockInjectedContext.isInternetAvailable()
            mockAPI.loadEventByID(any())
        }

        // On attend les valeurs de mockAPI.loadAllEvents
        assertEquals(1, resultList.size)

        val expectedResult = ResultCustom.Success(mockEvent)
        assertEquals(expectedResult,resultList[0])

        // Cancel the collection job
        job.cancel()


    }

    /**
     * Chargement d'un évènement sans connexion Internet
     */
    @Test
    fun loadEventByID_NoInternetConnexion() = runTest {

        // definition des mocks

        // Pas de connexion Internet
        coEvery {
            mockInjectedContext.isInternetAvailable()
        } returns false

        // Configurer le comportement de context
        coEvery {
            mockInjectedContext.getInjectedContext()
        } returns mockContext

        // Configurer le comportement de context pour getString()
        coEvery {
            mockContext.getString(R.string.no_network)
        } returns "No Network Connection"


        // Créer le collecteur du flow du repository
        val resultList = mutableListOf<ResultCustom<Event>>()
        val job = launch {
            cutEventRepository.loadEventByID("1").collect { result ->
                resultList.add(result)
            }
        }

        // Attend que toutes les couroutines en attente s'executent
        //advanceUntilIdle()
        job.join()

        // coVerify : s'assure que les fonctions des mocks ont été appelées
        coVerify {
            mockInjectedContext.isInternetAvailable()
            mockInjectedContext.getInjectedContext()
            mockContext.getString(any())
        }

        // On attend les valeurs de mockAPI.loadAllEvents
        assertEquals(1, resultList.size)
        assert(resultList[0] is ResultCustom.Failure)

        // Cancel the collection job
        job.cancel()

    }

    /**
     * Ajout d'un évènement avec succès
     */
    @Test
    fun addEvent_success() = runTest {

        // definition des mocks

        // Connexion Internet OK
        coEvery {
            mockInjectedContext.isInternetAvailable()
        } returns true

        // Configurer le comportement de context
        coEvery {
            mockInjectedContext.getInjectedContext()
        } returns mockContext


        val mockEvent = mockk<Event>(relaxed = true) // L’option relaxed = true est utilisée pour éviter de devoir simuler toutes les méthodes non utilisées.
        val calendar = Calendar.getInstance() // Obtenir l'instance de Calendar pour la date actuelle
        calendar.add(Calendar.DAY_OF_YEAR, 1) // Ajouter un jour
        coEvery { mockEvent.id } returns "idTest"
        coEvery { mockEvent.lDatetime } returns calendar.timeInMillis

        coEvery {
            mockEvent.geolocate(any())
        } returns ""

        coEvery {
            mockAPI.addEvent(any())
        } returns flowOf(ResultCustomAddEvent.Success(mockEvent))

        // Créer le collecteur du flow du repository
        val resultList = mutableListOf<ResultCustomAddEvent<Event>>()

        val job = launch {
            cutEventRepository.addEvent(mockEvent).collect { result ->
                resultList.add(result)
            }
        }

        // Attend que toutes les couroutines en attente s'executent
        //advanceUntilIdle()
        job.join()

        // coVerify : s'assure que les fonctions des mocks ont été appelées
        coVerify {
            mockInjectedContext.isInternetAvailable()
            mockAPI.addEvent(any())
            mockEvent.geolocate(any())
        }

        // On attend les valeurs de mockAPI.loadAllEvents
        assertEquals(2, resultList.size)

        assertEquals(ResultCustomAddEvent.Loading,resultList[0])

        val expectedResult = ResultCustomAddEvent.Success(mockEvent)
        assertEquals(expectedResult,resultList[1])

        // Cancel the collection job
        job.cancel()

    }

    /**
     * Ajout d'un évènement sans connexion Internet
     */
    @Test
    fun addEvent_NoInternetConnexion() = runTest {

        // Evènement à ajouter
        val calendar = Calendar.getInstance() // Obtenir l'instance de Calendar pour la date actuelle
        calendar.add(Calendar.DAY_OF_YEAR, 1) // Ajouter un jour
        val eventToAdd = Event(id ="Test", lDatetime = calendar.timeInMillis)

        // definition des mocks

        // Connexion Internet => erreur
        coEvery {
            mockInjectedContext.isInternetAvailable()
        } returns false

        // Configurer le comportement de context pour getString()
        coEvery {
            mockContext.getString(any())
        } returns ""

        // Configurer le comportement de context
        coEvery {
            mockInjectedContext.getInjectedContext()
        } returns mockContext

        // Créer le collecteur du flow du repository
        val resultList = mutableListOf<ResultCustomAddEvent<Event>>()
        val job = launch {
            cutEventRepository.addEvent(eventToAdd).collect { result ->
                resultList.add(result)
            }
        }

        // Attend que toutes les couroutines en attente s'executent
        //advanceUntilIdle()
        job.join()

        // coVerify : s'assure que les fonctions des mocks ont été appelées
        coVerify {
            mockInjectedContext.isInternetAvailable()
            mockContext.getString(any())
            mockInjectedContext.getInjectedContext()
        }

        // On attend les valeurs de mockAPI.loadAllEvents
        assertEquals(2, resultList.size)

        assertEquals(ResultCustomAddEvent.Loading,resultList[0])

        assert(resultList[1] is ResultCustomAddEvent.Failure)

        // Cancel the collection job
        job.cancel()

    }

    /**
     * Ajout d'un évènement à une date passée => erreur
     */
    @Test
    fun addEvent_DateInPastError() = runTest {

        // Evènement à ajouter / Date dans le passé
        val calendar = Calendar.getInstance() // Obtenir l'instance de Calendar pour la date actuelle
        calendar.add(Calendar.DAY_OF_YEAR, -1) // Enleve un jour
        val eventToAdd = Event(id ="Test", lDatetime = calendar.timeInMillis)

        // definition des mocks

        // Connexion Internet OK
        coEvery {
            mockInjectedContext.isInternetAvailable()
        } returns true

        // Configurer le comportement de context
        coEvery {
            mockInjectedContext.getInjectedContext()
        } returns mockContext

        // Configurer le comportement de context pour getString()
        coEvery {
            mockContext.getString(any())
        } returns ""


        // Créer le collecteur du flow du repository
        val resultList = mutableListOf<ResultCustomAddEvent<Event>>()

        val job = launch {
            cutEventRepository.addEvent(eventToAdd).collect { result ->
                resultList.add(result)
            }
        }

        // Attend que toutes les couroutines en attente s'executent
       // advanceUntilIdle()
        job.join()

        // coVerify : s'assure que les fonctions des mocks ont été appelées
        coVerify {
            mockInjectedContext.isInternetAvailable()
            mockInjectedContext.getInjectedContext()
            mockContext.getString(any())
        }

        // On attend les valeurs
        assertEquals(2, resultList.size)

        assertEquals(ResultCustomAddEvent.Loading,resultList[0])

        assert(resultList[1] is ResultCustomAddEvent.DateFailure)

        // Cancel the collection job
        job.cancel()

    }

}