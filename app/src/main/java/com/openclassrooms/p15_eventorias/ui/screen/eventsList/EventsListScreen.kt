package com.openclassrooms.p15_eventorias.ui.screen.eventsList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import com.openclassrooms.p15_eventorias.model.User
import com.openclassrooms.p15_eventorias.ui.URLImageComposable
import com.openclassrooms.p15_eventorias.ui.ui.theme.P15EventoriasTheme
import com.openclassrooms.p15_eventorias.utils.longToFormatedDate
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import com.openclassrooms.p15_eventorias.ui.ui.theme.ColorCard


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
            .clickable()
            {
                onEventClickP(eventP)
            },
        colors = CardDefaults.cardColors(
            containerColor = ColorCard // sinon pardéfaut MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(8.dp)
    ){

        // 3 lignes
        Row(
            // Eléments de l'item centrés verticallement
            verticalAlignment = Alignment.CenterVertically
        ){

            // 1er élément de la ligne : Avatar du créateur
            URLImageComposable(
                modifier = Modifier
                    .weight(2f) // 20% de la largeur
                    .padding(10.dp)
                    .aspectRatio(1f) // Ajustez le rapport selon vos besoins
                    .clip(CircleShape)

                    ,
                sURLP = eventP.userCreatorEvent?.sURLAvatar,
                nIDResssourceIfNotFoundP = R.drawable.baseline_face_24
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
                    text =longToFormatedDate(eventP.lDatetime),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // 3ème élément de la ligne : Photo de l'évènement
            URLImageComposable(
                modifier = Modifier
                    .weight(4f) // 40% de la largeur
                    .fillMaxSize(),
                sURLP = eventP.sURLEventPicture,
                nIDResssourceIfNotFoundP = R.drawable.baseline_image_not_supported,
            )
        }


    }

}



@Preview("Events list")
@Composable
fun EventListComposablePreview() {

    val userTest1 = User("1","Didier","didier@free.fr","https://xsgames.co/randomusers/assets/avatars/male/71.jpg")
    val userTest2 = User("2","Laetitia","laetitia@free.fr","https://xsgames.co/randomusers/assets/avatars/female/1.jpg")

    // Coil n'affiche pas les images dans les previews... Ok à l'exec
    val listEvents = listOf(
        Event("1","Event1","Description de l'évent 1",1629858873 /* 25/08/2021 */, "https://xsgames.co/randomusers/assets/avatars/male/71.jpg", "", userTest1),
        Event("2","Event2","Description de l'évent 2",1451638679 /* 01/01/2016 */, "https://storage.canalblog.com/05/71/1016201/88287252_o.png", "", userTest2),
        Event("3","Event3","sans avatar créateur",1451638679 /* 01/01/2016 */, "", "", null),
    )

    P15EventoriasTheme {

        EventListComposable(
            listEvents = listEvents,
            onEventClickP = {}
        )
    }
}