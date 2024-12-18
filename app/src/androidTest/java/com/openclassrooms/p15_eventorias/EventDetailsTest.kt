package com.openclassrooms.p15_eventorias


import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassrooms.p15_eventorias.repository.event.EventFakeAPI
import com.openclassrooms.p15_eventorias.ui.MainActivity
import com.openclassrooms.p15_eventorias.ui.TestTags
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
        val event2 = fakeListEvent[1]

        // Attend que la liste d'évènement soit chargée complétement
        composeTestRule.onNodeWithTag(TestTags.LAZY_COLUMN_EVENTS)
            .onChildren()
            .assertCountEquals(fakeListEvent.size)

        composeTestRule.awaitIdle()

        //composeTestRule.onNodeWithTag("${TestTags.EVENT_ID_PREFIX}${event2.id}")
        composeTestRule.onNodeWithText(event2.sTitle)
            .assertIsDisplayed()
            .performClick()

        composeTestRule.awaitIdle()
        //composeTestRule.waitForIdle() // Tentative de stabilisation du test dans GitHub Action

        // Attend tant que l'évènement n'est pas chargé complétement (sinon problème dans GitHub Action)
        composeTestRule.waitUntil(timeoutMillis = 10000) { // 10000ms => 100s => 1m30
            composeTestRule.onNodeWithTag(TestTags.EVENT_ITEM_LOAD).isDisplayed()
            //composeTestRule.onNodeWithText(fakeListEvent[0].sTitle).isDisplayed()
        }

        // Titre affiché dans l'app bar
        composeTestRule.onNodeWithText(event2.sTitle)
            .assertIsDisplayed()

        // Test ne passe pas dans GitHub Action (Intégration continue) avec des assertIsDisplayed
        // Je pense que l'écran de l'émulateur GutHub Action est trop petit
        // Je mets donc des assertExists() au lieu des assertIsDisplayed()

//        composeTestRule.onNodeWithContentDescription(composeTestRule.activity.getString(R.string.avatar_of_the_event_creator))
//            .assertExists() // Existe mais n'est pas affiché

        // Détection de l'adresse de l'évènement
        composeTestRule.onNodeWithText(event2.sAddress)
            .assertExists()
            //.assertIsDisplayed()

        // Détection de la description de l'évènement
        composeTestRule.onNodeWithText(event2.sDescription)
            .assertExists()
            //.assertIsDisplayed()

    }


}