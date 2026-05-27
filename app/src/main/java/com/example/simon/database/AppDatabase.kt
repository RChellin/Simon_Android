package com.example.simon.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [GameResultEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun gameResultDao(): GameResultEntityDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            if (INSTANCE != null) {
                return INSTANCE!!
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "simon_database"
                ).build()

                INSTANCE = instance
                return instance
            }
        }
    }
}