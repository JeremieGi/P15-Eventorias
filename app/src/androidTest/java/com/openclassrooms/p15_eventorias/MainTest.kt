package com.openclassrooms.p15_eventorias

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
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

    companion object {
        private const val IDCUSTOMER = 1
    }

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    // Lancement de l'activité principale en début de test
    @get:Rule(order = 2)
    var activityTest = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun add_classic() {

        val test = ""

    }

}