package com.openclassrooms.p15_eventorias.ui.screen.eventItem

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
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

    Column(
        modifier = modifier
    ){
        Text(
            text = "ID = $eventId"
        )

        Button(
            onClick = {}
        ){
            Text(
                text = "test style"
            )
        }
    }


}
