package com.example.workminder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey val id: String,
    val subject_name: String,
    val color: String
)
