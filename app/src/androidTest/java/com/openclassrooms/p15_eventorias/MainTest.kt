package com.openclassrooms.p15_eventorias

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassrooms.p15_eventorias.model.Event
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

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val fakeListEvent = EventFakeAPI.initFakeEvents()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun event_search() = runTest {

    }

    @Test
    fun event_order() = runTest {

        // Attend tant que la liste d'évènement n'est pas chargée complétement
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("event_item").fetchSemanticsNodes().size == fakeListEvent.size
        }

        // Clique sur le bouton de tri de la bottom bar => tri par Date
        val sContentDescOrderIcon = composeTestRule.activity.getString(R.string.sortByDate)
        composeTestRule.onNodeWithContentDescription(sContentDescOrderIcon)
            .performClick()

        composeTestRule.awaitIdle()

        val expectedEventListAsc = fakeListEvent.sortedBy { it.lDatetime }
        assertLazyColumn(expectedEventListAsc)

        // Re-Clique sur le bouton de tri de la bottom bar => tri par Date dans l'autre sens
        composeTestRule.onNodeWithContentDescription(sContentDescOrderIcon)
            .performClick()

        composeTestRule.awaitIdle()

        val expectedEventListDesc = fakeListEvent.sortedByDescending { it.lDatetime }
        assertLazyColumn(expectedEventListDesc)

    }


    /**
     * Fonction qui permet de vérifier le contenu exact d'un LasyColumn
     */
    private fun assertLazyColumn(expectedEventListP: List<Event>) {

        // Récupérer tous les nœuds avec le testTag
        val nodes = composeTestRule.onAllNodesWithTag("event_item").fetchSemanticsNodes()

        // Parcours de chaque noeud
        nodes.forEachIndexed { index, node ->

            // Récupération du texte semantique
            val annotatedString = node.config.getOrNull(SemanticsProperties.Text)
            val sSemanticText = annotatedString?.toString() ?: ""

            val expectedTitle = expectedEventListP[index].sTitle

            // Vérifie si le texte sémantique contient le titre attendu
            assert(sSemanticText.contains(expectedTitle)) {
                "Cet événement devrait être $expectedTitle et c'est $sSemanticText"
            }
        }

    }


    @Test
    fun navigation_bottom_bar() = runTest {

        // Attend tant que la liste d'évènement n'est pas chargée complétement
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("event_item").fetchSemanticsNodes().size == fakeListEvent.size
        }

        // Détection du titre 'Event List"
        val sTitleEventList = composeTestRule.activity.getString(R.string.event_list)
        composeTestRule.onNodeWithText(sTitleEventList)
            .assertIsDisplayed()

        // Clique sur le bouton 'Events' de la bottom bar
        composeTestRule.onNodeWithTag("iconEvent")
            .performClick()

        // Attente des redessins
        composeTestRule.awaitIdle()

        // il ne se passe rien
        composeTestRule.onNodeWithText(sTitleEventList).assertIsDisplayed()

        // Clique sur le bouton 'Profile'
        composeTestRule
            .onNodeWithTag("iconProfile").performClick()

        composeTestRule.awaitIdle()

        // La fenêtre du profil est bien ouverte
        val sTitleUserProfile = composeTestRule.activity.getString(R.string.userprofile)
        composeTestRule.onNodeWithText(sTitleUserProfile).assertExists()

        // Clique sur le bouton 'Profile'
        composeTestRule
            .onNodeWithTag("iconProfile").performClick()

        composeTestRule.awaitIdle()

        // il ne se passe rien
        composeTestRule.onNodeWithText(sTitleUserProfile).assertExists()

        // Clique sur le bouton 'Events' de la bottom bar
        composeTestRule.onNodeWithTag("iconEvent")
            .performClick()

        composeTestRule.awaitIdle()

        // Retour à l'écran de liste des évènements
        composeTestRule.onNodeWithText(sTitleEventList).assertIsDisplayed()


    }

}