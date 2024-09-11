package com.openclassrooms.p15_eventorias.ui.screen.eventsList

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclassrooms.p15_eventorias.model.Event

@Composable
fun EventsListScreen(
    modifier: Modifier = Modifier,
    viewModel: EventsListViewModel = hiltViewModel(),
    onEventClickP : (Event) -> Unit = {}
) {

    Text(
        text = "EventsListScreen",
        modifier = modifier
    )

}