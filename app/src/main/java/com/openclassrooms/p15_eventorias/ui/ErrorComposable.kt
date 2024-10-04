package com.openclassrooms.p15_eventorias.ui


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.openclassrooms.p15_eventorias.R
import com.openclassrooms.p15_eventorias.ui.ui.theme.P15EventoriasTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon

import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource



/**
 * Composant d'erreur : Affiche un message d'erreur et un bouton "Try Again"
 */
@Composable
fun ErrorComposable(
    modifier: Modifier = Modifier,
    sErrorMessage : String,
    onClickRetryP: () -> Unit
) {

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.baseline_error_black_36),
            contentDescription = null // Libellé Error ci-dessous sufisant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.error),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = sErrorMessage,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onClickRetryP) {
            Text(stringResource(R.string.try_again))
        }
    }
}

// Le thème n'est pas appliqué....
// A l'exécution, si la fenêtre de login est annulé => style pas appliqué non plus
@Preview(showBackground = true)
@Composable
fun ErrorDialogPreview() {

    P15EventoriasTheme {
        ErrorComposable(
            sErrorMessage = "message d'erreur",
            onClickRetryP = { }
        )
    }

}