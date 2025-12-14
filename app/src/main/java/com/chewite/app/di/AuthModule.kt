package com.chewite.app.di

import android.content.Context
import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.AnonymousAWSCredentials
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.regions.Regions
import com.amazonaws.services.cognitoidentityprovider.AmazonCognitoIdentityProviderClient
import com.chewite.app.data.login.AuthApi
import com.chewite.app.data.login.AuthRepository
import com.chewite.app.data.login.AuthRepositoryImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    @Singleton
    fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository {
        return authRepositoryImpl
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

        return Retrofit.Builder().baseUrl("https://poodle.petground.kr/")
            .addConverterFactory(MoshiConverterFactory.create(moshi)).build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserPool(@ApplicationContext context: Context): CognitoUserPool {
        return CognitoUserPool(
            context,
            "ap-northeast-2_aGbITuffL",
            "2q49e9a4qvgm95a3ail5t0gp0b",
            null,
            Regions.AP_NORTHEAST_2
        )
    }

    @Provides
    @Singleton
    fun provideIdpClient(): AmazonCognitoIdentityProviderClient {
        val config = ClientConfiguration().apply {
            connectionTimeout = 15_000
            socketTimeout = 15_000
        }
        return AmazonCognitoIdentityProviderClient(
            AnonymousAWSCredentials(), config
        ).apply {
            setRegion(com.amazonaws.regions.Region.getRegion(Regions.AP_NORTHEAST_2))
        }
    }
}
