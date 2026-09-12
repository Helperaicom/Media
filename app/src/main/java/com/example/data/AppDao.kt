package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // --- Videos ---
    @Query("SELECT * FROM videos ORDER BY id DESC")
    fun getAllVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE id = :id LIMIT 1")
    fun getVideoById(id: Long): Flow<VideoEntity?>

    @Query("SELECT * FROM videos WHERE isDownloaded = 1 ORDER BY id DESC")
    fun getDownloadedVideos(): Flow<List<VideoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VideoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllVideos(videos: List<VideoEntity>)

    @Update
    suspend fun updateVideo(video: VideoEntity)

    @Delete
    suspend fun deleteVideo(video: VideoEntity)

    @Query("UPDATE videos SET isLiked = :isLiked, likesCount = :likesCount WHERE id = :id")
    suspend fun updateLikeStatus(id: Long, isLiked: Boolean, likesCount: Int)

    @Query("UPDATE videos SET isDownloaded = :isDownloaded WHERE id = :id")
    suspend fun updateDownloadStatus(id: Long, isDownloaded: Boolean)

    @Query("UPDATE videos SET viewsCount = viewsCount + 1 WHERE id = :id")
    suspend fun incrementViews(id: Long)

    @Query("DELETE FROM videos")
    suspend fun deleteAllVideos()

    // --- Comments ---
    @Query("SELECT * FROM comments WHERE videoId = :videoId ORDER BY timestamp DESC")
    fun getCommentsForVideo(videoId: Long): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllComments(comments: List<CommentEntity>)

    @Delete
    suspend fun deleteComment(comment: CommentEntity)

    // --- User Account ---
    @Query("SELECT * FROM user_account WHERE id = 1 LIMIT 1")
    fun getUserAccount(): Flow<UserAccountEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: UserAccountEntity)

    @Query("UPDATE user_account SET subscriptionTier = :tier, isSubscribed = :isSubscribed, subscriptionExpiryMillis = :expiryMillis WHERE id = 1")
    suspend fun updateSubscription(tier: String, isSubscribed: Boolean, expiryMillis: Long)

    @Query("UPDATE user_account SET isCreator = :isCreator WHERE id = 1")
    suspend fun updateUserRole(isCreator: Boolean)

    @Query("UPDATE user_account SET name = :name, email = :email WHERE id = 1")
    suspend fun updateUserProfile(name: String, email: String)
}
