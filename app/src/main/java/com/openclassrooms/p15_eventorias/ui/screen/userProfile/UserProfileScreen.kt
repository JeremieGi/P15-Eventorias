package com.openclassrooms.p15_eventorias.ui.screen.userProfile

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.openclassrooms.p15_eventorias.R
import com.openclassrooms.p15_eventorias.model.User
import com.openclassrooms.p15_eventorias.repository.user.UserFakeAPI
import com.openclassrooms.p15_eventorias.ui.BottomBarComposable
import com.openclassrooms.p15_eventorias.ui.ErrorComposable
import com.openclassrooms.p15_eventorias.ui.LoadingComposable
import com.openclassrooms.p15_eventorias.ui.Screen
import com.openclassrooms.p15_eventorias.ui.URLImageAvatarComposable
import com.openclassrooms.p15_eventorias.ui.ui.theme.ColorBackground
import com.openclassrooms.p15_eventorias.ui.ui.theme.ColorCardAndInput
import com.openclassrooms.p15_eventorias.ui.ui.theme.ColorTitleWhite
import com.openclassrooms.p15_eventorias.ui.ui.theme.P15EventoriasTheme
import android.Manifest
import android.app.Activity
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker

@Composable
fun UserProfileScreen(
    //modifier: Modifier = Modifier,
    viewModel: UserProfileViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onClickEventsP: () -> Unit
) {

    // Lecture du user
    val uiStateUser by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiStateUser) {
        //if (uiStateUser !is UserUIState.Success) {
            viewModel.loadCurrentUser()
        //}
    }

    UserProfileStateComposable(
        uiStateUserP = uiStateUser,
        onBackClick = onBackClick,
        onClickEventsP = onClickEventsP,
        loadCurrentUserP = viewModel::loadCurrentUser,
        onChangeNotificationEnableP = viewModel::changeCurrentUserNotificationEnabled,
        onClickSignOutP = viewModel::signOut,
    )


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileStateComposable(
    uiStateUserP: UserUIState,
    onBackClick: () -> Unit,
    onClickEventsP: () -> Unit,
    loadCurrentUserP : () -> Unit,
    onChangeNotificationEnableP : (Boolean) -> Unit,
    onClickSignOutP: (Context) -> Task<Void>,
) {


    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onBackClick()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back),
                            tint = ColorTitleWhite
                        )
                    }
                },
                title = {
                    Text(stringResource(id = R.string.userprofile))
                },
                actions = {

                    if (uiStateUserP is UserUIState.Success){
                        URLImageAvatarComposable(
                            modifier = Modifier
                                .padding(end = Screen.CTE_PADDING_HORIZONTAL_APPLI.dp)
                                .size(48.dp),
                            sURLP = uiStateUserP.user.sURLAvatar
                        )
                    }

                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = ColorBackground,
                )
            )
        },


        content = { contentPadding ->


            when (uiStateUserP) {

                // Chargement
                is UserUIState.IsLoading -> {
                    LoadingComposable(Modifier.padding(contentPadding))
                }

                // Récupération des données avec succès
                is UserUIState.Success -> {

                    val user = uiStateUserP.user

                    UserProfileComposable(
                        modifier = Modifier.padding(contentPadding),
                        userP = user,
                        onChangeNotificationEnableP = onChangeNotificationEnableP,
                        onClickSignOutP = onClickSignOutP,
                        onBackClick = onBackClick
                    )

                }

                // Exception
                is UserUIState.Error -> {

                    val error = uiStateUserP.sError ?: stringResource(
                        R.string.unknown_error
                    )

                    ErrorComposable(
                        modifier=Modifier.padding(contentPadding),
                        sErrorMessage = error,
                        onClickRetryP = { loadCurrentUserP() }
                    )


                }
            }


        },

        bottomBar = {
            BottomBarComposable(
                sActiveScreenP = Screen.CTE_USER_PROFILE_SCREEN,
                onClickEventsP = onClickEventsP,
                onClickProfileP = {}
            )
        }

    )




}

