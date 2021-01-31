package com.dsvag.tinkoff.data.repository

import com.dsvag.tinkoff.data.remote.ApiPostService
import com.dsvag.tinkoff.models.Post
import retrofit2.Response
import javax.inject.Inject

class PostRepository @Inject constructor(private val apiPostService: ApiPostService) {

    suspend fun fetchRandomPost(): Response<Post> {
        return apiPostService.getRandomPost()
    }

}