package com.example.cednik.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cednik.model.Journey
import com.example.cednik.model.User

@Database(entities = [Journey::class, User::class], version = 7, exportSchema = true)
abstract class JourneysDatabase : RoomDatabase() {

    abstract fun journeysDao(): JourneysDao

    companion object {
        private var INSTANCE: JourneysDatabase? = null

        fun getDatabase(context: Context): JourneysDatabase {
            if (INSTANCE == null) {
                synchronized(JourneysDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            JourneysDatabase::class.java, "journeys_database"
                        ).fallbackToDestructiveMigration().build()
                    }
                }
            }
            return INSTANCE!!
        }
    }
}