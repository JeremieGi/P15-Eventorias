package com.openclassrooms.p15_eventorias.di

import android.content.Context
import com.openclassrooms.p15_eventorias.repository.event.EventApi
import com.openclassrooms.p15_eventorias.repository.InjectedContext
import com.openclassrooms.p15_eventorias.repository.event.EventFirestoreAPI
import com.openclassrooms.p15_eventorias.repository.user.UserApi
import com.openclassrooms.p15_eventorias.repository.user.UserFirestoreAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Classe permettant de gérer les injections de dépendance pour l'application
 */
@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideEventAPI(): EventApi {
        //return EventFakeAPI()
        return EventFirestoreAPI()
    }

    @Provides
    @Singleton
    fun provideUserAPI(): UserApi {
        //return UserFakeAPI() //
        return UserFirestoreAPI() // Utilisation de FireStore
    }


    @Provides
    @Singleton
    fun provideConnectivityChecker(@ApplicationContext context: Context): InjectedContext {
        return InjectedContext(context) // Contexte injecté dans le repository
    }

}