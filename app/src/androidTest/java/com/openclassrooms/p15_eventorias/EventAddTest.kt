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
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withText
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
        val sLabelDate = composeTestRule.activity.getString(R.string.date)
        composeTestRule.onNodeWithText(sLabelDate).performClick() // Ouvre le picker que je ne peux pas piloter

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)  // Ajoute un jour
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1 // Les mois commencent à 0
        val year = calendar.get(Calendar.YEAR)

        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name)))
            .perform(PickerActions.setDate(year, month, day))

        onView(withText("OK")).perform(click()) // Appuyez sur le bouton OK du picker


        // Photo => déjà présente en mode test

 //       val myViewModel = composeTestRule.activity.viewModels<EventAddViewModel>().value
 //       val test = myViewModel.getFormError()

//        val context = InstrumentationRegistry.getInstrumentation().targetContext
//        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.baseline_face_24)
//
//        composeTestRule.onNodeWithTag("imgEvent")
//            .perform {
//                imageView.setImageBitmap(bitmap)
//            }
//
//        composeTestRule.onAllNodesWithContentDescription()
//        onView(with(R.id.image_view))
//            .perform {
//                imageView.setImageBitmap(bitmap)
//            }


        // Clique sur le bouton 'Validate'
        val sValidateButton = composeTestRule.activity.getString(R.string.validate)
        composeTestRule.onNodeWithText(sValidateButton).performClick()

        composeTestRule.awaitIdle()

        // Retour à la liste d'évènements

        // Vérifier l'ajout dans la liste d'évènement

        // Attend tant que la liste d'évènement n'est pas chargée complétement
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            // un élément de plus
            composeTestRule.onAllNodesWithTag("event_item").fetchSemanticsNodes().size == fakeListEvent.size+1
        }

        // L'évènement doit apparaitre
        composeTestRule.onNodeWithText(sTitleVal)
            .assertIsDisplayed()

    }

    // TODO JG : Ecrire les tests aux limites (champs obligatoires manquants)


}