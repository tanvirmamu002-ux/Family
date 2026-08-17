package com.example.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class ContentProtectionService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val packageName = event.packageName?.toString() ?: ""
        
        // Monitor system settings or app uninstallation attempts
        if (packageName.contains("settings") || packageName.contains("packageinstaller")) {
            Log.d("ContentProtection", "Monitored settings window opened: $packageName")
            // Protection check logic: Log event or restrict sensitive system configuration
        }
    }

    override fun onInterrupt() {
        Log.d("ContentProtection", "Service Interrupted")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("ContentProtection", "Family Guard Protection Service Connected and active.")
    }
}
