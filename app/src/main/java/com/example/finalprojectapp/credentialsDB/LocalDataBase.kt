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


@Database(entities = [Notification::class,Service::class, Credentials::class, DataSet::class, DataSetCredentialsManyToMany::class], version = 10, exportSchema = false)
@TypeConverters(Converters::class)
abstract class LocalDataBase: RoomDatabase() {

    abstract fun credentialDAO():CredentialDAO
    abstract fun dataSetDAO():DataSetDAO
    abstract fun serviceDao():ServiceDAO
    abstract fun notificationDao():NotificationDAO


    companion object {
        @Volatile
        private var INSTANCE: LocalDataBase? = null

    fun getDatabase(context: Context): LocalDataBase {
        val tempInstance = INSTANCE
        if (tempInstance != null) {
            return tempInstance
        }
        synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                LocalDataBase::class.java,
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