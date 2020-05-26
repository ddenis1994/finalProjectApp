package com.example.finalprojectapp.credentialsDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.Notification
import com.example.finalprojectapp.data.model.Service
import com.example.finalprojectapp.data.model.relationship.DataSetCredentialsManyToMany


@Database(entities = [Notification::class,Service::class, Credentials::class, DataSet::class, DataSetCredentialsManyToMany::class], version = 5, exportSchema = false)
@TypeConverters(Converters::class)
abstract class CredentialsDataBase: RoomDatabase() {

    abstract fun credentialDAO():CredentialDAO
    abstract fun dataSetDAO():DataSetDAO
    abstract fun serviceDao():ServiceDAO
    abstract fun notificationDao():NotificationDAO


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