@Composable
fun UserProfileComposable(
    modifier: Modifier = Modifier,
    userP: User,
    onChangeNotificationEnableP : (Boolean) -> Unit,
    onClickSignOutP: (Context) -> Task<Void>,
    onBackClick: () -> Unit,
) {

    val context = LocalContext.current

    Column(
        modifier = modifier
            .padding(
                horizontal = Screen.CTE_PADDING_HORIZONTAL_APPLI.dp,
                vertical = Screen.CTE_PADDING_VERTICAL_APPLI.dp
            )
            .verticalScroll(rememberScrollState()) // permet de rendre la colonne défilable verticalement si le contenu dépasse la taille de l'écran.,
        //verticalArrangement = Arrangement.spacedBy(16.dp), // Espacement entre les éléments
    ){

        // Saisie du nom
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .background(ColorCardAndInput),
            value = userP.sName,
            textStyle = MaterialTheme.typography.labelLarge,
            label = {
                Text(
                    text = stringResource(id = R.string.name),
                    style = MaterialTheme.typography.labelMedium
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = true,
            enabled = false,
            onValueChange = {}, // Obligation d'inclure onValueChange, même si le champ est désactivé
        )


        Spacer(modifier = Modifier.height(16.dp))

        // Saisie du mail
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .background(ColorCardAndInput),
            value = userP.sEmail,
            textStyle = MaterialTheme.typography.labelLarge,
            label = {
                Text(
                    text = stringResource(id = R.string.email),
                    style = MaterialTheme.typography.labelMedium
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = true,
            enabled = false,
            onValueChange = {}, // Obligation d'inclure onValueChange, même si le champ est désactivé
        )

        Spacer(modifier = Modifier.height(16.dp))

        SwitchNotificationComposable(
            bNotificationEnabledP = userP.bNotificationEnabled,
            onChangeNotificationEnableP = onChangeNotificationEnableP
        )


        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {

            onClickSignOutP(context)
                .addOnCompleteListener {
                    // méthode qui permet de spécifier une action à exécuter une fois que l'opération signOut() est terminée.

                    Toast
                        .makeText(context, context.getString(R.string.deconnexion_ok), Toast.LENGTH_SHORT)
                        .show()

                    onBackClick()

                }
                .addOnFailureListener { exception ->
                    // Erreur lors de la déconnexion

                    val errorMessage = exception.localizedMessage ?: context.getString(R.string.unknown_error)

                    Toast
                        .makeText(context, errorMessage, Toast.LENGTH_SHORT)
                        .show()

                }

        }) {
            Text(stringResource(id = R.string.SignOut))
        }



    }



}

@Composable
fun SwitchNotificationComposable(
    bNotificationEnabledP : Boolean,
    onChangeNotificationEnableP : (Boolean) -> Unit,
) {


    val context = LocalContext.current

    // Pour ne pas redessiner toute la vue, j'utilise un remember
    var varIsChecked by rememberSaveable { mutableStateOf(bNotificationEnabledP) }

    // Launcher pour demander la permission de notification (uniquement à partir d'Android 13)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // La permission a été accordée
            // inscription aux notifications
            onChangeNotificationEnableP(true)
            varIsChecked = true
        } else {
            // La permission a été refusée
            onChangeNotificationEnableP(false)
            varIsChecked = false
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ){


        Switch(
            modifier = Modifier
                .semantics {
                    this.contentDescription = if (varIsChecked) {
                        context.getString(R.string.tap_to_turn_off_notifications)
                    } else {
                        context.getString(R.string.tap_to_enable_notifications)
                    }
                },
            checked = varIsChecked,
            onCheckedChange = {

                // On est en train d'activer les notification
                val bActivation = !varIsChecked

                if (bActivation){

                    // Vérifie si on est sur Android 13 ou supérieur
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                        // Vérifie si la permission n'a pas déjà été accordée
//                        val isNotificationPermissionGranted = ContextCompat.checkSelfPermission(
//                            context, Manifest.permission.POST_NOTIFICATIONS
//                        ) == PermissionChecker.PERMISSION_GRANTED
//
//                        if (!isNotificationPermissionGranted) {
//
//                            val activity = context as? Activity
//                            activity?.let { activityNotNull ->
//                                if (ActivityCompat.shouldShowRequestPermissionRationale(activityNotNull, Manifest.permission.POST_NOTIFICATIONS)) {
//                                    // La permission a été refusée précédemment
//                                    Toast.makeText(context,
//                                        context.getString(R.string.permission_already_denied), Toast.LENGTH_SHORT).show()
//                                }
//                                else {
//                                    // La permission n'a jamais été demandée ou a été refusée sans "Ne plus demander"
//                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//                                }
//
//                            }
//
//                        }
//                        else{
//                            onChangeNotificationEnableP(it)
//                            varIsChecked = it
//                        }

                        when (ContextCompat.checkSelfPermission(
                            context, Manifest.permission.POST_NOTIFICATIONS
                        )) {
                            PermissionChecker.PERMISSION_GRANTED -> {
                                // La permission est accordée
                                onChangeNotificationEnableP(it)
                                varIsChecked = it
                            }

                            PermissionChecker.PERMISSION_DENIED -> {

                                val activity = context as? Activity
                                activity?.let { activityNotNull ->
                                    // Vérifie si l'utilisateur a refusé la permission et si elle est toujours refusée
                                    if (ActivityCompat.shouldShowRequestPermissionRationale(activityNotNull, Manifest.permission.POST_NOTIFICATIONS)) {
                                        // L'utilisateur a refusé la permission, mais sans cocher "Ne plus demander"
                                        Toast.makeText(context, context.getString(R.string.permission_already_denied), Toast.LENGTH_SHORT).show()
                                    } else {
                                        // Soit la permission n'a jamais été demandée, soit elle a été désactivée dans les Paramètres
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                }


                            }

                            PermissionChecker.PERMISSION_DENIED_APP_OP -> {
                                // la permission a été refusée via les Paramètres
                                Toast.makeText(context, context.getString(R.string.permission_already_denied), Toast.LENGTH_SHORT).show()
                            }
                        }

                    }
                    else{
                        // Pas de permission pour les versions d'Android < 13
                        onChangeNotificationEnableP(it)
                        varIsChecked = it
                    }

                }
                else{
                    // cas de la désactivation
                    onChangeNotificationEnableP(it)
                    varIsChecked = it
                }


            }
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(id = R.string.notifications),
            style = MaterialTheme.typography.bodyMedium
        )

    }

}

