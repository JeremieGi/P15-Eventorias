package com.openclassrooms.p15_eventorias.ui.screen.userProfile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: UserProfileViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onClickEventsP: () -> Unit
) {

    // Lecture du user
    val uiStateUser by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadCurrentUser()
    }

    when (uiStateUser) {

        // Chargement
        is UserUIState.IsLoading -> {
            LoadingComposable(modifier)
        }

        // Récupération des données avec succès
        is UserUIState.Success -> {

            val user = (uiStateUser as UserUIState.Success).user

            UserProfileComposable(
                userP = user,
                onBackClick = onBackClick,
                onClickEventsP = onClickEventsP
            )

        }

        // Exception
        is UserUIState.Error -> {

            val error = (uiStateUser as UserUIState.Error).sError ?: stringResource(
                R.string.unknown_error
            )

            ErrorComposable(
                modifier=modifier,
                sErrorMessage = error,
                onClickRetryP = { viewModel.loadCurrentUser() }
            )


        }
    }





}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileComposable(
    modifier: Modifier = Modifier,
    userP: User,
    onBackClick: () -> Unit,
    onClickEventsP: () -> Unit
) {

    // TODO Denis : Voir la bonne stratégie :
    // Scaffold dans le composable stateLess ? => mais çà fait que le sablier s'affiche pas beau
    // Avant ce composable : Comment mettre l'avatar dans la top bar ?

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
                    URLImageAvatarComposable(
                        modifier = Modifier
                            .size(48.dp)
                            .padding(end = Screen.CTE_PADDING_HORIZONTAL_APPLI.dp),
                        sURLP = userP.sURLAvatar
                    )
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = ColorBackground,
                )
            )
        },


        content = { contentPadding ->


            Column(
                modifier = modifier
                    .padding(contentPadding)
                    .padding(
                        horizontal = Screen.CTE_PADDING_HORIZONTAL_APPLI.dp,
                        vertical = Screen.CTE_PADDING_VERTICAL_APPLI.dp
                    ),
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


@Preview("BottomBar")
@Composable
fun UserProfileComposablePreview() {

    val user = UserFakeAPI.initFakeCurrentUser()

    P15EventoriasTheme {
        UserProfileComposable(
            userP = user,
            onBackClick = {},
            onClickEventsP = {}
        )
    }
}
