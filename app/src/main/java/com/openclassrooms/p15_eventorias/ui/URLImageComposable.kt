package com.openclassrooms.p15_eventorias.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.openclassrooms.p15_eventorias.R


@Composable
fun URLImageComposable(
    modifier: Modifier,
    sURLP: String?,
    nIDResssourceIfNotFoundP: Int,
) {

    val modifierRound = modifier
        .size(50.dp)
        .clip(RoundedCornerShape(percent = 100))

    if (sURLP.isNullOrEmpty()) {
        // URL vide

        Image(
            modifier = modifierRound,
            painter = painterResource(id = nIDResssourceIfNotFoundP),
            contentDescription = stringResource(R.string.not_find)
        )

    }
    else{

        // TODO Denis / JG : Voir ici la possibilité d'intégrer l'image de secours + dans ce cas, pas besoin de factoriser dans un composant
        AsyncImage(
            modifier = modifierRound,
            model = ImageRequest.Builder(LocalContext.current)
                .data(sURLP)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,

            )
    }


}