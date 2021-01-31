package com.dsvag.tinkoff.ui

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsvag.tinkoff.data.repository.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostViewModel @ViewModelInject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    val state get() = postRepository.mutableState

    fun next() {
        viewModelScope.launch(Dispatchers.IO) {
            postRepository.getNext()
        }
    }

    fun previous() {
        viewModelScope.launch(Dispatchers.IO) {
            postRepository.getPrevious()
        }
    }

    fun getGifUrl(): Uri? = postRepository.getGifUrl()
}