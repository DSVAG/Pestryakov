package com.dsvag.tinkoff.data.remote

import com.dsvag.tinkoff.models.Post
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiPostService {

    @GET("random")
    suspend fun getRandom(
        @Query("json") isJson: Boolean = true
    ): Response<Post>

}