package com.openclassrooms.p15_eventorias.ui

sealed class Screen(
    val route: String
) {
    data object Launch : Screen("launchScreen")

    data object EventsList : Screen("eventsList")

    data object EventItem : Screen("eventItem/{$CTE_PARAM_ID_EVENT}"){
        // Configurer la Route avec des Arguments
        fun createRoute(eventId: String) = "eventItem/$eventId"
    }

    data object EventAdd : Screen("eventAdd")

    data object UserProfile : Screen("userProfile")

    companion object {

        const val CTE_PARAM_ID_EVENT: String = "eventID"
        const val CTE_PADDING_HORIZONTAL_APPLI = 26
        const val CTE_PADDING_VERTICAL_APPLI = 5
    }



}