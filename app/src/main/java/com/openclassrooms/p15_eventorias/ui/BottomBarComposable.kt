package com.openclassrooms.p15_eventorias.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.openclassrooms.p15_eventorias.R
import com.openclassrooms.p15_eventorias.ui.ui.theme.ColorCardAndInput
import com.openclassrooms.p15_eventorias.ui.ui.theme.ColorTitleWhite
import com.openclassrooms.p15_eventorias.ui.ui.theme.P15EventoriasTheme

@Composable
fun BottomBarComposable(
    sActiveScreenP : String,
    onClickEventsP  : () -> Unit,
    onClickProfileP : () -> Unit
) {


    // Box utile pour centrer horizontalement le contenu
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(), // Ajoute un padding en fonction de la barre de navigation système,
        contentAlignment = Alignment.TopCenter
    ) {

        // Une ligne
        Row{

            // Colonne pour Afficher un Text sous un IconButton
            Column{
                IconButton(
                    modifier = Modifier.testTag("iconEvent"),
                    onClick = {
                        onClickEventsP()
                    },
                    enabled = (sActiveScreenP!=Screen.CTE_EVENTS_LIST_SCREEN) ,// pas actif si on est déjà sur l'écran de liste des évènements
                    colors = IconButtonDefaults.iconButtonColors(
                        disabledContainerColor = ColorCardAndInput
                    )
                ) {

                    Icon(
                        painter = painterResource(R.drawable.baseline_event_24),
                        contentDescription = stringResource(id = R.string.events),
                        tint = ColorTitleWhite)

                }
                Text(
                    text = stringResource(id = R.string.events),
                    style = MaterialTheme.typography.bodyMedium
                )
            }


            Spacer(modifier = Modifier.width(20.dp))

            // Colonne pour Afficher un Text sous un IconButton
            Column{
                IconButton(
                    modifier = Modifier.testTag("iconProfile"),
                    onClick = {
                        onClickProfileP()
                    },
                    enabled = (sActiveScreenP!=Screen.CTE_USER_PROFILE_SCREEN), // pas actif si on est déjà sur l'écran de profil
                    colors = IconButtonDefaults.iconButtonColors(
                        disabledContainerColor = ColorCardAndInput
                    )
                ) {

                    Icon(
                        painter = painterResource(R.drawable.outline_person_24),
                        contentDescription = stringResource(id = R.string.profile),
                        tint = ColorTitleWhite
                    )

                }

                Text(
                    text = stringResource(id = R.string.profile),
                    style = MaterialTheme.typography.bodyMedium
                )
            }


        }

    }



}





@Preview("BottomBar")
@Composable
fun BottomBarComposablePreview() {

    P15EventoriasTheme {
        BottomBarComposable(
            sActiveScreenP = Screen.CTE_USER_PROFILE_SCREEN,
            onClickEventsP = {},
            onClickProfileP = {}
        )
    }
}

