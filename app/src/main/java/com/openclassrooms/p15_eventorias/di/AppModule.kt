package com.openclassrooms.p15_eventorias.di

import android.content.Context
import com.openclassrooms.p15_eventorias.repository.EventApi
import com.openclassrooms.p15_eventorias.repository.EventFireStoreAPI
import com.openclassrooms.p15_eventorias.repository.InjectedContext
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * This class acts as a Dagger Hilt module, responsible for providing dependencies to other parts of the application.
 * It's installed in the SingletonComponent, ensuring that dependencies provided by this module are created only once
 * and remain available throughout the application's lifecycle.
 */
@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideEventAPI(): EventApi {
        return EventFireStoreAPI() // Utilisation de FireStore
    }


    @Provides
    @Singleton
    fun provideConnectivityChecker(@ApplicationContext context: Context): InjectedContext {
        return InjectedContext(context) // Contexte injecté dans le repository
    }

}