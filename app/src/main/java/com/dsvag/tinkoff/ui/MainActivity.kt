package com.dsvag.tinkoff.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.dsvag.tinkoff.R
import com.dsvag.tinkoff.databinding.ActivityMainBinding
import com.dsvag.tinkoff.models.Post
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val postViewModel by viewModels<PostViewModel>()

    private val rotateAnimation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.rotate)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.buttonNext.setOnClickListener {
            postViewModel.next()
            it.startAnimation(rotateAnimation)
        }

        binding.buttonPrevious.setOnClickListener {
            postViewModel.previous()
            it.startAnimation(rotateAnimation)
        }

        binding.buttonRepeat.setOnClickListener {
            postViewModel.next()
        }

        postViewModel.state.observe(this, ::stateObserver)

        binding.card.setOnLongClickListener {
            val clipboard = ContextCompat.getSystemService(this, ClipboardManager::class.java)
            val clip = ClipData.newUri(contentResolver, "Gif Url", postViewModel.getGifUrl())

            clipboard?.setPrimaryClip(clip)
            Toast.makeText(this, this.getString(R.string.toast_msg), Toast.LENGTH_SHORT).show()

            true
        }
    }

    private fun stateObserver(state: PostViewModel.State) {
        when (state) {
            PostViewModel.State.Default -> postViewModel.next()
            PostViewModel.State.Loading -> onLoad()
            is PostViewModel.State.Success -> onSuccess(state.post, state.ind)
            PostViewModel.State.Error -> onError()
        }
    }

    private fun onLoad() {
        binding.loadingIndicator.isVisible = true
        binding.buttonNext.isEnabled = false
    }

    private fun onSuccess(post: Post, ind: Int) {
        binding.loadingIndicator.isVisible = false
        binding.card.isVisible = true
        binding.buttonNext.isEnabled = true
        binding.buttonNext.isVisible = true
        binding.buttonPrevious.isVisible = true
        binding.networkError.isVisible = false

        binding.buttonPrevious.isEnabled = ind > 0

        binding.gifDescription.text = post.description

        Glide
            .with(this)
            .asGif()
            .load(post.gifUrl)
            .optionalCenterInside()
            .transition(DrawableTransitionOptions.withCrossFade(200))
            .into(binding.gifPlaceholder)

        Glide.with(this)
            .asGif()
            .load(post.gifUrl)
            .optionalCenterCrop()
            .transition(DrawableTransitionOptions.withCrossFade(200))
            .into(binding.bottomGifPlaceholder)
    }

    private fun onError() {
        binding.loadingIndicator.isVisible = false
        binding.card.isVisible = false
        binding.buttonNext.isVisible = false
        binding.buttonPrevious.isVisible = false
        binding.networkError.isVisible = true
    }
}