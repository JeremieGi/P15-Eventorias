package com.openclassrooms.p15_eventorias

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
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

    @Test
    fun detailsfirstevent() = runTest {

        val fakeListEvent = EventFakeAPI.initFakeEvents()

        // Clic sur le 1er élément
        composeTestRule.onNodeWithText(fakeListEvent[0].sTitle)
            .assertIsDisplayed()
            .performClick()

        composeTestRule.awaitIdle()

        // Détection du contenu de l'évènement
        composeTestRule.onNodeWithText(fakeListEvent[0].sDescription)
            .assertIsDisplayed()

        // Détection du contenu de l'évènement
        composeTestRule.onNodeWithText(fakeListEvent[0].sAddress)
            .assertIsDisplayed()

        // Titre affiché dans l'app bar
        composeTestRule.onNodeWithText(fakeListEvent[0].sTitle)
            .assertIsDisplayed()

    }


}