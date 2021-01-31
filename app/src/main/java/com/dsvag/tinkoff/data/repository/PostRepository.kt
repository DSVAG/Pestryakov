package com.dsvag.tinkoff.data.repository

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.dsvag.tinkoff.data.remote.ApiPostService
import com.dsvag.tinkoff.models.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Integer.max
import javax.inject.Inject

class PostRepository @Inject constructor(private val apiPostService: ApiPostService) {

    val mutableState = MutableLiveData<State>(State.Default)

    private val _mutablePosts = mutableListOf<Post>()
    private var postInd = -1

    suspend fun getNext() {
        if (postInd == -1 || postInd == _mutablePosts.lastIndex) {
            fetchRandomPost()
        } else {
            postInd += 1
            setState(State.Success(_mutablePosts[postInd], postInd))
        }
    }

    suspend fun getPrevious() {
        postInd = max(postInd - 1, 0)
        setState(State.Success(_mutablePosts[postInd], postInd))
    }

    fun getGifUrl(): Uri? = _mutablePosts[postInd].gifUrl?.toUri()

    private suspend fun fetchRandomPost() {
        setState(State.Loading)
        val response = runCatching { apiPostService.getRandomPost() }.getOrNull()

        if (response != null && response.isSuccessful && response.body() != null) {
            if (response.body()?.gifSize != 0) {

                _mutablePosts.add(response.body()!!)
                postInd += 1

                setState(State.Success(_mutablePosts[postInd], postInd))
            } else {
                fetchRandomPost()
            }
        } else {
            setState(State.Error)
        }
    }

    private suspend fun setState(newState: State) = withContext(Dispatchers.Main) {
        mutableState.postValue(newState)
    }

    sealed class State {
        object Default : State()
        object Loading : State()
        data class Success(val post: Post, val ind: Int) : State()
        object Error : State()
    }
}