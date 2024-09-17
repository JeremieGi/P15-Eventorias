package com.openclassrooms.p15_eventorias.ui.screen.eventsList


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclassrooms.p15_eventorias.R
import com.openclassrooms.p15_eventorias.model.Event
import com.openclassrooms.p15_eventorias.ui.ErrorComposable
import com.openclassrooms.p15_eventorias.ui.LoadingComposable

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.tooling.preview.Preview
import com.openclassrooms.p15_eventorias.ui.ui.theme.P15EventoriasTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import com.openclassrooms.p15_eventorias.repository.event.EventFakeAPI
import com.openclassrooms.p15_eventorias.ui.BottomBarComposable
import com.openclassrooms.p15_eventorias.ui.Screen
import com.openclassrooms.p15_eventorias.ui.URLImageAvatarComposable
import com.openclassrooms.p15_eventorias.ui.URLImageEventComposable
import com.openclassrooms.p15_eventorias.ui.ui.theme.ColorBackground
import com.openclassrooms.p15_eventorias.ui.ui.theme.ColorCardAndInput
import com.openclassrooms.p15_eventorias.ui.ui.theme.ColorTitleWhite
import com.openclassrooms.p15_eventorias.utils.longToFormatedString


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsListScreen(
    //modifier: Modifier = Modifier,
    viewModel: EventsListViewModel = hiltViewModel(),
    onEventClickP: (Event) -> Unit,
    onClickAddP: () -> Unit,
    onClickProfileP : () -> Unit
) {

    Scaffold(

        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.event_list))
                },
                actions = {
                    IconButton(
                        onClick = {
                            // TODO JG : Recherche
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(id = R.string.search),
                            tint = ColorTitleWhite
                        )
                    }
                    IconButton(
                        onClick = {
                            // TODO JG : Tri
                        }
                    ) {
                        Icon(
                            // TODO Denis : Faire une rotation de 90 degrés
                            painter = painterResource(R.drawable.baseline_compare_arrows_24),
                            contentDescription = stringResource(id = R.string.search),
                            tint = ColorTitleWhite
                        )

                    }

                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = ColorBackground,
                )
            )
        },

        floatingActionButtonPosition = FabPosition.End,

        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onClickAddP()
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.addEvent)
                )
            }
        },

        content = { innerPadding ->

            val modifierScaffold =  Modifier.padding(innerPadding)

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
                    LoadingComposable(modifierScaffold)
                }

                // Récupération des données avec succès
                is EventListUIState.Success -> {

                    val listEvents = (uiStateList as EventListUIState.Success).listEvents

                    EventListComposable(
                        modifier = modifierScaffold,
                        listEvents = listEvents,
                        onEventClickP = onEventClickP
                    )

                }

                // Exception
                is EventListUIState.Error -> {

                    val error = (uiStateList as EventListUIState.Error).exception.message ?: stringResource(
                        R.string.unknown_error
                    )

                    ErrorComposable(
                        modifier=modifierScaffold,
                        sErrorMessage = error,
                        onClickRetryP = { viewModel.loadAllEvents() }
                    )


                }
            }
        },

        bottomBar = {
            BottomBarComposable(
                sActiveScreenP = Screen.CTE_EVENTS_LIST_SCREEN,
                onClickEventsP = {},
                onClickProfileP = onClickProfileP
            )
        }

    )


}



@Composable
fun EventListComposable(
    modifier: Modifier = Modifier,
    listEvents: List<Event>,
    onEventClickP: (Event) -> Unit
) {

    LazyColumn (
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = Screen.CTE_PADDING_HORIZONTAL_APPLI.dp,
                vertical = Screen.CTE_PADDING_VERTICAL_APPLI.dp
            )
    ) {

        items(
            items = listEvents,
            key = { it.id }
        ) { event ->

            EventItemListComposable(
                modifier = Modifier.padding(
                    bottom = 5.dp   // Pour mettre un espace bien visible entre les éléments
                ),
                onEventClickP = onEventClickP,
                eventP = event
            )

        }
    }

}

@Composable
fun EventItemListComposable(
    modifier: Modifier,
    eventP: Event,
    onEventClickP: (Event) -> Unit,
    ) {


    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable()
            {
                onEventClickP(eventP)
            },
        colors = CardDefaults.cardColors(
            containerColor = ColorCardAndInput // sinon pardéfaut MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(8.dp)
    ){

        // 3 lignes
        Row(
            // Eléments de l'item centrés verticallement
            verticalAlignment = Alignment.CenterVertically
        ){

            // 1er élément de la ligne : Avatar du créateur
            URLImageAvatarComposable(
                modifier = Modifier
                    .weight(2f) // 20% de la largeur
                    .padding(10.dp) // Pour pas que le rond prenne toute la place
                    ,
                sURLP = eventP.sURLPhotoAuthor
            )

            // 2ème élément de la ligne : Titre de l'évènement + date
            Column(
                modifier = Modifier.weight(4f), // 40% de la largeur
            ) {
                Text(
                    //modifier = Modifier.padding(end= 10.dp),
                    text = eventP.sTitle,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier
                    .height(10.dp)
                )
                Text(
                    text = longToFormatedString(eventP.lDatetime,"MMMM dd, yyyy"),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // 3ème élément de la ligne : Photo de l'évènement
            URLImageEventComposable(
                modifier = Modifier
                    .weight(4f) // 40% de la largeur
                    ,
                sURLP = eventP.sURLEventPicture
            )
        }


    }

}



@Preview("Events list")
@Composable
fun EventListComposablePreview() {


    val listFakeEvent = EventFakeAPI.initFakeEvents()

    P15EventoriasTheme {

        EventListComposable(
            listEvents = listFakeEvent,
            onEventClickP = {}
        )
    }
}