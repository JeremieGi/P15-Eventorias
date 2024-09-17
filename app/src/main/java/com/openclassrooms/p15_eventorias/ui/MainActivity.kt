package com.openclassrooms.p15_eventorias.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.p15_eventorias.ui.screen.eventItem.EventItemScreen
import com.openclassrooms.p15_eventorias.ui.screen.eventAdd.EventAddScreen
import com.openclassrooms.p15_eventorias.ui.screen.launch.LaunchScreen
import com.openclassrooms.p15_eventorias.ui.screen.eventsList.EventsListScreen
import com.openclassrooms.p15_eventorias.ui.screen.userProfile.UserProfileScreen
import com.openclassrooms.p15_eventorias.ui.ui.theme.P15EventoriasTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

//            P15EventoriasTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//            }

            // On appelle le NavController
            val navController = rememberNavController()

            P15EventoriasTheme {
                NavGraph(
                    navController = navController
                )
            }


        }

    }

}


@Composable
fun NavGraph(
    navController: NavHostController
) {

    NavHost(
        navController = navController,
        startDestination = Screen.Launch.route   // Point d'entrée de l'application
    ) {

        // Fenêtre de lancement (Login ou directement liste des évènènements)
        composable(route = Screen.Launch.route) {

            LaunchScreen(
                onEventClickP = { event ->
                    //Le clic sur un Post, ouvre le Post
                    navController.navigate(Screen.EventItem.createRoute(event.id))
                },
                onClickAddP = {
                    navController.navigate(Screen.EventAdd.route)
                },
                onClickProfileP = {
                    navController.navigate(Screen.UserProfile.route)
                }
            )

        }


        // Liste des évènements // TODO JG : Vérifier qu'on utilise bien ce chemin (on lance jamais ,
        //                    onClickAddPP = {
        //                        navController.navigate(Screen.EventAdd.route)
        //                    } pour le moment)
        composable(Screen.EventsList.route) {

                EventsListScreen(
                    onEventClickP = { event ->
                        //Le clic sur un Post, ouvre le Post
                        navController.navigate(Screen.EventItem.createRoute(event.id))
                    },
                    onClickAddP = {
                        navController.navigate(Screen.EventAdd.route)
                    },
                    onClickProfileP = {
                        navController.navigate(Screen.UserProfile.route)
                    }
                )


        }

        // Fenêtre d'un evènement
        composable(Screen.EventItem.route) { backStackEntry -> // BackStackEntry ici permet de récupérer les paramètres

            val eventId = backStackEntry.arguments?.getString(Screen.CTE_PARAM_ID_EVENT)
                ?: error("Missing required argument eventId") // pour lever une exception de type IllegalArgumentException avec le message spécifié.

                EventItemScreen(
                    onBackClick = { navController.navigateUp() },
                    eventId = eventId,
                )



        }

        composable(route = Screen.EventAdd.route) {
            EventAddScreen(
                onBackClick = { navController.navigateUp() }
            )
        }

        composable(route = Screen.UserProfile.route) {
            UserProfileScreen(
                onBackClick = { navController.navigateUp() }
            )
        }

    }
}



//// Structuration de toutes les fenêtres de l'application
//// avec application du thème et padding pour ne pas se superposer à la bar Android
//@Composable
//fun StructureComposable(
//    functionComposableParam : @Composable (modifier: Modifier) -> Unit
//){
//
//    Scaffold(
//
//        content = { innerPadding ->
//
//            functionComposableParam(
//                Modifier.padding(innerPadding) // Named arguments in composable function types are deprecated. This will become an error in Kotlin 2.0
//            )
//        }
//
//    )
//
//}

