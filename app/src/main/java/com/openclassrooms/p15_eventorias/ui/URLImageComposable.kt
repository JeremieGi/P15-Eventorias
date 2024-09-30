package com.openclassrooms.p15_eventorias.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.openclassrooms.p15_eventorias.R
import com.openclassrooms.p15_eventorias.ui.ui.theme.ColorTitleWhite


@Composable
fun URLImageAvatarComposable(
    modifier: Modifier = Modifier,
    sURLP: String
) {

    // SubcomposeAsyncImage => permet d'afficher un composant pour le chargement et pour l'erreur
    // Doc : https://coil-kt.github.io/coil/compose/
    SubcomposeAsyncImage(
        modifier = modifier
            //.size(50.dp)
            //.clip(CircleShape)
            /*.aspectRatio(1f)*/,
        model = ImageRequest.Builder(LocalContext.current)
            .data(sURLP)
            .build(),
        contentDescription = stringResource(R.string.avatar_of_the_event_creator),
        contentScale = ContentScale.Crop,
        loading = {
            CircularProgressIndicator()
        },
        error = {
            Image(
                painter = painterResource(id = R.drawable.baseline_face_24),
                contentDescription = stringResource(R.string.not_find),
                colorFilter = ColorFilter.tint(ColorTitleWhite)
            )
        }
    )

}

@Composable
fun URLImageEventComposable(
    modifier: Modifier = Modifier,
    sURLP: String
) {

    // SubcomposeAsyncImage => permet d'afficher un composant pour le chargement et pour l'erreur
    // Doc : https://coil-kt.github.io/coil/compose/
    SubcomposeAsyncImage(
        modifier = modifier
            .fillMaxSize(),
        model = ImageRequest.Builder(LocalContext.current)
            .data(sURLP)
            .build(),
        contentDescription = stringResource(R.string.event_image),
        contentScale = ContentScale.Crop,  // https://developer.android.com/reference/kotlin/androidx/compose/ui/layout/ContentScale
        loading = {
            CircularProgressIndicator()
        },
        error = {
            Image(
                painter = painterResource(id = R.drawable.baseline_image_not_supported),
                contentDescription = stringResource(R.string.not_find),
                colorFilter = ColorFilter.tint(ColorTitleWhite)
            )
        }
    )

}