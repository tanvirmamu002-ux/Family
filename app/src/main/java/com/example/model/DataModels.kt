package com.example.model

import androidx.annotation.DrawableRes

data class GalleryCategory(
    val id: String,
    val name: String,
    val count: Int,
    val iconName: String
)

data class GalleryPhoto(
    val id: String,
    val title: String,
    @DrawableRes val resId: Int? = null,
    val url: String? = null,
    val isProtected: Boolean = true,
    val category: String = "Camera"
)

data class AiPromptItem(
    val id: String,
    val title: String,
    val category: String,
    val promptText: String,
    val likesCount: String,
    @DrawableRes val imageResId: Int? = null,
    val tag: String = "Trending"
)

data class VideoTemplate(
    val id: String,
    val title: String,
    val duration: String,
    val category: String,
    @DrawableRes val thumbnailResId: Int? = null
)

data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: String
)

data class ConnectedDevice(
    val id: String,
    val name: String,
    val lastSync: String,
    val status: String, // Online, Offline
    val isProtected: Boolean = true
)

data class ProtectionLog(
    val id: String,
    val time: String,
    val action: String,
    val deviceName: String = "Rahat's Phone"
)
