package com.openclassrooms.p15_eventorias.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.openclassrooms.p15_eventorias.R


@Composable
fun URLImageComposable(
    modifier: Modifier,
    sURLP: String?,
    nIDResssourceIfNotFoundP: Int,
) {

    // SubcomposeAsyncImage => permet d'afficher un composant pour le chargement et pour l'erreur
    // Doc : https://coil-kt.github.io/coil/compose/
    SubcomposeAsyncImage(
        modifier = modifier
            .size(50.dp),
        model = ImageRequest.Builder(LocalContext.current)
            .data(sURLP)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        loading = {
            LoadingComposable()
        },
        error = {
            Image(
                painter = painterResource(id = nIDResssourceIfNotFoundP),
                contentDescription = stringResource(R.string.not_find)
            )
        }
    )

}