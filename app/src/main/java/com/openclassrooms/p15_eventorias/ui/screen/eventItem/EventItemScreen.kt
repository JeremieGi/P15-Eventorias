package com.openclassrooms.p15_eventorias.ui.screen.eventItem

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel

import com.openclassrooms.p15_eventorias.R
import com.openclassrooms.p15_eventorias.model.Event
import com.openclassrooms.p15_eventorias.ui.ErrorComposable
import com.openclassrooms.p15_eventorias.ui.LoadingComposable
import com.openclassrooms.p15_eventorias.ui.Screen
import com.openclassrooms.p15_eventorias.ui.URLImageAvatarComposable
import com.openclassrooms.p15_eventorias.ui.URLImageEventComposable
import com.openclassrooms.p15_eventorias.ui.ui.theme.ColorBackground
import com.openclassrooms.p15_eventorias.ui.ui.theme.ColorTitleWhite
import com.openclassrooms.p15_eventorias.ui.ui.theme.P15EventoriasTheme
import com.openclassrooms.p15_eventorias.utils.longToFormatedString


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventItemScreen(
    modifier: Modifier = Modifier,
    viewModel: EventItemViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    eventId: String) {


    // Lecture du post
    val uiStateItem by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(eventId) {
       viewModel.loadEventByID(eventId)
    }

    when (uiStateItem) {

        // Chargement
        is EventItemUIState.IsLoading -> {
            LoadingComposable(modifier)
        }

        // Récupération des données avec succès
        is EventItemUIState.Success -> {

            val event = (uiStateItem as EventItemUIState.Success).event

            EventItemComposable(
                modifier=modifier,
                eventP = event,
                onBackClick = onBackClick,
            )

        }

        // Exception
        is EventItemUIState.Error -> {

            val error = (uiStateItem as EventItemUIState.Error).exception.message ?: stringResource(
                R.string.unknown_error
            )

            ErrorComposable(
                modifier=modifier,
                sErrorMessage = error,
                onClickRetryP = { viewModel.loadEventByID(eventId) }
            )


        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventItemComposable(
    modifier: Modifier = Modifier,
    eventP: Event,
    onBackClick: () -> Unit,
) {

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(eventP.sTitle)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onBackClick()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back),
                            tint = ColorTitleWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = ColorBackground,
                )
            )
        }
    ) { contentPadding ->

        Column(
            modifier = modifier
                .padding(contentPadding)
                .padding(
                    horizontal = Screen.CTE_PADDING_HORIZONTAL_APPLI.dp,
                    vertical = Screen.CTE_PADDING_VERTICAL_APPLI.dp
                )
        ){

            val nVerticalSpaceBetweenComposable = 16


            URLImageEventComposable(
                modifier = Modifier
                    .weight(5f)     // Image prend la moitié de l'écran
                    //.height(365.dp)
                    .clip(RoundedCornerShape(12.dp)),
                sURLP = eventP.sURLEventPicture )

            // Espace entre les 2 champs
            Spacer(modifier = Modifier.height(nVerticalSpaceBetweenComposable.dp))

            Column(
                modifier = Modifier
                    .weight(5f)     // L'autre moitié de l'écran pour les autres infos
            ){

                // Ligne avec date / heure et avatar de l'auteur
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){

                    // Partie date et heure avec les pictos
                    FormatedDateComposant(
                        modifier = Modifier
                            .weight(8f),    // 80% de la largeur
                        eventP.lDatetime
                    )


                    URLImageAvatarComposable(
                        modifier = Modifier
                            .weight(2f),    // 20% pour l'avatar
                        sURLP = eventP.sURLPhotoAuthor
                    )

                }

                // Espace entre les 2 champs
                Spacer(modifier = Modifier.height(nVerticalSpaceBetweenComposable.dp))

                Text(
                    text = eventP.sDescription,
                    style = MaterialTheme.typography.bodyMedium
                )

                // Espace entre les 2 champs
                Spacer(modifier = Modifier.height(nVerticalSpaceBetweenComposable.dp))


                AdresseComposable(eventP.sAdress)


            }


        }

    }



}

@Composable
fun AdresseComposable(sAdress: String) {

    Row(){

        Text(
            modifier = Modifier.weight(1f), // Chaque champ utilise la moitié de l'écran
            text = sAdress
        )

        Text(
            modifier = Modifier.weight(1f),
            text = "A remplacer par le champ carte"
        )
    }


}

// Partie date et heure avec les pictos
@Composable
fun FormatedDateComposant(
    modifier: Modifier,
    lDatetime: Long,

) {

    Column(
        modifier = modifier
    ){

        val nSpaceBetweenPictoAnDate = 15

        Row(){

            Icon(
                modifier = Modifier.padding(
                    end = nSpaceBetweenPictoAnDate.dp
                ),
                imageVector = Icons.Filled.DateRange,
                contentDescription = null,
                tint = ColorTitleWhite
            )

            Text(
                text = longToFormatedString(lDatetime,"MMMM dd, yyyy"),
                style = MaterialTheme.typography.bodyLarge
            )

        }

        // Espace entre les 2 lignes
        Spacer(modifier = Modifier.height(16.dp))

        Row(){

            Icon(
                modifier = Modifier.padding(
                    end = nSpaceBetweenPictoAnDate.dp
                ),
                painter = painterResource(R.drawable.baseline_access_time_24),
                contentDescription = null,
                tint = ColorTitleWhite
            )


            Text(
                text = longToFormatedString(lDatetime,"hh:mm"),
                style = MaterialTheme.typography.bodyLarge
            )

        }
    }






}


@Preview("Event Item")
@Composable
fun EventListComposablePreview() {

    val sPhotoUser1 = "https://xsgames.co/randomusers/assets/avatars/male/71.jpg"

    // Coil n'affiche pas les images dans les previews... Ok à l'exec
    val event = Event("1","Event1","Description de l'évent 1",1629858873 /* 25/08/2021 */, "https://xsgames.co/randomusers/assets/avatars/male/71.jpg", "", sPhotoUser1)


    P15EventoriasTheme {

        EventItemComposable(
            eventP = event,
            onBackClick = {}
        )

    }
}
