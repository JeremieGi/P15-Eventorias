package com.openclassrooms.p15_eventorias.ui.screen.eventAdd

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openclassrooms.p15_eventorias.R
import com.openclassrooms.p15_eventorias.repository.event.EventFakeAPI
import com.openclassrooms.p15_eventorias.ui.Screen
import com.openclassrooms.p15_eventorias.ui.screen.eventItem.EventItemComposable
import com.openclassrooms.p15_eventorias.ui.ui.theme.ColorBackground
import com.openclassrooms.p15_eventorias.ui.ui.theme.ColorCardAndInput
import com.openclassrooms.p15_eventorias.ui.ui.theme.ColorTitleWhite
import com.openclassrooms.p15_eventorias.ui.ui.theme.P15EventoriasTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventAddScreen(
    modifier: Modifier = Modifier,
    viewModel: EventAddViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {

    // Gestion du formulaire (Champs obligatoires)
    val uiStateError by viewModel.uiStateFormError.collectAsStateWithLifecycle()

    // Données du formulaire conservées dans le ViwModel
    val uiStateCurrentEvent by viewModel.uiStateCurrentEvent.collectAsStateWithLifecycle()

    // Obtenir le résultat de l'enregistrement de l'évènement
    val uiStateAddEventResult by viewModel.uiStateAddEventResult.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.event_creation))
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
            modifier = Modifier
                .padding(contentPadding)
                .padding(
                    horizontal = Screen.CTE_PADDING_HORIZONTAL_APPLI.dp,
                    vertical = Screen.CTE_PADDING_VERTICAL_APPLI.dp
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp), // Espacement entre les éléments
        ){


            OutlinedTextField(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .background(ColorCardAndInput),
                value = uiStateCurrentEvent.sTitle,
                textStyle = MaterialTheme.typography.labelLarge,
                isError = (uiStateError is FormErrorAddEvent.TitleError),
                onValueChange =  {
                    viewModel.onAction(FormDataAddEvent.TitleChanged(it))
                },
                label = {
                    Text(
                        text = stringResource(id = R.string.title),
                        style = MaterialTheme.typography.labelMedium
                    )
                 },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                singleLine = true,
                // On peut toujours personnaliser les autres éléments,
                // comme les couleurs des indicateurs et des labels,
                // via cette fonction, mais pas la couleur de fond qui se paramètre dans le Modifier.
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                )
            )
            if (uiStateError is FormErrorAddEvent.TitleError) {
                Text(
                    text = stringResource(id = R.string.mandatorytitle),
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {

                }
            ){
                Text(
                    text = stringResource(id = R.string.validate),
                )
            }

        }

    }



}


@Preview("Event Add")
@Composable
fun EventListComposablePreview() {

    P15EventoriasTheme {

        EventAddScreen(
            onBackClick = {}
        )

    }

}
