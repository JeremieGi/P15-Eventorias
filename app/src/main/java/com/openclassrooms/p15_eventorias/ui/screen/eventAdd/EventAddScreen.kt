package com.openclassrooms.p15_eventorias.ui.screen.eventAdd

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openclassrooms.p15_eventorias.R
import com.openclassrooms.p15_eventorias.ui.Screen
import com.openclassrooms.p15_eventorias.ui.ui.theme.ColorBackground
import com.openclassrooms.p15_eventorias.ui.ui.theme.ColorCardAndInput
import com.openclassrooms.p15_eventorias.ui.ui.theme.ColorTitleWhite
import com.openclassrooms.p15_eventorias.ui.ui.theme.P15EventoriasTheme
import com.openclassrooms.p15_eventorias.utils.longToFormatedString
import java.sql.Time
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date

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
            //verticalArrangement = Arrangement.spacedBy(16.dp), // Espacement entre les éléments
        ){

            // Saisie du titre
            OutlinedTextField(
                modifier = Modifier
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

            Spacer(modifier = Modifier.height(16.dp))

            // Saisie de la description
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ColorCardAndInput),
                value = uiStateCurrentEvent.sDescription,
                textStyle = MaterialTheme.typography.labelLarge,
                isError = (uiStateError is FormErrorAddEvent.DescriptionError),
                onValueChange =  {
                    viewModel.onAction(FormDataAddEvent.DescriptionChanged(it))
                },
                label = {
                    Text(
                        text = stringResource(id = R.string.description),
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                maxLines = 3,
                // On peut toujours personnaliser les autres éléments,
                // comme les couleurs des indicateurs et des labels,
                // via cette fonction, mais pas la couleur de fond qui se paramètre dans le Modifier.
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                )
            )
            if (uiStateError is FormErrorAddEvent.DescriptionError) {
                Text(
                    text = stringResource(id = R.string.mandatorydescription),
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ComposableDateTime(
                datetimeValueInMs = uiStateCurrentEvent.lDatetime,
                onValueChangeDateTimeChanged = {
                    viewModel.onAction(FormDataAddEvent.DateTimeChanged(it))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

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

/**
 * Composable dédié à l'affichage et la sélection de la date et l'heure
 */
@Composable
fun ComposableDateTime(
    modifier: Modifier = Modifier,
    datetimeValueInMs: Long,
    onValueChangeDateTimeChanged : (Long) -> Unit
) {

    // Pour obtenir le contexte
    val context = LocalContext.current

    // Obtenir l'instance du calendrier
    val calendar = Calendar.getInstance()

    // DatePickerDialog pour sélectionner la date
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            // Mettre à jour le ViewModel avec la date sélectionnée
            calendar.set(year, month, dayOfMonth)
            onValueChangeDateTimeChanged(calendar.timeInMillis)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // TimePickerDialog pour sélectionner l'heure
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            // Mettre à jour l'état avec l'heure sélectionnée (format HH:MM)
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            onValueChangeDateTimeChanged(calendar.timeInMillis)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true // Utilisation du format 24h
    )

    // Interface utilisateur avec les champs de saisie
    Row(
        modifier = Modifier
            .fillMaxWidth(),
            //.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Champ de date (JJ/MM/YYYY)
        OutlinedTextField(
            modifier = Modifier
                .weight(1f)
                .clickable() {
                    datePickerDialog.show() // Affiche le picker au clic
                },
            value = longToFormatedString(datetimeValueInMs,"MM/dd/yyyy"),
            onValueChange = { },
            label = {
                Text(
                    text = stringResource(id = R.string.date),
                    style = MaterialTheme.typography.labelMedium
                )
            },
            placeholder = {
                Text(
                    text = stringResource(id = R.string.dateformat),
                    style = MaterialTheme.typography.labelMedium
                )
            },
            readOnly = true, // Empêche la modification manuelle
            enabled = false, // Obligatoire sinon .clickable() { n'est pas appelé
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            )
        )

        // Champ de temps (HH:MM)
        OutlinedTextField(
            modifier = Modifier
                .weight(1f)
                .clickable() {
                    timePickerDialog.show()
                },
            value = longToFormatedString(datetimeValueInMs,"HH:mm"),
            onValueChange = { },
            label = {
                Text(
                    text = stringResource(id = R.string.time),
                    style = MaterialTheme.typography.labelMedium
                )
            },
            placeholder = {
                Text(
                    text = stringResource(id = R.string.timeformat),
                    style = MaterialTheme.typography.labelMedium
                )
            },
            readOnly = true, // Empêche la modification manuelle
            enabled = false, // Obligatoire sinon .clickable() { n'est pas appelé
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            )
        )

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
