package com.dsvag.tinkoff.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Post(
    @Json(name = "id")
    val id: Int?,

    @Json(name = "description")
    val description: String?,

    @Json(name = "votes")
    val votes: Int?,

    @Json(name = "author")
    val author: String?,

    @Json(name = "date")
    val date: String?,

    @Json(name = "gifURL")
    val gifURL: String?,

    @Json(name = "gifSize")
    val gifSize: Int?,

    @Json(name = "previewURL")
    val previewURL: String?,

    @Json(name = "videoURL")
    val videoURL: String?,

    @Json(name = "videoPath")
    val videoPath: String?,

    @Json(name = "videoSize")
    val videoSize: Int?,

    @Json(name = "type")
    val type: String?,

    @Json(name = "width")
    val width: String?,

    @Json(name = "height")
    val height: String?,

    @Json(name = "commentsCount")
    val commentsCount: Int?,

    @Json(name = "fileSize")
    val fileSize: Int?,

    @Json(name = "canVote")
    val canVote: Boolean?
)