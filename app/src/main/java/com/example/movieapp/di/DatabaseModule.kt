package com.example.movieapp.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.movieapp.database.AppDatabase
import com.example.movieapp.database.DatabaseManager
import com.example.movieapp.database.MovieDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideContext(app: Application): Context {
        return app.applicationContext
    }

    @Provides
    @Singleton
    fun provideDataStoreManager(
        @ApplicationContext context: Context,
        movieDao: MovieDao
    ): DatabaseManager {
        return DatabaseManager(context, movieDao)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "kkphim_database"
        ).build()
    }

    @Provides
    fun provideMovieDao(database: AppDatabase): MovieDao {
        return database.movieDao()
    }
}