package com.example.data

import android.content.Context
import android.content.SharedPreferences
import java.util.UUID
import kotlin.random.Random

class AppPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("family_guard_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_DEVICE_ID = "key_device_id"
        private const val KEY_MASTER_PIN = "key_master_pin"
        private const val KEY_DNS_ACTIVE = "key_dns_active"
        private const val KEY_ACCESSIBILITY_ENABLED = "key_accessibility_enabled"
        private const val KEY_ADULT_BLOCK_ACTIVE = "key_adult_block_active"
        private const val KEY_DEVICE_MODE = "key_device_mode"
    }

    init {
        if (!prefs.contains(KEY_DEVICE_ID)) {
            val randomNum = Random.nextInt(1000, 9999)
            val randomLetters = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ").toList().shuffled().take(3).joinToString("")
            val generatedId = "FAM-$randomNum-$randomLetters"
            prefs.edit().putString(KEY_DEVICE_ID, generatedId).apply()
        }
    }

    val deviceId: String
        get() = prefs.getString(KEY_DEVICE_ID, "FAM-8839-XYZ") ?: "FAM-8839-XYZ"

    var masterPin: String
        get() = prefs.getString(KEY_MASTER_PIN, "1234") ?: "1234"
        set(value) = prefs.edit().putString(KEY_MASTER_PIN, value).apply()

    var isDnsActive: Boolean
        get() = prefs.getBoolean(KEY_DNS_ACTIVE, true)
        set(value) = prefs.edit().putBoolean(KEY_DNS_ACTIVE, value).apply()

    var isAccessibilityEnabled: Boolean
        get() = prefs.getBoolean(KEY_ACCESSIBILITY_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_ACCESSIBILITY_ENABLED, value).apply()

    var isAdultBlockActive: Boolean
        get() = prefs.getBoolean(KEY_ADULT_BLOCK_ACTIVE, true)
        set(value) = prefs.edit().putBoolean(KEY_ADULT_BLOCK_ACTIVE, value).apply()

    var deviceMode: String
        get() = prefs.getString(KEY_DEVICE_MODE, "Child Mode") ?: "Child Mode"
        set(value) = prefs.edit().putString(KEY_DEVICE_MODE, value).apply()
}
