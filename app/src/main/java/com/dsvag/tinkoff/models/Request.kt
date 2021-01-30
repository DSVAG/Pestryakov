package com.dsvag.tinkoff.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Request(
    @Json(name = "result")
    val posts: List<Post>?,

    @Json(name = "totalCount")
    val totalCount: Int?
)