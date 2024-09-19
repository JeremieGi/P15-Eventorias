package com.openclassrooms.p15_eventorias.ui.screen.eventAdd

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.openclassrooms.p15_eventorias.BuildConfig
import com.openclassrooms.p15_eventorias.R
import com.openclassrooms.p15_eventorias.model.Event
import com.openclassrooms.p15_eventorias.repository.event.EventFakeAPI
import com.openclassrooms.p15_eventorias.ui.ErrorComposable
import com.openclassrooms.p15_eventorias.ui.LoadingComposable
import com.openclassrooms.p15_eventorias.ui.Screen
import com.openclassrooms.p15_eventorias.ui.ui.theme.ColorBackground
import com.openclassrooms.p15_eventorias.ui.ui.theme.ColorCardAndInput
import com.openclassrooms.p15_eventorias.ui.ui.theme.ColorTitleWhite
import com.openclassrooms.p15_eventorias.ui.ui.theme.MyButtonStyle
import com.openclassrooms.p15_eventorias.ui.ui.theme.P15EventoriasTheme
import com.openclassrooms.p15_eventorias.utils.createImageFile
import com.openclassrooms.p15_eventorias.utils.longToFormatedString
import java.util.Calendar
import java.util.Objects

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

        // Gestion du résultat de l'ajout
        val currentStateUiStateAddEventResult = uiStateAddEventResult // Utilisation d'un variable car sinon erreur : "Smart cast to 'EventAddUIState.Error' is impossible, because 'uiStateAddEventResult' is a property that has open or custom getter", => Kotlin ne peut pas garantir que la valeur de la propriété n'a pas changé entre les 2 appels

        EventAddStateComposable(
            modifier = Modifier
                .padding(contentPadding),
            currentStateUiStateAddEventResultP = currentStateUiStateAddEventResult,
            uiStateCurrentEventP = uiStateCurrentEvent,
            uiStateErrorP = uiStateError,
            addEventP = {
                viewModel.addEvent()
            },
            onActionP = viewModel::onAction,
            formIsCompleteP = viewModel::formIsComplete,
            onBackClick = onBackClick
        )

    }



}

