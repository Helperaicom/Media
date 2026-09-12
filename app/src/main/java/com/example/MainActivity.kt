package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.ui.StreamUiState
import com.example.ui.StreamViewModel
import com.example.ui.StreamViewModelFactory
import com.example.ui.components.AuthDialog
import com.example.ui.components.CustomizationHubDialog
import com.example.ui.components.SubscribeDialog
import com.example.ui.components.UploadVideoDialog
import com.example.ui.screens.CreatorStudioScreen
import com.example.ui.screens.DownloadsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.VideoDetailScreen
import com.example.ui.theme.BrandCrimson
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

enum class AppTab(val label: String) {
    HOME("Browse"),
    DOWNLOADS("Downloads"),
    STUDIO("Studio"),
    PROFILE("Profile")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext, lifecycleScope)
        val repository = AppRepository(database.appDao())

        setContent {
            MyApplicationTheme(darkTheme = true) {
                val viewModel: StreamViewModel = viewModel(
                    factory = StreamViewModelFactory(repository)
                )
                CreatorStreamApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CreatorStreamApp(viewModel: StreamViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var currentTab by remember { mutableStateOf(AppTab.HOME) }
    var showUploadDialog by remember { mutableStateOf(false) }
    var showSubscribeDialog by remember { mutableStateOf(false) }
    var showCustomizationDialog by remember { mutableStateOf(false) }
    var showAuthDialog by remember { mutableStateOf(false) }

    // Helper for attempting to upload: only admin/creator can upload
    val onAttemptUpload: () -> Unit = {
        if (uiState.userAccount.isCreator) {
            showUploadDialog = true
        } else {
            Toast.makeText(context, "Only Admin can upload videos! Please sign in as Admin.", Toast.LENGTH_LONG).show()
            showAuthDialog = true
        }
    }

    // Display feedback toast whenever toastMessage changes
    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    // If an active video is selected, show the full-screen VideoDetailScreen
    val activeVideo = uiState.activeVideo
    if (activeVideo != null) {
        VideoDetailScreen(
            video = activeVideo,
            uiState = uiState,
            onBack = { viewModel.selectVideo(null) },
            onVideoSelect = { v -> viewModel.selectVideo(v) },
            onToggleLike = { v -> viewModel.toggleLike(v) },
            onToggleDownload = { v -> viewModel.toggleDownload(v) },
            onOpenSubscribe = { showSubscribeDialog = true },
            onAddComment = { videoId, content ->
                viewModel.addComment(videoId, content, uiState.userAccount.name)
            }
        )
    } else {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .testTag("stream_main_scaffold"),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.testTag("stream_navigation_bar")
                ) {
                    NavigationBarItem(
                        selected = currentTab == AppTab.HOME,
                        onClick = { currentTab = AppTab.HOME },
                        icon = { Icon(Icons.Default.PlayCircleFilled, contentDescription = "Browse") },
                        label = { Text("Browse") },
                        modifier = Modifier.testTag("nav_item_browse")
                    )

                    NavigationBarItem(
                        selected = currentTab == AppTab.DOWNLOADS,
                        onClick = { currentTab = AppTab.DOWNLOADS },
                        icon = { Icon(Icons.Default.FileDownload, contentDescription = "Downloads") },
                        label = { Text("Downloads") },
                        modifier = Modifier.testTag("nav_item_downloads")
                    )

                    NavigationBarItem(
                        selected = currentTab == AppTab.STUDIO,
                        onClick = { currentTab = AppTab.STUDIO },
                        icon = { Icon(Icons.Default.VideoLibrary, contentDescription = "Studio") },
                        label = { Text("Studio") },
                        modifier = Modifier.testTag("nav_item_studio")
                    )

                    NavigationBarItem(
                        selected = currentTab == AppTab.PROFILE,
                        onClick = { currentTab = AppTab.PROFILE },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Account") },
                        modifier = Modifier.testTag("nav_item_profile")
                    )
                }
            },
            floatingActionButton = {
                // Quick Upload FAB on Home or Studio tab (only visible for Admin)
                if ((currentTab == AppTab.HOME && uiState.userAccount.isCreator) || currentTab == AppTab.STUDIO) {
                    FloatingActionButton(
                        onClick = onAttemptUpload,
                        containerColor = BrandCrimson,
                        contentColor = androidx.compose.ui.graphics.Color.White,
                        modifier = Modifier.testTag("upload_video_fab")
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = "Upload Video")
                    }
                }
            }
        ) { innerPadding ->
            when (currentTab) {
                AppTab.HOME -> {
                    HomeScreen(
                        uiState = uiState,
                        onVideoClick = { video -> viewModel.selectVideo(video) },
                        onDownloadClick = { video -> viewModel.toggleDownload(video) },
                        onCategorySelect = { cat -> viewModel.selectCategory(cat) },
                        onSearchChange = { q -> viewModel.setSearchQuery(q) },
                        onOpenSubscribe = { showSubscribeDialog = true },
                        onOpenUpload = onAttemptUpload,
                        onOpenCustomization = { showCustomizationDialog = true },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                AppTab.DOWNLOADS -> {
                    DownloadsScreen(
                        uiState = uiState,
                        onVideoClick = { video -> viewModel.selectVideo(video) },
                        onDeleteDownload = { video -> viewModel.toggleDownload(video) },
                        onExploreClick = { currentTab = AppTab.HOME },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                AppTab.STUDIO -> {
                    CreatorStudioScreen(
                        uiState = uiState,
                        onOpenUpload = onAttemptUpload,
                        onVideoClick = { video -> viewModel.selectVideo(video) },
                        onUpdateVideo = { video -> viewModel.updateVideo(video) },
                        onDeleteVideo = { video -> viewModel.deleteVideo(video) },
                        onOpenCustomization = { showCustomizationDialog = true },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                AppTab.PROFILE -> {
                    ProfileScreen(
                        uiState = uiState,
                        onOpenSubscribe = { showSubscribeDialog = true },
                        onOpenCustomization = { showCustomizationDialog = true },
                        onOpenAuth = { showAuthDialog = true },
                        onAdjustMonthlyPrice = { delta -> viewModel.adjustMonthlyPrice(delta) },
                        onToggleVipFreePromo = { free -> viewModel.toggleVipFreePromo(free) },
                        onAdjustVipDurationDays = { delta -> viewModel.adjustVipDurationDays(delta) },
                        onGrantFreeVipPass = { days -> viewModel.grantFreeVipPass(days) },
                        onToggleRole = { isCreator -> viewModel.toggleRole(isCreator) },
                        onUpdateProfile = { name, email -> viewModel.updateProfile(name, email) },
                        onResetDemo = { viewModel.resetDemoData() },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    // Upload Video Sheet
    if (showUploadDialog) {
        UploadVideoDialog(
            isUploading = uiState.isUploading,
            onDismiss = { showUploadDialog = false },
            onPublish = { title, desc, videoUri, thumbUri, duration, category, isPremium, tags ->
                viewModel.uploadVideo(
                    title = title,
                    description = desc,
                    videoUri = videoUri,
                    thumbnailUri = thumbUri,
                    duration = duration,
                    category = category,
                    isPremium = isPremium,
                    tags = tags
                )
            }
        )
    }

    // Subscription Sheet
    if (showSubscribeDialog) {
        SubscribeDialog(
            currentTier = uiState.userAccount.subscriptionTier,
            isSubscribed = uiState.userAccount.isSubscribed,
            onDismiss = { showSubscribeDialog = false },
            onSubscribe = { tier, days ->
                viewModel.subscribeToPlan(tier, days)
            },
            onCancelSubscription = {
                viewModel.cancelSubscription()
            },
            customization = uiState.customization,
            onOpenCustomization = { showCustomizationDialog = true }
        )
    }

    // Comprehensive Customization Hub Bottom Sheet
    if (showCustomizationDialog) {
        CustomizationHubDialog(
            customization = uiState.customization,
            userAccount = uiState.userAccount,
            onDismiss = { showCustomizationDialog = false },
            onSetMonthlyPrice = { price -> viewModel.setMonthlyPrice(price) },
            onAdjustMonthlyPrice = { delta -> viewModel.adjustMonthlyPrice(delta) },
            onSetAnnualPrice = { price -> viewModel.setAnnualPrice(price) },
            onAdjustAnnualPrice = { delta -> viewModel.adjustAnnualPrice(delta) },
            onToggleVipFreePromo = { free -> viewModel.toggleVipFreePromo(free) },
            onAdjustVipDurationDays = { delta -> viewModel.adjustVipDurationDays(delta) },
            onGrantFreeVipPass = { days -> viewModel.grantFreeVipPass(days) },
            onMakeAllVideosFree = { makeFree -> viewModel.makeAllVideosFree(makeFree) },
            onUpdatePlaybackSpeed = { speed -> viewModel.updatePlaybackSpeed(speed) },
            onUpdateDefaultQuality = { quality -> viewModel.updateDefaultQuality(quality) },
            onToggleAutoPlayNext = { auto -> viewModel.toggleAutoPlayNext(auto) },
            onToggleUltraFastMode = { fast -> viewModel.toggleUltraFastMode(fast) }
        )
    }

    // Sign In / Authentication Dialog
    if (showAuthDialog) {
        AuthDialog(
            currentName = uiState.userAccount.name,
            currentEmail = uiState.userAccount.email,
            onDismiss = { showAuthDialog = false },
            onLoginSuccess = { name, email, isAdmin ->
                viewModel.loginUser(name, email, isAdmin)
            }
        )
    }
}

// Preserve Greeting composable for backward compatibility with template screenshot test
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme { Greeting("CreatorStream") }
}
