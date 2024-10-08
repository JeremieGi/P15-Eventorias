package com.openclassrooms.p15_eventorias


import android.widget.DatePicker
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassrooms.p15_eventorias.repository.event.EventFakeAPI
import com.openclassrooms.p15_eventorias.ui.MainActivity
import com.openclassrooms.p15_eventorias.ui.TestTags
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Calendar



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

        TestEnvironment.isTesting = true
    }

    /**
     * Cas basique, d'ajout d'un élément sans erreur
     */
    @Test
    fun add_classic() = runTest {

        composeTestRule.awaitIdle()

        // Détection du titre
        val sTitleEventList = composeTestRule.activity.getString(R.string.event_list)
        composeTestRule.onNodeWithText(sTitleEventList)
            .assertIsDisplayed()

        // Vérification du nombre d'éléments
        val fakeListEvent = EventFakeAPI.initFakeEvents()

        composeTestRule.onNodeWithTag(TestTags.LAZY_COLUMN_EVENTS)
            .onChildren()
            .assertCountEquals(fakeListEvent.size)
            //.printToLog("lazyColumnEvents") // printToLog permet d'afficher les informations d'accessibilité d'un SemanticsNode (ou de plusieurs nœuds) dans les logs. Cela peut être très utile pour déboguer les tests Compose, car cela te permet de voir les attributs sémantiques (par exemple, texte, état d'affichage, clicabilité, etc.) associés à un élément de l'interface utilisateur.

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
        val sAdressVal = "Montpellier"

        // Titre
        val sLabelTitle = composeTestRule.activity.getString(R.string.title)
        composeTestRule.onNodeWithText(sLabelTitle)
            .performTextInput(sTitleVal)

        // Description
        val sLabelDescription = composeTestRule.activity.getString(R.string.description)
        composeTestRule.onNodeWithText(sLabelDescription)
            .performTextInput(sDescriptionVal)

        // Address
        val sLabelAddress = composeTestRule.activity.getString(R.string.address)
        composeTestRule.onNodeWithText(sLabelAddress)
            .performTextInput(sAdressVal)

        // Clic sur le picker de date
        getPicketDate()

        // Photo => déjà présente en mode test

        // Clique sur le bouton 'Validate'
        val sValidateButton = composeTestRule.activity.getString(R.string.validate)
        composeTestRule.onNodeWithText(sValidateButton).performClick()

        composeTestRule.awaitIdle()

        // Retour à la liste d'évènements
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onNodeWithText(sTitleEventList).isDisplayed()
        }

        // Vérifier l'ajout dans la liste d'évènement

        // un élément de plus
        composeTestRule.onNodeWithTag(TestTags.LAZY_COLUMN_EVENTS)
            .onChildren()
            .assertCountEquals(fakeListEvent.size+1)

        // L'évènement doit apparaitre
        composeTestRule.onNodeWithText(sTitleVal)
            .assertIsDisplayed() // TODO Denis : Pas possible de rendre les erreurs plus explicites dans les comptes-rendus ?

    }

    /**
     * Vérification des champs obligatoires
     */
    @Test
    fun addEventLimit() = runTest {

        // Clique sur le bouton '+'
        val sContentDescButton = composeTestRule.activity.getString(R.string.addEvent)
        composeTestRule.onNodeWithContentDescription(sContentDescButton)
            .performClick()

        composeTestRule.awaitIdle()

        // Le bouton valider est grisé
        val sValidateButton = composeTestRule.activity.getString(R.string.validate)
        composeTestRule.onNodeWithText(sValidateButton)
            .assertIsNotEnabled()

        // Saisie du titre
        val sTitleVal = "Event Title Test"
        val sLabelTitle = composeTestRule.activity.getString(R.string.title)
        composeTestRule.onNodeWithText(sLabelTitle)
            .performTextInput(sTitleVal)


        // Erreur Mandatory description
        val sDescriptionError = composeTestRule.activity.getString(R.string.mandatorydescription)
        composeTestRule.onNodeWithText(sDescriptionError)
            .assertIsDisplayed()

        // Saisie de la description
        val sLabelDesc = composeTestRule.activity.getString(R.string.description)
        composeTestRule.onNodeWithText(sLabelDesc)
            .performTextInput("Description Test")

        // Plus d'erreur
        composeTestRule.onNodeWithText(sDescriptionError)
            .assertIsNotDisplayed()

        // Bouton validé toujours grisé
        composeTestRule.onNodeWithText(sValidateButton)
            .assertIsNotEnabled()

        // Erreur Mandatory date
        val sDateError = composeTestRule.activity.getString(R.string.mandatorydatetime)
        composeTestRule.onNodeWithText(sDateError)
            .assertIsDisplayed()

        // Saisie de la date
        getPicketDate()

        // Plus d'erreur
        composeTestRule.onNodeWithText(sDateError)
            .assertIsNotDisplayed()

        // Bouton validé toujours grisé
        composeTestRule.onNodeWithText(sValidateButton)
            .assertIsNotEnabled()

        // Erreur Mandatory address
        val sAddressError = composeTestRule.activity.getString(R.string.mandatoryaddress)
        composeTestRule.onNodeWithText(sAddressError)
            .assertIsDisplayed()

        // Address
        val sLabelAddress = composeTestRule.activity.getString(R.string.address)
        composeTestRule.onNodeWithText(sLabelAddress)
            .performTextInput("Paris")

        // Plus d'erreur
        composeTestRule.onNodeWithText(sAddressError)
            .assertIsNotDisplayed()

        // La photo est déjà remplie en mode test

        // Bouton validé dégrisé
        composeTestRule.onNodeWithText(sValidateButton)
            .assertIsEnabled()

    }

    private fun getPicketDate() {
        val sLabelDate = composeTestRule.activity.getString(R.string.date)
        composeTestRule.onNodeWithText(sLabelDate)
            .performClick() // Ouvre le picker que je ne peux pas piloter

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)  // Ajoute un jour
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1 // Les mois commencent à 0
        val year = calendar.get(Calendar.YEAR)

        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name)))
            .perform(PickerActions.setDate(year, month, day))

        onView(withText("OK")).perform(click()) // Appuyez sur le bouton OK du picker
    }



}