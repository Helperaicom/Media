package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppRepository
import com.example.data.CommentEntity
import com.example.data.CustomizationSettings
import com.example.data.UserAccountEntity
import com.example.data.VideoEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CreatorStats(
    val totalVideos: Int = 0,
    val totalViews: Long = 0L,
    val totalLikes: Long = 0L,
    val totalWatchHours: Double = 0.0,
    val subscribersCount: Int = 1420,
    val monthlyRevenue: Double = 3450.00,
    val avgRetentionPercent: Int = 68,
    val vipViewsRatio: Int = 74,
    val freeViewsRatio: Int = 26,
    val viewsTrend: List<Int> = listOf(2400, 3100, 2800, 4300, 5200, 6800, 7450),
    val trendDays: List<String> = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
)

data class StreamUiState(
    val allVideos: List<VideoEntity> = emptyList(),
    val filteredVideos: List<VideoEntity> = emptyList(),
    val downloadedVideos: List<VideoEntity> = emptyList(),
    val userAccount: UserAccountEntity = UserAccountEntity(),
    val customization: CustomizationSettings = CustomizationSettings(),
    val selectedCategory: String = "All",
    val searchQuery: String = "",
    val activeVideo: VideoEntity? = null,
    val activeVideoComments: List<CommentEntity> = emptyList(),
    val creatorStats: CreatorStats = CreatorStats(),
    val isUploading: Boolean = false,
    val toastMessage: String? = null
) {
    val effectiveHasVip: Boolean
        get() = userAccount.isSubscribed || customization.isVipFreePromo || customization.monthlyPrice == 0.0
}

class StreamViewModel(private val repository: AppRepository) : ViewModel() {

    private val _selectedCategory = MutableStateFlow("All")
    private val _searchQuery = MutableStateFlow("")
    private val _activeVideoId = MutableStateFlow<Long?>(null)
    private val _isUploading = MutableStateFlow(false)
    private val _toastMessage = MutableStateFlow<String?>(null)
    private val _customization = MutableStateFlow(CustomizationSettings())

    // Reactive comments flow based on active video
    private val _activeComments = _activeVideoId.flatMapLatest { id ->
        if (id != null) {
            repository.getCommentsForVideo(id)
        } else {
            flowOf(emptyList())
        }
    }

    private data class FilterState(
        val category: String,
        val search: String,
        val activeId: Long?
    )

    private val _filterState = combine(
        _selectedCategory,
        _searchQuery,
        _activeVideoId
    ) { category, search, activeId ->
        FilterState(category, search, activeId)
    }

    private data class RepoData(
        val allVideos: List<VideoEntity>,
        val downloadedVideos: List<VideoEntity>,
        val userAccount: UserAccountEntity?
    )

    private val _repoDataFlow = combine(
        repository.allVideos,
        repository.downloadedVideos,
        repository.userAccount
    ) { allVideos, downloaded, account ->
        RepoData(allVideos, downloaded, account)
    }

