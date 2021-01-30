package com.dsvag.tinkoff.ui

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

    private val _mutablePosts = MutableLiveData<MutableList<Post>>(mutableListOf())
    private val _mutablePost = MutableLiveData<Pair<Post?, Int>>()
    val post get() = _mutablePost

    private var id = -1

    fun next() {
        if (id == -1 || id == _mutablePosts.value?.lastIndex) {
            loadNext()
            id += 1
        } else {
            id += 1
            _mutablePost.postValue(_mutablePosts.value?.get(id) to id)
        }
    }

    fun previous() {
        id = max(id - 1, 0)
        _mutablePost.postValue(_mutablePosts.value?.get(id) to id)
    }

    private fun loadNext() {
        setState(State.Loading)

        viewModelScope.launch(Dispatchers.IO) {
            val response = postRepository.fetchRandomPost()
            if (response.isSuccessful && response.body() != null) {
                val value = _mutablePosts.value

                value?.add(response.body()!!)

                _mutablePosts.postValue(value!!)
                _mutablePost.postValue(_mutablePosts.value?.get(id) to id)

                setState(State.Success)
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

    sealed class State {
        object Default : State()
        object Loading : State()
        object Success : State()
        object Error : State()
    }
}