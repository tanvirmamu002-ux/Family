package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.service.FamilyGuardForegroundService
import com.example.ui.components.BottomNavBar
import com.example.ui.components.MasterPinDialog
import com.example.ui.components.NavTab
import com.example.ui.components.TopAppBarHeader
import com.example.ui.screens.AiChatScreen
import com.example.ui.screens.AiPromptsScreen
import com.example.ui.screens.GalleryScreen
import com.example.ui.screens.ParentDashboardScreen
import com.example.ui.screens.SecurityScreen
import com.example.ui.screens.VideoHubScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                // Request runtime permissions on launch & start Foreground Service
                val context = LocalContext.current
                val permissionsToRequest = mutableListOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ).apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        add(Manifest.permission.POST_NOTIFICATIONS)
                        add(Manifest.permission.READ_MEDIA_IMAGES)
                    } else {
                        add(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }.toTypedArray()

                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) {
                    FamilyGuardForegroundService.startProtectionService(context)
                }

                LaunchedEffect(Unit) {
                    val allGranted = permissionsToRequest.all {
                        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
                    }
                    if (!allGranted) {
                        permissionLauncher.launch(permissionsToRequest)
                    } else {
                        FamilyGuardForegroundService.startProtectionService(context)
                    }
                }

                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val isMasterPinDialogOpen by viewModel.isMasterPinDialogOpen.collectAsStateWithLifecycle()
    val isParentDashboardUnlocked by viewModel.isParentDashboardUnlocked.collectAsStateWithLifecycle()
    val deviceId by viewModel.deviceId.collectAsStateWithLifecycle()
    val masterPin by viewModel.masterPin.collectAsStateWithLifecycle()
    val isDnsActive by viewModel.isDnsActive.collectAsStateWithLifecycle()
    val isAdultBlockActive by viewModel.isAdultBlockActive.collectAsStateWithLifecycle()
    val isAccessibilityEnabled by viewModel.isAccessibilityEnabled.collectAsStateWithLifecycle()
    val dnsStatus by viewModel.dnsStatus.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val deviceMode by viewModel.deviceMode.collectAsStateWithLifecycle()

    if (isParentDashboardUnlocked) {
        // Fullscreen Parent Admin Dashboard View
        ParentDashboardScreen(
            devices = viewModel.connectedDevices,
            logs = viewModel.protectionLogs,
            photos = viewModel.galleryPhotos,
            categories = viewModel.galleryCategories,
            deviceMode = deviceMode,
            onSetDeviceMode = { viewModel.setDeviceMode(it) },
            onCloseDashboard = { viewModel.lockParentDashboard() }
        )
    } else {
        Scaffold(
            topBar = {
                if (currentTab != NavTab.AI_CHAT) {
                    TopAppBarHeader(
                        isDnsActive = isDnsActive,
                        onTriggerSecretPin = { viewModel.openMasterPinDialog() }
                    )
                }
            },
            bottomBar = {
                BottomNavBar(
                    currentTab = currentTab,
                    onTabSelected = { viewModel.selectTab(it) }
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent<NavTab>(
                    targetState = currentTab,
                    transitionSpec = {
                        val duration = 300
                        if (targetState.ordinal > initialState.ordinal) {
                            // Moving to right tab: slide in from right, slide out to left
                            (slideInHorizontally(animationSpec = tween(duration)) { width -> width / 3 } + fadeIn(animationSpec = tween(duration))) togetherWith
                                    (slideOutHorizontally(animationSpec = tween(duration)) { width -> -width / 3 } + fadeOut(animationSpec = tween(duration)))
                        } else {
                            // Moving to left tab: slide in from left, slide out to right
                            (slideInHorizontally(animationSpec = tween(duration)) { width -> -width / 3 } + fadeIn(animationSpec = tween(duration))) togetherWith
                                    (slideOutHorizontally(animationSpec = tween(duration)) { width -> width / 3 } + fadeOut(animationSpec = tween(duration)))
                        }
                    },
                    label = "TabTransition"
                ) { targetTab ->
                    when (targetTab) {
                        NavTab.GALLERY -> GalleryScreen(
                            deviceId = deviceId,
                            photos = viewModel.galleryPhotos,
                            categories = viewModel.galleryCategories
                        )
                        NavTab.AI_PROMPTS -> AiPromptsScreen(
                            prompts = viewModel.aiPrompts,
                            onSelectTab = { viewModel.selectTab(it) }
                        )
                        NavTab.VIDEO_HUB -> VideoHubScreen(
                            templates = viewModel.videoTemplates
                        )
                        NavTab.AI_CHAT -> AiChatScreen(
                            messages = chatMessages,
                            onSendMessage = { viewModel.sendChatMessage(it) }
                        )
                        NavTab.SECURITY -> SecurityScreen(
                            deviceId = deviceId,
                            isDnsActive = isDnsActive,
                            isAdultBlockActive = isAdultBlockActive,
                            isAccessibilityEnabled = isAccessibilityEnabled,
                            dnsStatus = dnsStatus,
                            onToggleAdultBlock = { viewModel.toggleAdultBlock(it) },
                            onToggleDns = { viewModel.toggleDns(it) }
                        )
                    }
                }
            }
        }
    }

    // Secret Master PIN Entry Dialog
    if (isMasterPinDialogOpen) {
        MasterPinDialog(
            correctPin = masterPin,
            onSuccess = { viewModel.unlockParentDashboard() },
            onDismiss = { viewModel.closeMasterPinDialog() }
        )
    }
}
