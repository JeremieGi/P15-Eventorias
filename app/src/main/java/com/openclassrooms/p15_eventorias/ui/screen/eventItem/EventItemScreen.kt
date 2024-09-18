package com.openclassrooms.p15_eventorias.ui.screen.eventItem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest

import com.openclassrooms.p15_eventorias.R
import com.openclassrooms.p15_eventorias.model.Event
import com.openclassrooms.p15_eventorias.repository.event.EventFakeAPI
import com.openclassrooms.p15_eventorias.ui.ErrorComposable
import com.openclassrooms.p15_eventorias.ui.LoadingComposable
import com.openclassrooms.p15_eventorias.ui.Screen
import com.openclassrooms.p15_eventorias.ui.URLImageAvatarComposable
import com.openclassrooms.p15_eventorias.ui.URLImageEventComposable
import com.openclassrooms.p15_eventorias.ui.ui.theme.ColorBackground
import com.openclassrooms.p15_eventorias.ui.ui.theme.ColorTitleWhite
import com.openclassrooms.p15_eventorias.ui.ui.theme.P15EventoriasTheme
import com.openclassrooms.p15_eventorias.utils.googleAPIDrawCard
import com.openclassrooms.p15_eventorias.utils.longToFormatedString


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

    EventItemStateComposable(
        modifier=modifier,
        uiStateItemP = uiStateItem,
        onBackClick = onBackClick,
        onLoadEventByID = { viewModel.loadEventByID(eventId) }
    )


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventItemStateComposable(
    modifier: Modifier = Modifier,
    uiStateItemP: EventItemUIState,
    onBackClick: () -> Unit,
    onLoadEventByID: () -> Unit,
) {


    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    if (uiStateItemP is EventItemUIState.Success){
                        Text(uiStateItemP.event.sTitle)
                    }
                    else{
                        Text(stringResource(id = R.string.event))
                    }
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

        when (uiStateItemP) {

            // Chargement
            is EventItemUIState.IsLoading -> {
                LoadingComposable(modifier.padding(contentPadding))
            }

            // Récupération des données avec succès
            is EventItemUIState.Success -> {

                EventItemSuccessComposable(
                    modifier=modifier.padding(contentPadding),
                    eventP = uiStateItemP.event
                )

            }

            // Exception
            is EventItemUIState.Error -> {

                val error = uiStateItemP.sError ?: stringResource(
                    R.string.unknown_error
                )


                ErrorComposable(
                    modifier=modifier
                        .padding(contentPadding)
                        ,
                    sErrorMessage = error,
                    onClickRetryP = onLoadEventByID
                )


            }
        }



    }



}

@Composable
fun EventItemSuccessComposable(
    modifier: Modifier,
    eventP: Event) {

    Column(
        modifier = modifier
            .padding(
                horizontal = Screen.CTE_PADDING_HORIZONTAL_APPLI.dp,
                vertical = Screen.CTE_PADDING_VERTICAL_APPLI.dp
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp), // Espacement entre les éléments
    ){


        URLImageEventComposable(
            modifier = Modifier
                .weight(5f)     // Image prend la moitié de l'écran
                //.height(365.dp)
                .clip(RoundedCornerShape(12.dp)),
            sURLP = eventP.sURLEventPicture )


        Column(
            modifier = Modifier
                .weight(5f)     // L'autre moitié de l'écran pour les autres infos
            ,
            verticalArrangement = Arrangement.spacedBy(16.dp), // Espacement entre les éléments
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


            Text(
                text = eventP.sDescription,
                style = MaterialTheme.typography.bodyMedium
            )

            AdresseComposable(eventP)


        }


    }

}

@Composable
fun AdresseComposable(event : Event) {

    Row {

        Text(
            modifier = Modifier.weight(1f), // Chaque champ utilise la moitié de l'écran
            text = event.sAdress
        )

        // https://developers.google.com/maps/documentation/maps-static/overview?hl=fr

        var sURLCompose by remember { mutableStateOf("") }

        // Obtenir la localisation actuelle
        LaunchedEffect(event) {
            // Ici, on est dans une coroutine
            sURLCompose = googleAPIDrawCard(event.coordGPS)
        }

        SubcomposeAsyncImage(
            modifier = Modifier
                .weight(1f)
        ,
            model = ImageRequest.Builder(LocalContext.current)
                .data(sURLCompose)
                .build(),
            contentDescription = null,
            //contentScale = ContentScale.Crop,
            loading = {
                CircularProgressIndicator()
            },
            error = {
                Text(
                    text = stringResource(R.string.impossible_to_load_a_map)
                )
            }
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

        Row {

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

        Row {

            Icon(
                modifier = Modifier.padding(
                    end = nSpaceBetweenPictoAnDate.dp
                ),
                painter = painterResource(R.drawable.baseline_access_time_24),
                contentDescription = null,
                tint = ColorTitleWhite
            )


            Text(
                text = longToFormatedString(lDatetime,"HH:mm"),
                style = MaterialTheme.typography.bodyLarge
            )

        }
    }






}


@Preview("Event Item Loading")
@Composable
fun EventItemStateComposableLoadingPreview() {

    val uiStateLoading = EventItemUIState.IsLoading

    P15EventoriasTheme {

        EventItemStateComposable(
            uiStateItemP = uiStateLoading,
            onBackClick = {},
            onLoadEventByID = {}
        )

    }
}


@Preview("Event Item Success")
@Composable
fun EventItemStateComposableSuccessPreview() {


    // Coil n'affiche pas les images dans les previews... Ok à l'exec

    val listFakeEvent = EventFakeAPI.initFakeEvents()
    val uiStateSuccess = EventItemUIState.Success(listFakeEvent[0])

    P15EventoriasTheme {

        EventItemStateComposable(
            uiStateItemP = uiStateSuccess,
            onBackClick = {},
            onLoadEventByID = {}
        )

    }


}


@Preview("Event Item Error")
@Composable
fun EventItemStateComposableErrorPreview() {

    val uiStateError = EventItemUIState.Error("Message de test pour la preview")

    P15EventoriasTheme {

        EventItemStateComposable(
            uiStateItemP = uiStateError,
            onBackClick = {},
            onLoadEventByID = {}
        )

    }
}




