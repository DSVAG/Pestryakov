package com.dsvag.tinkoff.data.remote

import com.dsvag.tinkoff.models.Post
import retrofit2.Response
import retrofit2.http.GET

interface ApiPostService {

    @GET("random?json=true")
    suspend fun getRandomPost(): Response<Post>

}