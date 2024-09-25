package com.openclassrooms.p15_eventorias

import android.content.Context
import com.openclassrooms.p15_eventorias.di.AppModule
import com.openclassrooms.p15_eventorias.repository.InjectedContext
import com.openclassrooms.p15_eventorias.repository.event.EventApi
import com.openclassrooms.p15_eventorias.repository.event.EventFakeAPI
import com.openclassrooms.p15_eventorias.repository.user.UserApi
import com.openclassrooms.p15_eventorias.repository.user.UserFakeAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]               // Remplace l'injection de dépendance de production
)
class DependencyInjectionTest {

    // Branchement des Fake API pour les tests

    @Provides
    @Singleton
    fun provideEventAPI(): EventApi {
        return EventFakeAPI()
        //return EventFirestoreAPI()
    }

    @Provides
    @Singleton
    fun provideUserAPI(): UserApi {
        return UserFakeAPI() //
        //return UserFirestoreAPI() // Utilisation de FireStore
    }

    @Provides
    @Singleton
    fun provideConnectivityChecker(@ApplicationContext context: Context): InjectedContext {
        return InjectedContext(context) // Contexte injecté dans le repository
    }
}