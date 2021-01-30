package com.dsvag.tinkoff.di

import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
@GlideModule
class GlideModule : AppGlideModule()