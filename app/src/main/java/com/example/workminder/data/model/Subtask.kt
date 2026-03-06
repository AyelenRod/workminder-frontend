package com.example.workminder.data.model

import com.google.gson.annotations.SerializedName

data class Subtask(
    @SerializedName("subtask_id") val subtask_id: String,
    @SerializedName("task_id") val task_id: String,
    @SerializedName("subtask_name") val subtask_name: String,
    @SerializedName("is_completed") val is_completed: Boolean = false
)
