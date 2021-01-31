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
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.dsvag.tinkoff.R
import com.dsvag.tinkoff.databinding.ActivityMainBinding
import com.dsvag.tinkoff.di.GlideApp
import com.dsvag.tinkoff.models.Post
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

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

        postViewModel.post.observe(this) { (post: Post?, id: Int) ->
            binding.buttonPrevious.isEnabled = id > 0

            post?.let {
                binding.gifDescription.text = it.description

                GlideApp
                    .with(this)
                    .asGif()
                    .load(it.gifURL)
                    .optionalCenterInside()
                    .transition(DrawableTransitionOptions.withCrossFade(200))
                    .into(binding.gifPlaceholder)
            }
        }

        binding.card.setOnLongClickListener {
            val clipboard = ContextCompat.getSystemService(this, ClipboardManager::class.java)
            val clip = ClipData.newUri(contentResolver, "Gif URL", postViewModel.getGifUrl())

            clipboard?.setPrimaryClip(clip)
            Toast.makeText(this, "Gif URL Copied", Toast.LENGTH_SHORT).show()

            true
        }
    }

    // FIXME: 1/31/2021
    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.launch(Dispatchers.IO) {
            GlideApp.get(this@MainActivity).clearDiskCache()
        }
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
        binding.buttonNext.isVisible = true
        binding.buttonPrevious.isVisible = true
    }

    private fun onError() {
        binding.loadingIndicator.isVisible = false
        binding.card.isVisible = false
        binding.buttonNext.isVisible = false
        binding.buttonPrevious.isVisible = false
    }
}