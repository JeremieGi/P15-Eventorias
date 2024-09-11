package com.openclassrooms.p15_eventorias.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.openclassrooms.p15_eventorias.ui.ui.theme.P15EventoriasTheme


/**
 * Error in DialogAlert
 */
@Composable
fun ErrorComposable(
    modifier: Modifier = Modifier,
    sErrorMessage : String,
    onClickRetryP: () -> Unit
) {

    // TODO JG : Fenêtre d'erreur



}

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