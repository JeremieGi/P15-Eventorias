package com.openclassrooms.p15_eventorias

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassrooms.p15_eventorias.repository.event.EventFakeAPI
import com.openclassrooms.p15_eventorias.ui.MainActivity
import org.junit.Rule
import org.junit.runner.RunWith
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MainTest {

    // https://developer.android.com/codelabs/jetpack-compose-testing?hl=fr#2


    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

//    // Lancement de l'activité principale en début de test
//    @get:Rule(order = 2)
//    var activityTest = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun add_classic() = runTest { // TODo Denis : Debug du test impossible (mainteant ok)

        // Détection du titre
        val sTitleEventList = composeTestRule.activity.getString(R.string.event_list)
        composeTestRule.onNodeWithText(sTitleEventList)
            .assertIsDisplayed()

        // Vérification du nombre d'éléments
        val fakeListEvent = EventFakeAPI.initFakeEvents()
        composeTestRule.onAllNodesWithTag("event_item")
            .assertCountEquals(fakeListEvent.size)

        // Clique sur le bouton '+'
        val sContentDescButton = composeTestRule.activity.getString(R.string.addEvent)
        composeTestRule.onNodeWithContentDescription(sContentDescButton).performClick()

        composeTestRule.awaitIdle()

        // Détection du titre de la fenêtre d'ouverture
        val sTitleAdd = composeTestRule.activity.getString(R.string.event_creation)
        composeTestRule.onNodeWithText(sTitleAdd)
            .assertIsDisplayed()

        val sTitleVal = "Event Title Test"
        val sDescriptionVal = "Event description Test \n on 2 lines"
        val sDateVal = "12/31/2030"
        val sTimeVal = "10:20"
        val sAdress = "Montpellier"

        // Titre
        val sLabelTitle = composeTestRule.activity.getString(R.string.title)
        composeTestRule.onNodeWithText(sLabelTitle)
            .performTextInput(sTitleVal)

        // Description
        val sLabelDescription = composeTestRule.activity.getString(R.string.description)
        composeTestRule.onNodeWithText(sLabelDescription)
            .performTextInput(sDescriptionVal)

        // Date
        val sLabelDate = composeTestRule.activity.getString(R.string.date)

        // TODO Denis : comment ouvrir le picker (ou injecter directement une valeur dans le champ ?)
        // le picker n'est pas en compose (par d'élément dans le layout inspector
        composeTestRule.onNodeWithText(sLabelDate).performClick()

//        composeTestRule.onNodeWithText(sLabelDate)
//            .performTextInput(sDateVal)
//
//        // Heure
//        val sLabelTime = composeTestRule.activity.getString(R.string.time)
//        composeTestRule.onNodeWithText(sLabelTime)
//            .performTextInput(sTimeVal)
//
//        // Adresse
//        val sLabelAddress = composeTestRule.activity.getString(R.string.address)
//        composeTestRule.onNodeWithText(sLabelAddress)
//            .performTextInput(sAdress)





        // Clique sur le bouton 'Validate'
        val sAddButton = composeTestRule.activity.getString(R.string.validate)
        composeTestRule.onNodeWithText(sAddButton).performClick()

    }


    @Test
    fun navigation_bottom_bar() = runTest {

        // Détection du titre
        val sTitleEventList = composeTestRule.activity.getString(R.string.event_list)
        composeTestRule.onNodeWithText(sTitleEventList)
            .assertIsDisplayed()

//        // Clique sur le bouton 'Events'
//        val sEventButton = composeTestRule.activity.getString(R.string.events)
//        composeTestRule.onNodeWithText(sEventButton).performClick()
//
//        //composeTestRule.awaitIdle()
//
//        // il ne se passe rien
//        composeTestRule.onNodeWithText(sTitleEventList)
//            .assertIsDisplayed()


        // Clique sur le bouton 'Profile'
        val sProfileButton = composeTestRule.activity.getString(R.string.profile)
        composeTestRule
            .onNodeWithText(sProfileButton).performClick()
            .assertIsDisplayed()


        composeTestRule.awaitIdle()

//        // La fenêtre du profil est bien ouverte
        val sTitleUserProfile = composeTestRule.activity.getString(R.string.userprofile)
//        composeTestRule.onNodeWithText(sTitleUserProfile)
//            .assertIsDisplayed()

//        composeTestRule.waitUntil(timeoutMillis = 5000) {
//            // Condition d'attente
//            false
//        }
//
        composeTestRule.onNodeWithText(sTitleUserProfile).assertExists()




    }

}