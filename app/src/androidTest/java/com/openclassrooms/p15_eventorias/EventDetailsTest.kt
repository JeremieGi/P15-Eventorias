package com.openclassrooms.p15_eventorias

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassrooms.p15_eventorias.repository.event.EventFakeAPI
import com.openclassrooms.p15_eventorias.ui.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests concernant la fenêtre de détails d'un évènement
 */

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class EventDetailsTest {


    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    // Lancement de l'activité principale en début de test
    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() {
        hiltRule.inject()
    }

    /**
     * Clique sur le 1er élément et vérifie les données dans l'écran ouvert
     */
    @Test
    fun detailsfirstevent() = runTest {

        val fakeListEvent = EventFakeAPI.initFakeEvents()

        // Attend tant que la liste d'évènement n'est pas chargée complétement
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("event_item").fetchSemanticsNodes().size == fakeListEvent.size
        }

        // Clic sur le 1er élément
        composeTestRule.onNodeWithText(fakeListEvent[0].sTitle)
            .assertIsDisplayed()
            .performClick()

        composeTestRule.awaitIdle()
        //composeTestRule.waitForIdle() // Tentative de stabilisation du test dans GitHub Action

        // Attend tant que l'évènement n'est pas chargé complétement (sinon problème dans GitHub Action)
        composeTestRule.waitUntil(timeoutMillis = 10000) { // 10000ms => 100s => 1m30
            //composeTestRule.onNodeWithTag("tagEventLoad").isDisplayed()
            composeTestRule.onNodeWithText(fakeListEvent[0].sTitle).isDisplayed()
        }


        // Détection de la description de l'évènement
        composeTestRule.onNodeWithText(fakeListEvent[0].sDescription)
            .assertIsDisplayed()

        // Détection de l'adresse de l'évènement
        composeTestRule.onNodeWithText(fakeListEvent[0].sAddress)
            .assertIsDisplayed()

        // Titre affiché dans l'app bar
        composeTestRule.onNodeWithText(fakeListEvent[0].sTitle)
            .assertIsDisplayed()

    }


}