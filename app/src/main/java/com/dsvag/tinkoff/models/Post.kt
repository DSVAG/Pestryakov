package com.dsvag.tinkoff.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Post(
    @Json(name = "description")
    val description: String?,

    @Json(name = "gifURL")
    val gifUrl: String?,

    @Json(name = "gifSize")
    val gifSize: Int?,
)