package com.chewite.app.di

import android.content.Context
import androidx.credentials.CredentialManager
import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.AnonymousAWSCredentials
import com.amazonaws.regions.Region.getRegion
import com.amazonaws.regions.Regions
import com.amazonaws.services.cognitoidentityprovider.AmazonCognitoIdentityProviderClient
import com.chewite.app.data.api.auth.social.GoogleAuthProvider
import com.chewite.app.data.api.auth.social.SocialAuthProvider
import com.chewite.app.data.api.chewite.AccountApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthHiltModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        return Retrofit.Builder().baseUrl("https://poodle.petground.kr/")
            .addConverterFactory(MoshiConverterFactory.create(moshi)).build()
    }

    @Provides
    @Singleton
    fun provideAccountApi(retrofit: Retrofit): AccountApi {
        return retrofit.create(AccountApi::class.java)
    }

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
    @Named("GoogleAuthProvider")
    fun provideGoogleAuthProvider(
        @ApplicationContext context: Context, credentialManager: CredentialManager
    ): SocialAuthProvider = GoogleAuthProvider(context, credentialManager)

    @Provides
    @Singleton
    fun provideCredentialManager(
        @ApplicationContext context: Context
    ): CredentialManager {
        return CredentialManager.create(context)
    }
}