    val uiState: StateFlow<StreamUiState> = combine(
        _repoDataFlow,
        _filterState,
        _activeComments,
        _customization
    ) { repoData, filter, comments, customization ->

        val allVideos = repoData.allVideos
        val downloadedVideos = repoData.downloadedVideos
        val currentUser = repoData.userAccount ?: UserAccountEntity()

        val filtered = allVideos.filter { video ->
            val matchesCategory = when (filter.category) {
                "All" -> true
                "VIP Exclusive" -> video.isPremium
                "Free to Watch" -> !video.isPremium
                else -> video.category.equals(filter.category, ignoreCase = true)
            }

            val matchesSearch = filter.search.isBlank() ||
                video.title.contains(filter.search, ignoreCase = true) ||
                video.description.contains(filter.search, ignoreCase = true) ||
                video.category.contains(filter.search, ignoreCase = true) ||
                video.tags.contains(filter.search, ignoreCase = true)

            matchesCategory && matchesSearch
        }

        val active = if (filter.activeId != null) {
            allVideos.find { it.id == filter.activeId }
        } else null

        val totalViews = allVideos.sumOf { it.viewsCount.toLong() }
        val totalLikes = allVideos.sumOf { it.likesCount.toLong() }
        val totalWatchMinutes = allVideos.sumOf { video ->
            val durationMin = try {
                val parts = video.duration.split(":")
                if (parts.size == 2) parts[0].toDouble() + (parts[1].toDouble() / 60.0) else 10.0
            } catch (e: Exception) {
                10.0
            }
            video.viewsCount * durationMin * 0.68
        }
        val totalWatchHours = totalWatchMinutes / 60.0

        val stats = CreatorStats(
            totalVideos = allVideos.size,
            totalViews = totalViews,
            totalLikes = totalLikes,
            totalWatchHours = totalWatchHours,
            subscribersCount = 1420 + (totalLikes / 10).toInt(),
            monthlyRevenue = (allVideos.size * customization.monthlyPrice * 20.0).coerceAtLeast(850.0),
            avgRetentionPercent = 68,
            vipViewsRatio = 74,
            freeViewsRatio = 26
        )

        StreamUiState(
            allVideos = allVideos,
            filteredVideos = filtered,
            downloadedVideos = downloadedVideos,
            userAccount = currentUser,
            customization = customization,
            selectedCategory = filter.category,
            searchQuery = filter.search,
            activeVideo = active,
            activeVideoComments = comments,
            creatorStats = stats,
            isUploading = _isUploading.value,
            toastMessage = _toastMessage.value
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StreamUiState()
    )

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectVideo(video: VideoEntity?) {
        _activeVideoId.value = video?.id
        if (video != null) {
            viewModelScope.launch {
                repository.incrementViews(video.id)
            }
        }
    }

    fun toggleLike(video: VideoEntity) {
        viewModelScope.launch {
            repository.toggleLike(video)
        }
    }

    fun toggleDownload(video: VideoEntity) {
        viewModelScope.launch {
            repository.toggleDownload(video)
            _toastMessage.value = if (!video.isDownloaded) {
                "\"${video.title}\" saved to Downloads for offline watching!"
            } else {
                "Removed from offline downloads"
            }
        }
    }

    fun uploadVideo(
        title: String,
        description: String,
        videoUri: String,
        thumbnailUri: String,
        duration: String,
        category: String,
        isPremium: Boolean,
        tags: String
    ) {
        viewModelScope.launch {
            _isUploading.value = true
            val effectiveVideoUri = if (videoUri.isNotBlank()) videoUri else "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
            val effectiveThumbnail = if (thumbnailUri.isNotBlank()) thumbnailUri else "https://images.unsplash.com/photo-1574717024653-61fd2cf4d44d?w=800&q=80"
            val effectiveDuration = if (duration.isNotBlank()) duration else "12:30"

            val newVideo = VideoEntity(
                title = title.trim(),
                description = description.trim(),
                videoUri = effectiveVideoUri,
                thumbnailUri = effectiveThumbnail,
                duration = effectiveDuration,
                category = category,
                isPremium = isPremium,
                viewsCount = 1,
                likesCount = 0,
                isLiked = false,
                isDownloaded = false,
                downloadSizeMb = 64.0,
                createdAt = System.currentTimeMillis(),
                creatorName = "Creator Studio",
                tags = tags
            )
            repository.insertVideo(newVideo)
            _isUploading.value = false
            _toastMessage.value = "Video published successfully to your channel!"
        }
    }

    fun updateVideo(video: VideoEntity) {
        viewModelScope.launch {
            repository.updateVideo(video)
            _toastMessage.value = "Video updated"
        }
    }

    fun deleteVideo(video: VideoEntity) {
        viewModelScope.launch {
            repository.deleteVideo(video)
            if (_activeVideoId.value == video.id) {
                _activeVideoId.value = null
            }
            _toastMessage.value = "Video deleted"
        }
    }

    fun addComment(videoId: Long, content: String, authorName: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            repository.addComment(
                videoId = videoId,
                authorName = authorName.ifBlank { "Viewer" },
                content = content.trim()
            )
        }
    }

    fun subscribeToPlan(tier: String, durationDays: Int) {
        viewModelScope.launch {
            repository.updateSubscription(tier, isSubscribed = true, durationDays = durationDays)
            _toastMessage.value = "Welcome to CreatorStream VIP! All exclusive videos unlocked."
        }
    }

    fun cancelSubscription() {
        viewModelScope.launch {
            repository.updateSubscription("FREE", isSubscribed = false, durationDays = 0)
            _toastMessage.value = "Subscription cancelled. Returned to Free tier."
        }
    }

    fun loginUser(name: String, email: String, isAdmin: Boolean) {
        viewModelScope.launch {
            repository.updateUserProfile(name, email)
            repository.updateUserRole(isAdmin)
            _toastMessage.value = if (isAdmin) {
                "Logged in as Admin ($name). Studio Upload unlocked!"
            } else {
                "Logged in as Viewer ($name). Get subscription to access premium videos!"
            }
        }
    }

