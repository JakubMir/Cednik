package com.example.cednik.di

import com.example.cednik.database.JourneysDao
import com.example.cednik.database.JourneysDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    @Provides
    @Singleton
    fun provideTasksDao(database: JourneysDatabase): JourneysDao {
        return database.journeysDao()
    }
}