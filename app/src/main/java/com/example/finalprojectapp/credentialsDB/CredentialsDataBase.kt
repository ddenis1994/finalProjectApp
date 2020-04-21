package com.example.finalprojectapp.credentialsDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.finalprojectapp.credentialsDB.model.Credentials
import com.example.finalprojectapp.credentialsDB.model.DataSet
import com.example.finalprojectapp.credentialsDB.model.Service
import com.example.finalprojectapp.credentialsDB.model.relationship.DataSetCredentialsManyToMany
import com.example.finalprojectapp.localDB.Converters


@Database(entities = [Service::class, Credentials::class,DataSet::class, DataSetCredentialsManyToMany::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class CredentialsDataBase: RoomDatabase() {

    abstract fun serviceDao(): LocalServiceDao
    abstract fun applicationDAO():LocalApplicationDAO

    companion object {
        @Volatile
        private var INSTANCE: CredentialsDataBase? = null

    fun getDatabase(context: Context): CredentialsDataBase {
        val tempInstance = INSTANCE
        if (tempInstance != null) {
            return tempInstance
        }
        synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                CredentialsDataBase::class.java,
                "password_Database"
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