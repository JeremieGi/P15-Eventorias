package com.openclassrooms.p15_eventorias.ui.screen.eventsList

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.openclassrooms.p15_eventorias.model.User
import com.openclassrooms.p15_eventorias.ui.ui.theme.P15EventoriasTheme

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

            EventListComposable(
                modifier = modifier,
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
                modifier=modifier,
                sErrorMessage = error,
                onClickRetryP = { viewModel.loadAllEvents() }
            )


        }
    }


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
                horizontal = 26.dp,
                vertical = 5.dp
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
    ){

        // 3 lignes
        Row(

        ){

            // 1er élément de la ligne : Avatar du créateur
            UserAvatarComposable(eventP.userCreatorEvent?.sURLAvatar)


            // 2ème élément de la ligne : Titre de l'évènement + date
            Text(eventP.sTitle)

            // 3ème élément de la ligne : Photo de l'évènement

        }




    }

}

@Composable
fun UserAvatarComposable(sURLAvatar: String?) {

    val modifierRound = Modifier
        .size(50.dp)
        .clip(RoundedCornerShape(percent = 100))

    if (sURLAvatar.isNullOrEmpty()) {
        // Pas d'avatar utilisateur

        Image(
            modifier = modifierRound,
            painter = painterResource(id = R.drawable.baseline_face_24),
            contentDescription = stringResource(R.string.creator_avatar_not_find)
        )

    }
    else{

        AsyncImage(
            modifier = modifierRound,
            model = ImageRequest.Builder(LocalContext.current)
                .data(sURLAvatar)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,

        )
    }


}


@Preview("Events list")
@Composable
fun EventListComposablePreview() {

    val userTest1 = User("1","Didier","didier@free.fr","https://xsgames.co/randomusers/assets/avatars/male/71.jpg")
    val userTest2 = User("2","Laetitia","laetitia@free.fr","https://xsgames.co/randomusers/assets/avatars/female/1.jpg")

    val listEvents = listOf(
        Event("1","Event1","Description de l'évent 1",1629858873 /* 25/08/2021 */, "https://fr.wikipedia.org/wiki/Fichier:Logo_OpenClassrooms.png", "", userTest1),
        Event("2","Event2","Description de l'évent 2",1451638679 /* 01/01/2016 */, "https://fr.wikipedia.org/wiki/Stade_de_la_Mosson#/media/Fichier:Australie-Fidji.4.JPG", "", userTest2),
        Event("3","Event3","sans avatar créateur",1451638679 /* 01/01/2016 */, "https://fr.wikipedia.org/wiki/Orchestre#/media/Fichier:Orquesta_Filarmonica_de_Jalisco.jpg", "", null)
    )

    P15EventoriasTheme {

        EventListComposable(
            listEvents = listEvents,
            onEventClickP = {}
        )
    }
}