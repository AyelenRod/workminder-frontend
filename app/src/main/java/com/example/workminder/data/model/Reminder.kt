package com.example.workminder.data.model

import com.google.gson.annotations.SerializedName

data class Reminder(
    @SerializedName("reminder_date") val reminderDate: String
)
