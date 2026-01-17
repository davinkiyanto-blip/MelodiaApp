package com.xcode.melodia.data.model

import com.google.gson.annotations.SerializedName

data class GenerateRequest(
    val customMode: Boolean = false,
    val instrumental: Boolean = false,
    val model: String = "V5",
    val negativeTags: String = "",
    val prompt: String = "",
    val style: String = "",
    val title: String = ""
)

data class TaskResponse(
    val id: String?,
    @SerializedName("jobId") val jobId: String?,
    @SerializedName("task_url") val taskUrl: String?,
    val status: String?,
    val progress: String?,
    val records: List<MusicRecord>?,
    val message: String?
)

data class MusicRecord(
    val id: String,
    val title: String?,
    val prompt: String?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("audio_url") val audioUrl: String?,
    val duration: Double?,
    val model: String?,
    val tags: String?,
    @SerializedName("create_time") val createTime: String?
)
