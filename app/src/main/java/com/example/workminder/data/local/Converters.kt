package com.example.workminder.data.local

import androidx.room.TypeConverter
import com.example.workminder.data.model.TaskStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }
    
    @TypeConverter
    fun fromTaskStatus(status: TaskStatus): String {
        return status.name
    }
    
    @TypeConverter
    fun toTaskStatus(name: String): TaskStatus {
        return try {
            TaskStatus.valueOf(name)
        } catch (e: Exception) {
            TaskStatus.PENDING
        }
    }
}
