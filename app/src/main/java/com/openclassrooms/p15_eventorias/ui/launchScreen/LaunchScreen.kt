package com.openclassrooms.p15_eventorias.ui.launchScreen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.openclassrooms.p15_eventorias.ui.screen.eventsList.EventsListScreen

@Composable
fun LaunchScreen(modifier: Modifier) {

    EventsListScreen(modifier = modifier)

}
