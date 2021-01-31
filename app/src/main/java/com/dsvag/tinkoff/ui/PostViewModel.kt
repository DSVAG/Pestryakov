package com.dsvag.tinkoff.ui

import android.net.Uri
import androidx.core.net.toUri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsvag.tinkoff.data.repository.PostRepository
import com.dsvag.tinkoff.models.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.max

class PostViewModel @ViewModelInject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _mutableState = MutableLiveData<State>(State.Default)
    val state get() = _mutableState

    private val _mutablePosts = mutableListOf<Post>()
    private val _mutablePost = MutableLiveData<Pair<Post, Int>>()
    val post get() = _mutablePost

    private var postInd = -1

    fun next() {
        if (postInd == -1 || postInd == _mutablePosts.lastIndex) {
            loadNext()
        } else {
            postInd += 1
            _mutablePost.postValue(_mutablePosts[postInd] to postInd)
        }
    }

    fun previous() {
        postInd = max(postInd - 1, 0)
        _mutablePost.postValue(_mutablePosts[postInd] to postInd)
    }

    private fun loadNext() {
        setState(State.Loading)

        viewModelScope.launch(Dispatchers.IO) {
            val response = runCatching { postRepository.fetchRandomPost() }.getOrNull()

            if (response != null && response.isSuccessful && response.body() != null) {

                if (response.body()?.gifSize != 0) {

                    _mutablePosts.add(response.body()!!)
                    postInd += 1

                    _mutablePost.postValue(_mutablePosts[postInd] to postInd)

                    setState(State.Success)
                } else {
                    loadNext()
                }
            } else {
                setState(State.Error)
            }
        }
    }

    private fun setState(newState: State) {
        viewModelScope.launch(Dispatchers.Main) {
            state.value = newState
        }
    }

    fun getGifUrl(): Uri? = post.value?.first?.gifUrl?.toUri()

    sealed class State {
        object Default : State()
        object Loading : State()
        object Success : State()
        object Error : State()
    }
}