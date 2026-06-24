package com.example.bookapp.di

import com.example.bookapp.data.network.OpenLibraryApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [NetworkModule::class]
)
object FakeNetworkModule {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val serverUrl = MockWebServerHolder.url.toHttpUrl()
                val newUrl = chain.request().url.newBuilder()
                    .scheme(serverUrl.scheme)
                    .host(serverUrl.host)
                    .port(serverUrl.port)
                    .build()
                chain.proceed(chain.request().newBuilder().url(newUrl).build())
            }
            .build()
        return Retrofit.Builder()
            .baseUrl("http://localhost/")
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideOpenLibraryApi(retrofit: Retrofit): OpenLibraryApi =
        retrofit.create(OpenLibraryApi::class.java)
}

object MockWebServerHolder {
    var url: String = "http://localhost/"
}
