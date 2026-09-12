package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AppRepository(private val dao: AppDao) {

    val allVideos: Flow<List<VideoEntity>> = dao.getAllVideos()
    val downloadedVideos: Flow<List<VideoEntity>> = dao.getDownloadedVideos()
    val userAccount: Flow<UserAccountEntity?> = dao.getUserAccount()

    fun getCommentsForVideo(videoId: Long): Flow<List<CommentEntity>> {
        return dao.getCommentsForVideo(videoId)
    }

    suspend fun insertVideo(video: VideoEntity): Long = withContext(Dispatchers.IO) {
        dao.insertVideo(video)
    }

    suspend fun updateVideo(video: VideoEntity) = withContext(Dispatchers.IO) {
        dao.updateVideo(video)
    }

    suspend fun deleteVideo(video: VideoEntity) = withContext(Dispatchers.IO) {
        dao.deleteVideo(video)
    }

    suspend fun toggleLike(video: VideoEntity) = withContext(Dispatchers.IO) {
        val newLiked = !video.isLiked
        val newLikesCount = if (newLiked) video.likesCount + 1 else (video.likesCount - 1).coerceAtLeast(0)
        dao.updateLikeStatus(video.id, newLiked, newLikesCount)
    }

    suspend fun toggleDownload(video: VideoEntity) = withContext(Dispatchers.IO) {
        val newDownloadState = !video.isDownloaded
        dao.updateDownloadStatus(video.id, newDownloadState)
    }

    suspend fun incrementViews(videoId: Long) = withContext(Dispatchers.IO) {
        dao.incrementViews(videoId)
    }

    suspend fun addComment(videoId: Long, authorName: String, content: String): Long = withContext(Dispatchers.IO) {
        dao.insertComment(
            CommentEntity(
                videoId = videoId,
                authorName = authorName,
                content = content,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun updateSubscription(tier: String, isSubscribed: Boolean, durationDays: Int) = withContext(Dispatchers.IO) {
        val expiryMillis = if (isSubscribed) {
            if (durationDays < 0) -1L // Lifetime
            else System.currentTimeMillis() + (durationDays.toLong() * 86400000L)
        } else {
            0L
        }
        dao.updateSubscription(tier, isSubscribed, expiryMillis)
    }

    suspend fun updateUserRole(isCreator: Boolean) = withContext(Dispatchers.IO) {
        dao.updateUserRole(isCreator)
    }

    suspend fun updateUserProfile(name: String, email: String) = withContext(Dispatchers.IO) {
        dao.updateUserProfile(name, email)
    }

    suspend fun resetDemoData() = withContext(Dispatchers.IO) {
        dao.deleteAllVideos()
        AppDatabase.populateInitialData(dao)
    }
}
