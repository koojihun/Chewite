package com.chewite.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.AnonymousAWSCredentials
import com.amazonaws.regions.Region.getRegion
import com.amazonaws.regions.Regions
import com.amazonaws.services.cognitoidentityprovider.AmazonCognitoIdentityProviderClient
import com.chewite.app.data.auth.AuthRepositoryImpl
import com.chewite.app.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthHiltModule {
    @Provides
    @Singleton
    fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

    @Provides
    @Singleton
    fun provideAuthClient(): AmazonCognitoIdentityProviderClient {
        val config = ClientConfiguration().apply {
            connectionTimeout = 15_000
            socketTimeout = 15_000
        }
        return AmazonCognitoIdentityProviderClient(
            AnonymousAWSCredentials(), config
        ).apply {
            setRegion(getRegion(Regions.AP_NORTHEAST_2))
        }
    }

    @Provides
    @Singleton
    @Named("auth")
    fun provideAuthDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = null, migrations = emptyList(), produceFile = {
                context.preferencesDataStoreFile("auth_tokens")
            })
    }
}
