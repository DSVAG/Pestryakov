package com.dsvag.tinkoff.data.repository

import android.net.Uri
import androidx.core.net.toUri
import com.dsvag.tinkoff.data.remote.ApiPostService
import com.dsvag.tinkoff.models.Post
import java.lang.Integer.max
import javax.inject.Inject

class PostRepository @Inject constructor(private val apiPostService: ApiPostService) {

    private val _mutablePosts = mutableListOf<Post>()
    private var postInd = -1

    suspend fun getNext(): Pair<Post, Int>? {
        return if (postInd == -1 || postInd == _mutablePosts.lastIndex) {
            fetchRandomPost()
        } else {
            postInd += 1
            _mutablePosts[postInd] to postInd
        }
    }

    fun getPrevious(): Pair<Post, Int> {
        postInd = max(postInd - 1, 0)
        return _mutablePosts[postInd] to postInd
    }

    fun getGifUrl(): Uri? = _mutablePosts[postInd].gifUrl?.toUri()

    private suspend fun fetchRandomPost(): Pair<Post, Int>? {
        val response = runCatching { apiPostService.getRandomPost() }.getOrNull()

        if (response != null && response.isSuccessful && response.body() != null) {
            if (response.body()?.gifSize != 0) {

                _mutablePosts.add(response.body()!!)
                postInd += 1

                return _mutablePosts[postInd] to postInd
            } else {
                return fetchRandomPost()
            }
        } else {
            return null
        }
    }
}