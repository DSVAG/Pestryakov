package com.dsvag.tinkoff.ui

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsvag.tinkoff.data.repository.PostRepository
import com.dsvag.tinkoff.models.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostViewModel @ViewModelInject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _mutableState = MutableLiveData<State>(State.Default)
    val state get() = _mutableState

    fun next() {
        setState(State.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            val pair = postRepository.getNext()

            if (pair != null) {
                setState(State.Success(pair.first, pair.second))
            } else {
                setState(State.Error)
            }
        }
    }

    fun previous() {
        viewModelScope.launch(Dispatchers.IO) {
            val pair = postRepository.getPrevious()
            setState(State.Success(pair.first, pair.second))
        }
    }

    fun getGifUrl(): Uri? = postRepository.getGifUrl()

    private fun setState(newState: State) {
        viewModelScope.launch(Dispatchers.Main) {
            _mutableState.postValue(newState)
        }
    }

    sealed class State {
        object Default : State()
        object Loading : State()
        data class Success(val post: Post, val ind: Int) : State()
        object Error : State()
    }
}