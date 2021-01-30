package com.dsvag.tinkoff.ui

import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.dsvag.tinkoff.R
import com.dsvag.tinkoff.databinding.ActivityMainBinding
import com.dsvag.tinkoff.models.Post
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val postViewModel by viewModels<PostViewModel>()

    private val rotateAnimation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.rotate).apply {
            repeatCount = 0
        }
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

        postViewModel.state.observe(this, ::stateObserver)

        postViewModel.post.observe(this) { (post: Post?, id: Int) ->
            binding.buttonPrevious.isEnabled = id > 0

            post?.let {
                binding.gifDescription.text = it.description

                Glide
                    .with(this)
                    .load(it.previewURL)
                    .optionalCenterInside()
                    .transition(DrawableTransitionOptions.withCrossFade(200))
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .skipMemoryCache(false)
                    .into(binding.gifPlaceholder)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Glide.get(this).clearMemory()
    }

    private fun stateObserver(state: PostViewModel.State) {
        when (state) {
            PostViewModel.State.Default -> postViewModel.next()
            PostViewModel.State.Loading -> onLoad()
            PostViewModel.State.Success -> onSuccess()
            PostViewModel.State.Error -> onError()
        }
    }

    private fun onLoad() {
        binding.loadingIndicator.isVisible = true
        binding.buttonNext.isEnabled = false
    }

    private fun onSuccess() {
        binding.loadingIndicator.isVisible = false
        binding.card.isVisible = true
        binding.buttonNext.isEnabled = true
    }

    private fun onError() {
        binding.loadingIndicator.isVisible = false
        binding.card.isVisible = false
        binding.buttonNext.isEnabled = true
    }
}