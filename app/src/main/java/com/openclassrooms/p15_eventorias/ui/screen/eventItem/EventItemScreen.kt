package com.openclassrooms.p15_eventorias.ui.screen.eventItem

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun EventItemScreen(
    modifier: Modifier = Modifier,
    viewModel: EventItemViewModel = hiltViewModel(),
    onBackClick: () -> Boolean,
    eventId: Int) {

    Text(
        modifier = modifier,
        text = "ID = $eventId"
    )

}
