package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val videoUri: String,
    val thumbnailUri: String = "",
    val duration: String = "10:00",
    val category: String = "Masterclass",
    val isPremium: Boolean = false,
    val viewsCount: Int = 0,
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val isDownloaded: Boolean = false,
    val downloadSizeMb: Double = 45.0,
    val createdAt: Long = System.currentTimeMillis(),
    val creatorName: String = "Creator Studio",
    val tags: String = "video,streaming"
)