@Composable
fun EventAddStateComposable(
    modifier: Modifier = Modifier,
    currentStateUiStateAddEventResultP: EventAddUIState?,
    addEventP: () -> Unit,
    onBackClick: () -> Unit,
    uiStateCurrentEventP: Event,
    uiStateErrorP: FormErrorAddEvent?,
    onActionP: (FormDataAddEvent) -> Unit,
    formIsCompleteP: () -> Boolean,
) {

    when (currentStateUiStateAddEventResultP){

        // Erreur
        is EventAddUIState.Error -> {
            val sError = currentStateUiStateAddEventResultP.sError ?: stringResource(R.string.unknown_error)
            ErrorComposable(
                modifier = modifier,
                sErrorMessage = sError,
                onClickRetryP = addEventP
            )
        }

        // Ajout en cours
        EventAddUIState.IsLoading -> {
            LoadingComposable(modifier = modifier)
        }

        EventAddUIState.Success -> {
            onBackClick() // Retour à la liste d'évènement
        }

        // Formulaire de saisie
        null -> {

            Column(
                modifier = modifier
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
                    value = uiStateCurrentEventP.sTitle,
                    textStyle = MaterialTheme.typography.labelLarge,
                    isError = (uiStateErrorP is FormErrorAddEvent.TitleError),
                    onValueChange =  {
                        onActionP(FormDataAddEvent.TitleChanged(it))
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
                if (uiStateErrorP is FormErrorAddEvent.TitleError) {
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
                    value = uiStateCurrentEventP.sDescription,
                    textStyle = MaterialTheme.typography.labelLarge,
                    isError = (uiStateErrorP is FormErrorAddEvent.DescriptionError),
                    onValueChange =  {
                        onActionP(FormDataAddEvent.DescriptionChanged(it))
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
                if (uiStateErrorP is FormErrorAddEvent.DescriptionError) {
                    Text(
                        text = stringResource(id = R.string.mandatorydescription),
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                ComposableDateTime(
                    datetimeValueInMs = uiStateCurrentEventP.lDatetime,
                    onValueChangeDateTimeChanged = {
                        onActionP(FormDataAddEvent.DateTimeChanged(it))
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Saisie de l'adresse
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ColorCardAndInput),
                    value = uiStateCurrentEventP.sAdress,
                    textStyle = MaterialTheme.typography.labelLarge,
                    isError = (uiStateErrorP is FormErrorAddEvent.AddressError),
                    onValueChange =  {
                        onActionP(FormDataAddEvent.AdressChanged(it))
                    },
                    label = {
                        Text(
                            text = stringResource(id = R.string.address),
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.enterfulladdress),
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
                if (uiStateErrorP is FormErrorAddEvent.AddressError) {
                    Text(
                        text = uiStateErrorP.errorAddress?: stringResource(
                            R.string.unknown_error
                        ),/*stringResource(id = R.string.mandatoryaddress)*/
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                PhotoSelectorComposable(
                    modifier = Modifier.weight(1f), // Toute la place restante
                    sURLValueP = uiStateCurrentEventP.sURLEventPicture,
                    onPhotoChanged = {
                        onActionP(FormDataAddEvent.PhotoChanged(it))
                    },
                    uiStateError = uiStateErrorP,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = {
                        addEventP()
                    },
                    // Bouton actif si pas d'erreur et formulaire non vide
                    enabled = ( formIsCompleteP() ) ,
                    colors = MyButtonStyle.buttonColors()  // Couleurs de grisage factorisées dans le Theme

                ){
                    Text(
                        text = stringResource(id = R.string.validate),
                    )
                }

            }

        }
    }

}

@Composable
fun PhotoSelectorComposable(
    modifier: Modifier,
    sURLValueP: String,
    onPhotoChanged: (String) -> Unit,
    uiStateError: FormErrorAddEvent?,
)
{

    val context = LocalContext.current

    // Box utile pour centrer horizontalement le contenu
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = 30.dp
            ),
        contentAlignment = Alignment.TopCenter
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){

            //1 er élément la ligne avec les 2 boutons

            Row{

                val nIconSize = 52

                // Prise d'une photo avec l'appareil
                // https://medium.com/@dheerubhadoria/capturing-images-from-camera-in-android-with-jetpack-compose-a-step-by-step-guide-64cd7f52e5de

                // TODo JG : Protéger ce code pour qu'il soit pas exécuté à chaque recomposition
                val file = context.createImageFile()
                val uri = FileProvider.getUriForFile(
                    Objects.requireNonNull(context),
                    BuildConfig.APPLICATION_ID + ".provider", file
                )

                //var capturedImageUri by remember { mutableStateOf<Uri>(Uri.EMPTY) }

                // Callback de la prise de photo
                val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
                    //capturedImageUri = uri
                    onPhotoChanged(uri.toString())
                }

                // Callback de la demande de permission
                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { bPermissionGranted ->
                    if (bPermissionGranted) {
                        Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
                        cameraLauncher.launch(uri)
                    } else {
                        Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
                    }
                }

                IconButton(
                    modifier = Modifier
                        .size(nIconSize.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(ColorTitleWhite),
                    onClick = {

                        // Vérifie la permission
                        val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)

                        // Si permission accordée
                        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                            // Lance la prise de photo
                            cameraLauncher.launch(uri)
                        } else {
                            // Demande la permission
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }

                    }

                ) {
                    Icon(
                        modifier = Modifier.padding(16.dp),
                        painter = painterResource(R.drawable.baseline_photo_camera_24),
                        contentDescription = stringResource(id = R.string.takeapicture),
                        tint = ColorCardAndInput
                    )
                }


                Spacer(modifier = Modifier.width(16.dp))

                // Récupération d'un image dans la gallerie photo

                // Callback du mediaPicker (Android 11 et supérieur)
                val pickImageLauncherNew = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                    onPhotoChanged(uri.toString())
                }

                // Callback du image launcher (Android 10 et inférieur)
                val pickImageLauncherOld = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                    onPhotoChanged(uri.toString())
                }

                IconButton(
                    modifier = Modifier
                        .size(nIconSize.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primary)
                    ,
                    onClick = {
                        // Lancement des images Pickers
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // Android 11+
                            // Lancement du media picker (que les images)
                            pickImageLauncherNew.launch(PickVisualMediaRequest(ImageOnly))
                        } else { // Versions inférieures
                            pickImageLauncherOld.launch("image/*")
                        }
                    }

                ) {
                    Icon(
                        modifier = Modifier.padding(16.dp),
                        painter = painterResource(R.drawable.baseline_attach_file_24),
                        contentDescription = stringResource(id = R.string.selectphotoingallery),
                        tint = ColorTitleWhite
                    )
                }

            }

            if (uiStateError is FormErrorAddEvent.PhotoError) {
                Text(
                    text = stringResource(id = R.string.mandatoryphoto),
                    color = MaterialTheme.colorScheme.error,
                )
            }


            //2 ème élément la photo si elle existe

            if (sURLValueP.isNotEmpty()){
                Image(
                    painter = rememberAsyncImagePainter(sURLValueP), //  l'image est chargée et affichée à l'aide de Coil
                    contentDescription = null,
                    modifier = Modifier.size(200.dp),
                    contentScale = ContentScale.Crop
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
                .clickable {
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
                .clickable {
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

// TODO Denis : Pourquoi je n'ai pas le style dans les previews ?

@Preview("Event Add Form")
@Composable
fun EventListComposablePreview() {

    val listFakeEvent = EventFakeAPI.initFakeEvents()
    val currentEvent = listFakeEvent[0]

    P15EventoriasTheme {

        EventAddStateComposable(
            currentStateUiStateAddEventResultP = null, // Formulaire en cours de saisie
            addEventP = {},
            onBackClick = {},
            uiStateCurrentEventP = currentEvent,
            uiStateErrorP = null,
            onActionP = {},
            formIsCompleteP = { true  },
        )
    }

}


@Preview("Event Add Error")
@Composable
fun EventListComposableErrorPreview() {

    val listFakeEvent = EventFakeAPI.initFakeEvents()
    val currentEvent = listFakeEvent[0]

    P15EventoriasTheme {

        EventAddStateComposable(
            currentStateUiStateAddEventResultP = EventAddUIState.Error("Erreur lors de l'ajout"),
            addEventP = {},
            onBackClick = {},
            uiStateCurrentEventP = currentEvent,
            uiStateErrorP = null,
            onActionP = {},
            formIsCompleteP = { true  },
        )
    }

}


@Preview("Event Add Loading")
@Composable
fun EventListComposableLoadingPreview() {

    val listFakeEvent = EventFakeAPI.initFakeEvents()
    val currentEvent = listFakeEvent[0]

    P15EventoriasTheme {

        EventAddStateComposable(
            currentStateUiStateAddEventResultP = EventAddUIState.IsLoading,
            addEventP = {},
            onBackClick = {},
            uiStateCurrentEventP = currentEvent,
            uiStateErrorP = null,
            onActionP = {},
            formIsCompleteP = { true  },
        )
    }

}
