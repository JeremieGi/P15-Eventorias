package com.openclassrooms.p15_eventorias.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.p15_eventorias.ui.EventItemScreen.EventItemScreen
import com.openclassrooms.p15_eventorias.ui.eventAdd.EventAddScreen
import com.openclassrooms.p15_eventorias.ui.launchScreen.LaunchScreen
import com.openclassrooms.p15_eventorias.ui.screen.eventsList.EventsListScreen
import com.openclassrooms.p15_eventorias.ui.ui.theme.P15EventoriasTheme

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

            StructureComposable{ modifier ->
                LaunchScreen(
                    modifier = modifier
                )
            }

        }


        // Liste des évènements
        composable(Screen.EventsList.route) {

            StructureComposable{ modifier ->
                EventsListScreen(
                    //navController = navController,
                    modifier = modifier,
                    onEventClickP = { event ->
                        //Le clic sur un Post, ouvre le Post
                        navController.navigate(Screen.EventItem.createRoute(event.id))
                    }
                )
            }

        }

        // Fenêtre d'un evènement
        composable(Screen.EventItem.route) { backStackEntry -> // BackStackEntry ici permet de récupérer les paramètres

            val eventId = backStackEntry.arguments?.getString(Screen.CTE_PARAM_ID_EVENT)?.toIntOrNull()
                ?: error("Missing required argument eventId") // pour lever une exception de type IllegalArgumentException avec le message spécifié.

            StructureComposable{ modifier ->

                EventItemScreen(
                    modifier = modifier,
                    onBackClick = { navController.navigateUp() },
                    eventId = eventId,
                )

            }

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



// Structuration de toutes les fenêtres de l'application
// avec application du thème et padding pour ne pas se superposer à la bar Android
@Composable
fun StructureComposable(
    functionComposableParam : @Composable (modifier: Modifier) -> Unit
){

    Scaffold(

        content = { innerPadding ->

            functionComposableParam(
                Modifier.padding(innerPadding) // Named arguments in composable function types are deprecated. This will become an error in Kotlin 2.0
            )
        }

    )

}