    fun toggleRole(isCreator: Boolean) {
        viewModelScope.launch {
            repository.updateUserRole(isCreator)
            _toastMessage.value = if (isCreator) "Switched to Creator Studio mode" else "Switched to Public Viewer mode"
        }
    }

    fun updateProfile(name: String, email: String) {
        viewModelScope.launch {
            repository.updateUserProfile(name, email)
            _toastMessage.value = "Profile saved"
        }
    }

    // VIP Pass Customization Methods
    fun setMonthlyPrice(price: Double) {
        val p = (Math.round(price * 100.0) / 100.0).coerceAtLeast(0.0)
        _customization.value = _customization.value.copy(monthlyPrice = p)
        _toastMessage.value = "Monthly VIP Pass price set to ${if (p == 0.0) "FREE ($0.00)" else "$$p"}"
    }

    fun adjustMonthlyPrice(delta: Double) {
        val newPrice = (_customization.value.monthlyPrice + delta).coerceAtLeast(0.0)
        setMonthlyPrice(newPrice)
    }

    fun setAnnualPrice(price: Double) {
        val p = (Math.round(price * 100.0) / 100.0).coerceAtLeast(0.0)
        _customization.value = _customization.value.copy(annualPrice = p)
        _toastMessage.value = "Annual VIP Pass price set to ${if (p == 0.0) "FREE ($0.00)" else "$$p"}"
    }

    fun adjustAnnualPrice(delta: Double) {
        val newPrice = (_customization.value.annualPrice + delta).coerceAtLeast(0.0)
        setAnnualPrice(newPrice)
    }

    fun toggleVipFreePromo(isFree: Boolean) {
        _customization.value = _customization.value.copy(isVipFreePromo = isFree)
        _toastMessage.value = if (isFree) {
            "🎉 VIP Pass is now 100% FREE for everyone!"
        } else {
            "VIP Pass subscription restored to regular access"
        }
    }

    fun adjustVipDurationDays(deltaDays: Int) {
        val currentDays = _customization.value.customVipDays
        val newDays = (currentDays + deltaDays).coerceAtLeast(1)
        _customization.value = _customization.value.copy(customVipDays = newDays)
        viewModelScope.launch {
            if (uiState.value.userAccount.isSubscribed) {
                repository.updateSubscription(
                    tier = uiState.value.userAccount.subscriptionTier,
                    isSubscribed = true,
                    durationDays = newDays
                )
            }
        }
        _toastMessage.value = "VIP Pass validity adjusted to $newDays days"
    }

    fun grantFreeVipPass(days: Int = 365) {
        viewModelScope.launch {
            repository.updateSubscription("FREE VIP PASS", isSubscribed = true, durationDays = days)
            _toastMessage.value = "🎁 Free VIP Pass activated for $days days!"
        }
    }

    fun makeAllVideosFree(allFree: Boolean) {
        _customization.value = _customization.value.copy(areAllVideosFree = allFree)
        viewModelScope.launch {
            val videos = uiState.value.allVideos
            videos.forEach { video ->
                if (allFree && video.isPremium) {
                    repository.updateVideo(video.copy(isPremium = false))
                }
            }
            _toastMessage.value = if (allFree) "All videos are now 100% Free!" else "Video pricing rules updated"
        }
    }

    // App & Playback Customization Methods
    fun updatePlaybackSpeed(speed: Float) {
        _customization.value = _customization.value.copy(defaultPlaybackSpeed = speed)
        _toastMessage.value = "Default speed set to ${speed}x"
    }

    fun updateDefaultQuality(quality: String) {
        _customization.value = _customization.value.copy(defaultQuality = quality)
        _toastMessage.value = "Streaming quality set to $quality"
    }

    fun toggleAutoPlayNext(enabled: Boolean) {
        _customization.value = _customization.value.copy(autoPlayNext = enabled)
        _toastMessage.value = if (enabled) "Auto-play enabled" else "Auto-play disabled"
    }

    fun toggleUltraFastMode(enabled: Boolean) {
        _customization.value = _customization.value.copy(ultraFastMode = enabled)
        _toastMessage.value = if (enabled) "Ultra-Fast Smooth Mode enabled" else "Standard mode"
    }

    fun resetCustomization() {
        _customization.value = CustomizationSettings()
        _toastMessage.value = "Customization settings restored to default"
    }

    fun resetDemoData() {
        viewModelScope.launch {
            repository.resetDemoData()
            _activeVideoId.value = null
            _toastMessage.value = "Sample videos reset successfully"
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }
}

class StreamViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StreamViewModel::class.java)) {
            return StreamViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
