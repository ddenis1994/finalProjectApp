package com.example.finalprojectapp.localDB

import androidx.room.TypeConverter
import kotlinx.serialization.*
import kotlinx.serialization.json.*


class Converters {
    @Serializable
    data class Data(
        val a: List<String>
    )

    @TypeConverter
    fun listToString(value: String?): List<String>? {
        val json = Json(JsonConfiguration.Stable)
        return value?.let {
                json.parse(Data.serializer(), it)
            }?.a
    }

    @TypeConverter
    fun dateToTimestamp(date: List<String>?): String? {
        val json = Json(JsonConfiguration.Stable)
        val data = date?.let {
            Data(it)
        }
        return json.stringify(Data.serializer(), data!!)
    }
}