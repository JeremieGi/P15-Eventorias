package com.openclassrooms.p15_eventorias

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

// https://medium.com/jetpack-composers/writing-instrumented-tests-using-hilt-and-jetpack-compose-in-mvvm-30d4e3fe0318
// Article pour faire un test instrumenté en utilisant Hilt pour injecter des Fake APIs

class CustomTestRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }

}