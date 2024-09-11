package com.openclassrooms.p15_eventorias.ui.screen.eventItem

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun EventItemScreen(
    modifier: Modifier = Modifier,
    viewModel: EventItemViewModel = hiltViewModel(),
    onBackClick: () -> Boolean,
    eventId: Int) {

}
