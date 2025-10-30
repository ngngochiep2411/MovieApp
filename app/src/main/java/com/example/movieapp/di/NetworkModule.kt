package com.example.movieapp.di

import com.example.movieapp.Constant
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
import java.util.concurrent.TimeUnit
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
            .baseUrl(Constant.BASE_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
//            .connectTimeout(180, TimeUnit.SECONDS)
//            .writeTimeout(180, TimeUnit.SECONDS)
//            .readTimeout(180, TimeUnit.SECONDS)
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