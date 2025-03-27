package com.example.movieapp.di

import com.example.movieapp.network.CommentAPIService
import com.example.movieapp.network.MovieApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideGsonBuilder(): Gson {
        return GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
    }

    @Singleton
    @Provides
    @Named("retrofitPhim")
    fun provideRetrofit(): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl("https://phimapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
    }

    @Singleton
    @Provides
    @Named("retrofitComment")
    fun provideRetrofitComment(): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl("https://c580-116-96-47-36.ngrok-free.app/api/")
            .addConverterFactory(GsonConverterFactory.create())
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }


    @Singleton
    @Provides
    fun provideMovieApiService(
        @Named("retrofitPhim") retrofit: Retrofit.Builder,
        okHttpClient: OkHttpClient
    ): MovieApiService {
        return retrofit
            .client(okHttpClient)
            .build()
            .create(MovieApiService::class.java)
    }


    @Singleton
    @Provides
    fun provideCommentApiService(
        @Named("retrofitComment") retrofit: Retrofit.Builder,
        okHttpClient: OkHttpClient
    ): CommentAPIService {
        return retrofit
            .client(okHttpClient)
            .build()
            .create(CommentAPIService::class.java)
    }

}