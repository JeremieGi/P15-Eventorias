package com.openclassrooms.p15_eventorias.ui.screen.eventsList

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclassrooms.p15_eventorias.R
import com.openclassrooms.p15_eventorias.model.Event
import com.openclassrooms.p15_eventorias.ui.ErrorComposable
import com.openclassrooms.p15_eventorias.ui.LoadingComposable

@Composable
fun EventsListScreen(
    modifier: Modifier = Modifier,
    viewModel: EventsListViewModel = hiltViewModel(),
    onEventClickP : (Event) -> Unit = {}
) {

    // Recharger les évents quand l'écran est visible
    LaunchedEffect(Unit) { // Pour déclencher l'effet secondaire une seule fois au cours du cycle de vie de ce composable
        viewModel.loadAllEvents()
    }

    // lorsque la valeur uiState est modifiée,
    // la recomposition a lieu pour les composables utilisant la valeur uiState.
    val uiStateList by viewModel.uiState.collectAsState()

    when (uiStateList) {

        // Chargement
        is EventListUIState.IsLoading -> {
            LoadingComposable(modifier)
        }

        // Récupération des données avec succès
        is EventListUIState.Success -> {

            val listEvents = (uiStateList as EventListUIState.Success).listEvents

            Text(
                modifier = modifier,
                text = "${listEvents.size} events",
            )

        }

        // Exception
        is EventListUIState.Error -> {

            val error = (uiStateList as EventListUIState.Error).exception.message ?: stringResource(
                R.string.unknown_error
            )

            ErrorComposable(
                modifier=modifier,
                sErrorMessage = error,
                onClickRetryP = { viewModel.loadAllEvents() }
            )


        }
    }


}