@Preview("UserProfile - Success preview")
@Composable
fun UserProfileStateComposableSuccessPreview() {

    val user = UserFakeAPI.initFakeCurrentUser()
    val uiStateSuccess = UserUIState.Success(
        user = user
    )

    val mockContext : (Context) -> Task<Void> = { _ ->
        // Simulate a successful sign-out task
        Tasks.forResult(null)
    }

    P15EventoriasTheme {
        UserProfileStateComposable(
            uiStateUserP = uiStateSuccess,
            onBackClick = {},
            onClickEventsP = {},
            loadCurrentUserP= {},
            onChangeNotificationEnableP = {},
            onClickSignOutP = mockContext
        )
    }

}

@Preview("UserProfile - Loading preview")
@Composable
fun UserProfileStateComposableLoadingPreview() {

    val mockContext : (Context) -> Task<Void> = { _ ->
        // Simulate a successful sign-out task
        Tasks.forResult(null)
    }

    P15EventoriasTheme {
        UserProfileStateComposable(
            uiStateUserP = UserUIState.IsLoading,
            onBackClick = {},
            onClickEventsP = {},
            loadCurrentUserP= {},
            onChangeNotificationEnableP = {},
            onClickSignOutP = mockContext
        )
    }

}
@Preview("UserProfile - Error preview")
@Composable
fun UserProfileStateComposableErrorPreview() {

    val mockContext : (Context) -> Task<Void> = { _ ->
        // Simulate a successful sign-out task
        Tasks.forResult(null)
    }

    P15EventoriasTheme {
        UserProfileStateComposable(
            uiStateUserP = UserUIState.Error("Erreur de test de la preview"),
            onBackClick = {},
            onClickEventsP = {},
            loadCurrentUserP= {},
            onChangeNotificationEnableP = {},
            onClickSignOutP = mockContext
        )
    }

}
