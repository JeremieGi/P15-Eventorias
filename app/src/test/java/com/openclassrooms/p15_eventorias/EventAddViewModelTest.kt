package com.openclassrooms.p15_eventorias

import com.openclassrooms.p15_eventorias.repository.ResultCustomAddEvent
import com.openclassrooms.p15_eventorias.repository.event.EventFakeAPI
import com.openclassrooms.p15_eventorias.repository.event.EventRepository
import com.openclassrooms.p15_eventorias.repository.user.UserRepository
import com.openclassrooms.p15_eventorias.ui.screen.eventAdd.EventAddResultUIState
import com.openclassrooms.p15_eventorias.ui.screen.eventAdd.EventAddViewModel
import com.openclassrooms.p15_eventorias.ui.screen.eventAdd.FormErrorAddEvent
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

// TODO Denis JG : Retour soutenance : Tests unitaires sur les viewModel

class EventAddViewModelTest {

    // Utilisation de MockK pour le mock du repository
    @MockK
    lateinit var eventRepository: EventRepository

    @MockK
    lateinit var userRepository: UserRepository


    // ViewModel que nous allons tester
    private lateinit var cutViewModel: EventAddViewModel

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined) // Utile pour définir un dispatcher en mode teest
        MockKAnnotations.init(this)
        cutViewModel = EventAddViewModel(eventRepository,userRepository)
    }

    @Test
    fun add_success() = runTest {

        val events = EventFakeAPI.initFakeEvents()
        val event = events[0]

        // Simuler l'avatar de l'utilisateur actuel
        coEvery { userRepository.getCurrentUserAvatar() } returns "avatar_url"

        // Simuler un succès lors de l'ajout d'événement
        coEvery { eventRepository.addEvent(any()) } returns flowOf(ResultCustomAddEvent.Success(event))

        // Appeler la méthode addEvent
        cutViewModel.addEvent()

        // Vérifier que l'UIState a bien été mis à jour avec le succès
        val expectedState = EventAddResultUIState.AddSuccess
        assertEquals(expectedState, cutViewModel.uiState.value.addEventResult)

    }

    @Test
    fun add_loading() = runTest {

        // Simuler l'avatar de l'utilisateur actuel
        coEvery { userRepository.getCurrentUserAvatar() } returns "avatar_url"

        // Simuler un succès lors de l'ajout d'événement
        coEvery { eventRepository.addEvent(any()) } returns flowOf(ResultCustomAddEvent.Loading)

        // Appeler la méthode addEvent
        cutViewModel.addEvent()

        // Vérifier que l'UIState a bien été mis à jour avec le loading
        val expectedState = EventAddResultUIState.AddIsLoading
        assertEquals(expectedState, cutViewModel.uiState.value.addEventResult)

    }

    @Test
    fun add_failure() = runTest {

        val sError = "test error"

        // Simuler l'avatar de l'utilisateur actuel
        coEvery { userRepository.getCurrentUserAvatar() } returns "avatar_url"

        // Simuler un succès lors de l'ajout d'événement
        coEvery { eventRepository.addEvent(any()) } returns flowOf(ResultCustomAddEvent.Failure(sError))

        // Appeler la méthode addEvent
        cutViewModel.addEvent()

        // Vérifier que l'UIState a bien été mis à jour avec le Failure
        val expectedState = EventAddResultUIState.AddError(sError)
        assertEquals(expectedState, cutViewModel.uiState.value.addEventResult)


    }

    @Test
    fun add_datefailure() = runTest {

        val sError = "test error"

        // Simuler l'avatar de l'utilisateur actuel
        coEvery { userRepository.getCurrentUserAvatar() } returns "avatar_url"

        // Simuler un succès lors de l'ajout d'événement
        coEvery { eventRepository.addEvent(any()) } returns flowOf(ResultCustomAddEvent.DateFailure(sError))

        // Appeler la méthode addEvent
        cutViewModel.addEvent()

        // Null => reviens au formulaire
        val expectedAddEventResult = null
        assertEquals(expectedAddEventResult, cutViewModel.uiState.value.addEventResult)

        // Affiche l'erreur sous le champ Date
        val expectedformError = FormErrorAddEvent.DatetimeError(sError)
        assertEquals(expectedformError, cutViewModel.uiState.value.formError)

    }


    @Test
    fun add_addressfailure() = runTest {

        val sError = "test error"

        // Simuler l'avatar de l'utilisateur actuel
        coEvery { userRepository.getCurrentUserAvatar() } returns "avatar_url"

        // Simuler un succès lors de l'ajout d'événement
        coEvery { eventRepository.addEvent(any()) } returns flowOf(ResultCustomAddEvent.AdressFailure(sError))

        // Appeler la méthode addEvent
        cutViewModel.addEvent()

        // Null => reviens au formulaire
        val expectedAddEventResult = null
        assertEquals(expectedAddEventResult, cutViewModel.uiState.value.addEventResult)

        // Affiche l'erreur sous le champ Adresse
        val expectedformError = FormErrorAddEvent.AddressError(sError)
        assertEquals(expectedformError, cutViewModel.uiState.value.formError)

    }

}