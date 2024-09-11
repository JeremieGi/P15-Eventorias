package com.openclassrooms.p15_eventorias.ui.screen.launch

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclassrooms.p15_eventorias.ui.screen.eventsList.EventsListScreen


@Composable
fun LaunchScreen(
    modifier: Modifier = Modifier,
    viewModel: LaunchViewModel = hiltViewModel(),
) {

    EventsListScreen(modifier = modifier)

}
