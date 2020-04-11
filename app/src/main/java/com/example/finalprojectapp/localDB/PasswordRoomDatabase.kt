package com.example.finalprojectapp.localDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.finalprojectapp.data.model.LocalCredentials
import com.example.finalprojectapp.data.model.LocalService

@Database(entities = [LocalService::class,LocalCredentials::class], version = 1, exportSchema = false)
abstract class PasswordRoomDatabase: RoomDatabase() {

    abstract fun localCredentialsDAO(): LocalCredentialsDAO
    abstract fun localServiceDAO(): LocalServiceDAO
    companion object {
        @Volatile
        private var INSTANCE: PasswordRoomDatabase? = null

        fun getDatabase(context: Context): PasswordRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PasswordRoomDatabase::class.java,
                    "word_database"
                )
                    .addCallback(
                        object : RoomDatabase.Callback() {
                        }


                    )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

}