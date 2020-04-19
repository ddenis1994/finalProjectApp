package com.example.finalprojectapp.credentialsDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.finalprojectapp.credentialsDB.model.Credentials
import com.example.finalprojectapp.credentialsDB.model.DataSet
import com.example.finalprojectapp.credentialsDB.model.Service
import com.example.finalprojectapp.localDB.Converters


@Database(entities = [Service::class, Credentials::class,DataSet::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class CredentialsDataBase: RoomDatabase() {

    abstract fun credentialsDao(): LocalServiceDao

    companion object {
        @Volatile
        private var INSTANCE: CredentialsDataBase? = null

    fun getDatabase(context: Context): CredentialsDataBase {
        val tempInstance = CredentialsDataBase.INSTANCE
        if (tempInstance != null) {
            return tempInstance
        }
        synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                CredentialsDataBase::class.java,
                "word_database"
            )
                .addCallback(
                    object : RoomDatabase.Callback() {
                    }
                )
                .fallbackToDestructiveMigration()
                .build()
            CredentialsDataBase.INSTANCE = instance
            return instance
        }
    }
}



}