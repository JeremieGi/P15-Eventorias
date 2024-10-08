package com.openclassrooms.p15_eventorias.ui.screen.eventsList


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.openclassrooms.p15_eventorias.ui.ui.theme.P15EventoriasTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.TextFieldValue
import com.openclassrooms.p15_eventorias.repository.event.EventFakeAPI
import com.openclassrooms.p15_eventorias.ui.BottomBarComposable
import com.openclassrooms.p15_eventorias.ui.Screen
import com.openclassrooms.p15_eventorias.ui.TestTags
import com.openclassrooms.p15_eventorias.ui.URLImageAvatarComposable
import com.openclassrooms.p15_eventorias.ui.URLImageEventComposable
import com.openclassrooms.p15_eventorias.ui.ui.theme.ColorBackground
import com.openclassrooms.p15_eventorias.ui.ui.theme.ColorCardAndInput
import com.openclassrooms.p15_eventorias.ui.ui.theme.ColorTitleWhite
import com.openclassrooms.p15_eventorias.utils.longToFormatedString


@Composable
fun EventsListScreen(
    viewModel: EventsListViewModel = hiltViewModel(),
    onEventClickP: (Event) -> Unit,
    onClickAddP: () -> Unit,
    onClickProfileP : () -> Unit
) {

    // lorsque la valeur uiState est modifiée,
    // la recomposition a lieu pour les composables utilisant la valeur uiState.
    val uiStateList by viewModel.uiState.collectAsState()


    EventListStateComposable(
        uiStateListP = uiStateList,
        loadAllEventsP = viewModel::loadAllEvents,
        onEventClickP = onEventClickP,
        onClickAddP = onClickAddP,
        onClickProfileP = onClickProfileP
    )




}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListStateComposable(
    modifier: Modifier = Modifier,
    uiStateListP: EventListUIState,
    loadAllEventsP: (String, Boolean?) -> Unit,
    onEventClickP: (Event) -> Unit,
    onClickAddP: () -> Unit,
    onClickProfileP : () -> Unit
) {
    // Tri en cours
    var bSortAsc by rememberSaveable { mutableStateOf<Boolean?>(null) }

    // État pour la visibilité du champ de recherche dans la top bar
    var isSearchVisible by remember { mutableStateOf(false) }

    // Filtre par titre en cours
    var searchText by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    val context = LocalContext.current

    // Créer un FocusRequester
    val focusRequester = remember { FocusRequester() }

    // Recharger les évents quand l'écran est visible
    LaunchedEffect(Unit) { // Pour déclencher l'effet secondaire une seule fois au cours du cycle de vie de ce composable
        loadAllEventsP(searchText.text, bSortAsc)
    }

    Scaffold(

        topBar = {
            TopAppBar(
                title = {
                    // Clic sur l'icone de recherche OU rotation de l'écran avec une recherche en cours
                    if (isSearchVisible || searchText.text.isNotEmpty()) {
                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag(TestTags.APPBAR_SEARCH_FIELD) // Pour détecter le champ dans les tests instrumentés
                                .semantics {
                                    this.contentDescription =
                                        context.getString(R.string.search_event_by_title)
                                }
                                .focusRequester(focusRequester), // Associer le FocusRequester au champ,
                            value = searchText,
                            onValueChange = {
                                searchText = it
                                loadAllEventsP(searchText.text, bSortAsc)
                            },
                            singleLine = true
                        )

                        // Donner le focus une fois que le champ est visible
                        LaunchedEffect(Unit) {
                            focusRequester.requestFocus()
                        }

                    } else {
                        Text(stringResource(id = R.string.event_list))
                    }

                },
                actions = {
                    // Utilisé en debug
//                    IconButton(
//                        onClick = {
//                            viewModel.loadAllEvents()
//                        }
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Refresh,
//                            contentDescription = "Refresh",
//                            tint = ColorTitleWhite
//                        )
//                    }


                    IconButton(
                        onClick = {
                            // Change la visibilité du champ de recherche
                            isSearchVisible = !isSearchVisible
                            if (!isSearchVisible) {
                                //searchText.text = "" // Réinitialise le texte de recherche quand il se cache
                                searchText = TextFieldValue("")
                            }
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

                            bSortAsc = when (bSortAsc) {
                                null -> true // Si nul, commence par le tri croissant
                                true -> false // Inverse à décroissant
                                false -> null // Retourne à l'état initial (aucun tri)
                            }

                            // Tri par date
                            loadAllEventsP(searchText.text, bSortAsc)
                        }
                    ) {
                        Icon(
                            modifier = Modifier
                                .graphicsLayer(rotationZ = 90f), // rotation de 90 degrés
                            painter = painterResource(R.drawable.baseline_compare_arrows_24),
                            contentDescription = stringResource(id = R.string.sortByDate),
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

            when (uiStateListP) {

                // Chargement
                is EventListUIState.IsLoading -> {
                    LoadingComposable(modifier.padding(innerPadding))
                }

                // Récupération des données avec succès
                is EventListUIState.Success -> {

                    EventListComposable(
                        modifier = modifier.padding(innerPadding),
                        listEvents = uiStateListP.listEvents,
                        onEventClickP = onEventClickP
                    )

                }

                // Exception
                is EventListUIState.Error -> {

                    val error = uiStateListP.sError ?: stringResource(
                        R.string.unknown_error
                    )

                    ErrorComposable(
                        modifier= modifier.padding(innerPadding),
                        sErrorMessage = error,
                        onClickRetryP = {
                            loadAllEventsP(searchText.text, bSortAsc)
                        }
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

    if (listEvents.isEmpty()){
        Text(
            modifier = modifier
                .fillMaxSize()
                .padding(
                    horizontal = Screen.CTE_PADDING_HORIZONTAL_APPLI.dp,
                    vertical = Screen.CTE_PADDING_VERTICAL_APPLI.dp
                )
               ,
            text = stringResource(R.string.no_event)
        )
    }

    LazyColumn (
        modifier = modifier
            .testTag(TestTags.LAZY_COLUMN_EVENTS)
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
                )
                , // Ajouter un tag pour compter le nombre d'élément dans le test
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
            .testTag("${TestTags.EVENT_ID_PREFIX}${eventP.id}") // Permet d'identifier une ligne dans le test instrumenté
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

            // La box va permettre de respecter les 20% de largeur
            Box(
               modifier =  Modifier
                .weight(2f), // 20% de la largeur
               contentAlignment = Alignment.Center
            ){
                // 1er élément de la ligne : Avatar du créateur
                URLImageAvatarComposable(
                    modifier = Modifier
                        .clip(CircleShape)
                        .padding(5.dp), // Pour pas que le rond prenne toute la place,
                    sURLP = eventP.sURLPhotoAuthor
                )
            }


            // 2ème élément de la ligne : Titre de l'évènement + date
            Column(
                modifier = Modifier.weight(4f), // 40% de la largeur
            ) {
                Text(
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



@Preview(
    name ="Events list success",
    showBackground = true
)
@Composable
fun EventListComposableSuccessPreview() {

    val listFakeEvent = EventFakeAPI.initFakeEvents()
    val uiStateSuccess = EventListUIState.Success(listFakeEvent)

    P15EventoriasTheme {

        EventListStateComposable(
            uiStateListP = uiStateSuccess,
            loadAllEventsP = { _, _ ->
            },
            onEventClickP = { _ ->
            },
            onClickAddP = {
            },
            onClickProfileP = {
            }
        )
    }
}

@Preview("Events list loading")
@Composable
fun EventListComposableLoadingPreview() {

    P15EventoriasTheme {

        EventListStateComposable(
            uiStateListP = EventListUIState.IsLoading,
            loadAllEventsP = { _, _ ->
            },
            onEventClickP = { _ ->
            },
            onClickAddP = {
            },
            onClickProfileP = {
            }
        )
    }
}


@Preview("Events list error")
@Composable
fun EventListComposableErrorPreview() {

    P15EventoriasTheme {

        EventListStateComposable(
            uiStateListP = EventListUIState.Error("Erreur de test de la preview"),
            loadAllEventsP = { _, _ ->
            },
            onEventClickP = { _ ->
            },
            onClickAddP = {
            },
            onClickProfileP = {
            }
        )
    }
}