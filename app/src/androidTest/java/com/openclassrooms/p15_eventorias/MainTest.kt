package com.openclassrooms.p15_eventorias

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassrooms.p15_eventorias.repository.event.EventFakeAPI
import com.openclassrooms.p15_eventorias.ui.MainActivity
import org.junit.Rule
import org.junit.runner.RunWith
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
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
    fun add_classic() { // TODo Denis : Debug du test impossible

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

        // Détection du titre de la fenêtre d'ouverture
        val sTitleAdd = composeTestRule.activity.getString(R.string.event_creation)
        composeTestRule.onNodeWithText(sTitleAdd)
            .assertIsDisplayed()



    }



}