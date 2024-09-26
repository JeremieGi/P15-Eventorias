package com.openclassrooms.p15_eventorias

import android.widget.DatePicker
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassrooms.p15_eventorias.repository.event.EventFakeAPI
import com.openclassrooms.p15_eventorias.ui.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Test d'ajout d'évènements
 */

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class EventAddTest {

    // https://developer.android.com/codelabs/jetpack-compose-testing?hl=fr#2

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() {
        hiltRule.inject()
    }

    /**
     * Cas basique, d'ajout d'un élément sans erreur
     */
    @Test
    fun add_classic() = runTest { // TODo Denis : Debug du test impossible pendant un temps...

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

        // TODO Denis : comment ouvrir le picker (ou injecter directement une valeur dans le champ ?) + idem pour la photo
        // le picker n'est pas en compose (par d'élément dans le layout inspector
        composeTestRule.onNodeWithText(sLabelDate).performClick() // Ouvre le picker que je ne peux pas piloter

        //composeTestRule.onNodeWithText(sLabelDate).performTextInput("12/31/2024") // Plante car le champ est inactif

        //composeTestRule.onNodeWithText(sLabelDate).performTextReplacement("12/31/2024")

       // composeTestRule.onNodeWithTag("tagPickerDate").performTextInput("12/31/2024")
       // composeTestRule.onNodeWithTag("tagPickerDate").performTextReplacement("12/31/2024")


//        onView(withClassName(Matchers.equalTo<String>(DatePicker::class.java.name))).perform(
//            PickerActions.setDate(2024, 10, 1)
//        )


 //       composeTestRule.onRoot().printToLog("ARBITRARY_LOG_TAG")
        //Actions = [GetTextLayoutResult, SetText, InsertTextAtCursor, SetSelection, PerformImeAction, OnClick, OnLongClick, RequestFocus, SetTextSubstitution, ShowTextSubstitution, ClearTextSubstitution]

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

    // TODO JG : Ecrire les tests aux limites (champs obligatoires manquants)


}