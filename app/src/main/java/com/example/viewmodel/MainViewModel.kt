package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.R
import com.example.data.AppPreferences
import com.example.model.AiPromptItem
import com.example.model.ChatMessage
import com.example.model.ConnectedDevice
import com.example.model.GalleryCategory
import com.example.model.GalleryPhoto
import com.example.model.ProtectionLog
import com.example.model.VideoTemplate
import com.example.service.DnsSecurityManager
import com.example.service.DnsStatus
import com.example.ui.components.NavTab
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = AppPreferences(application)

    private val _currentTab = MutableStateFlow(NavTab.GALLERY)
    val currentTab: StateFlow<NavTab> = _currentTab.asStateFlow()

    private val _isMasterPinDialogOpen = MutableStateFlow(false)
    val isMasterPinDialogOpen: StateFlow<Boolean> = _isMasterPinDialogOpen.asStateFlow()

    private val _isParentDashboardUnlocked = MutableStateFlow(false)
    val isParentDashboardUnlocked: StateFlow<Boolean> = _isParentDashboardUnlocked.asStateFlow()

    private val _deviceId = MutableStateFlow(prefs.deviceId)
    val deviceId: StateFlow<String> = _deviceId.asStateFlow()

    private val _masterPin = MutableStateFlow(prefs.masterPin)
    val masterPin: StateFlow<String> = _masterPin.asStateFlow()

    private val _isDnsActive = MutableStateFlow(prefs.isDnsActive)
    val isDnsActive: StateFlow<Boolean> = _isDnsActive.asStateFlow()

    private val _isAdultBlockActive = MutableStateFlow(prefs.isAdultBlockActive)
    val isAdultBlockActive: StateFlow<Boolean> = _isAdultBlockActive.asStateFlow()

    private val _isAccessibilityEnabled = MutableStateFlow(prefs.isAccessibilityEnabled)
    val isAccessibilityEnabled: StateFlow<Boolean> = _isAccessibilityEnabled.asStateFlow()

    private val _deviceMode = MutableStateFlow(prefs.deviceMode)
    val deviceMode: StateFlow<String> = _deviceMode.asStateFlow()

    private val _dnsStatus = MutableStateFlow(DnsSecurityManager.checkDnsStatus(application))
    val dnsStatus: StateFlow<DnsStatus> = _dnsStatus.asStateFlow()

    // Sample Data Sets matching screenshot
    val galleryCategories = listOf(
        GalleryCategory("1", "Camera", 245, "ic_camera"),
        GalleryCategory("2", "Screenshots", 88, "ic_screenshots"),
        GalleryCategory("3", "Downloads", 56, "ic_downloads"),
        GalleryCategory("4", "WhatsApp Images", 120, "ic_whatsapp"),
        GalleryCategory("5", "Instagram", 78, "ic_instagram"),
        GalleryCategory("6", "Other", 34, "ic_other")
    )

    val galleryPhotos = listOf(
        GalleryPhoto("p1", "Family Trip Outdoor", R.drawable.img_family_hero, category = "Camera"),
        GalleryPhoto("p2", "Family Portrait Smiling", R.drawable.img_family_hero, category = "Camera"),
        GalleryPhoto("p3", "Sunset Together", R.drawable.img_video_template_love, category = "Camera"),
        GalleryPhoto("p4", "Home Modern View", R.drawable.img_modern_house, category = "Camera"),
        GalleryPhoto("p5", "Child Creative Art", R.drawable.img_ai_robot, category = "Downloads"),
        GalleryPhoto("p6", "Family Shield Icon", R.drawable.img_app_icon, category = "Screenshots")
    )

    val aiPrompts = listOf(
        AiPromptItem(
            id = "a1",
            title = "Cute Robot in Space",
            category = "Digital Art",
            promptText = "A cute robot floating in space, dreamy background, colorful planets, ultra detailed, 3D render.",
            likesCount = "1.2K",
            imageResId = R.drawable.img_ai_robot,
            tag = "Trending"
        ),
        AiPromptItem(
            id = "a2",
            title = "Modern House Design",
            category = "Architecture",
            promptText = "Modern luxury house at night, stunning lighting, ultra realistic, 8k render.",
            likesCount = "892",
            imageResId = R.drawable.img_modern_house,
            tag = "Popular"
        )
    )

    val videoTemplates = listOf(
        VideoTemplate("v1", "Love Story", "00:30", "Romantic", R.drawable.img_video_template_love),
        VideoTemplate("v2", "Birthday Wishes", "00:28", "Celebration", R.drawable.img_family_hero),
        VideoTemplate("v3", "Travel Memories", "00:32", "Adventure", R.drawable.img_modern_house),
        VideoTemplate("v4", "Cinematic Intro", "00:20", "Cinematic", R.drawable.img_ai_robot),
        VideoTemplate("v5", "Good Morning", "00:15", "Daily", R.drawable.img_app_icon),
        VideoTemplate("v6", "Festival Vibes", "00:25", "Events", R.drawable.img_family_hero)
    )

    val connectedDevices = listOf(
        ConnectedDevice("d1", "Rahat's Phone", "10:25 AM", "Online", isProtected = true),
        ConnectedDevice("d2", "Ammi's Phone", "09:40 AM", "Online", isProtected = true),
        ConnectedDevice("d3", "Tablet", "Yesterday", "Offline", isProtected = true)
    )

    val protectionLogs = listOf(
        ProtectionLog("l1", "10:25 AM", "DNS Filter: Active", "Rahat's Phone"),
        ProtectionLog("l2", "10:20 AM", "New Photo Synced", "Ammi's Phone"),
        ProtectionLog("l3", "10:18 AM", "Accessibility: Active", "Rahat's Phone"),
        ProtectionLog("l4", "10:15 AM", "Someone tried to disable permission (Blocked)", "Tablet")
    )

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "c1",
                text = "Hello! I'm your AI assistant. How can I help you today?",
                isUser = false,
                timestamp = "10:30 AM"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    fun selectTab(tab: NavTab) {
        _currentTab.value = tab
    }

    fun openMasterPinDialog() {
        _isMasterPinDialogOpen.value = true
    }

    fun closeMasterPinDialog() {
        _isMasterPinDialogOpen.value = false
    }

    fun unlockParentDashboard() {
        _isMasterPinDialogOpen.value = false
        _isParentDashboardUnlocked.value = true
    }

    fun lockParentDashboard() {
        _isParentDashboardUnlocked.value = false
    }

    fun toggleDns(active: Boolean) {
        prefs.isDnsActive = active
        _isDnsActive.value = active
    }

    fun toggleAdultBlock(active: Boolean) {
        prefs.isAdultBlockActive = active
        _isAdultBlockActive.value = active
    }

    fun setDeviceMode(mode: String) {
        prefs.deviceMode = mode
        _deviceMode.value = mode
    }

    fun sendChatMessage(userText: String) {
        if (userText.isBlank()) return

        val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        val userMsg = ChatMessage(
            id = System.currentTimeMillis().toString(),
            text = userText,
            isUser = true,
            timestamp = timeStr
        )

        val updatedList = _chatMessages.value + userMsg
        _chatMessages.value = updatedList

        viewModelScope.launch {
            val responseText = queryGeminiApi(userText)
            val aiMsg = ChatMessage(
                id = (System.currentTimeMillis() + 1).toString(),
                text = responseText,
                isUser = false,
                timestamp = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
            )
            _chatMessages.value = _chatMessages.value + aiMsg
        }
    }

    private suspend fun queryGeminiApi(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Smart fallback response if API key is unconfigured
            return@withContext when {
                prompt.contains("quantum", ignoreCase = true) ->
                    "Quantum computing uses qubits instead of bits. Qubits can be 0, 1, or both at the same time thanks to a property called superposition."
                prompt.contains("motivational", ignoreCase = true) || prompt.contains("quote", ignoreCase = true) ->
                    "\"Never give up, because great things take time—and every step forward, no matter how small, brings you closer to your dream.\""
                prompt.contains("privacy", ignoreCase = true) || prompt.contains("protect", ignoreCase = true) ->
                    "Family Guard uses Private DNS filtering and accessibility monitoring to block adult content and keep your children's device safe."
                else ->
                    "Family Guard AI is active! I can help you with prompt ideas, family gallery organization, or content safety rules."
            }
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)
            }

            val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val jsonRes = JSONObject(responseString)
                val candidates = jsonRes.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val first = candidates.getJSONObject(0)
                    val content = first.getJSONObject("content")
                    val parts = content.getJSONArray("parts")
                    if (parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).optString("text", "I'm here to assist you!")
                    }
                }
            }
            "Quantum computing uses qubits instead of bits. Qubits can be 0, 1, or both at the same time thanks to superposition."
        } catch (e: Exception) {
            "Family Guard AI: $prompt - Processing complete."
        }
    }
}
