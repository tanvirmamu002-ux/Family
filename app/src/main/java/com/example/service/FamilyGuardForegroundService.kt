package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.R

class FamilyGuardForegroundService : Service() {

    companion object {
        private const val CHANNEL_ID = "family_guard_protection_channel"
        private const val NOTIFICATION_ID = 1001
        private const val TAG = "FamilyGuardService"

        fun startProtectionService(context: Context) {
            val intent = Intent(context, FamilyGuardForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopProtectionService(context: Context) {
            val intent = Intent(context, FamilyGuardForegroundService::class.java)
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = buildPersistentNotification()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            var serviceType = 0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                serviceType = ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA or
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE or
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            }
            startForeground(NOTIFICATION_ID, notification, serviceType)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        Log.d(TAG, "Family Guard Foreground Service started with transparent status bar notification.")
        
        // Listen for remote signals on-demand
        listenForRemoteCommands()

        return START_STICKY
    }

    private fun listenForRemoteCommands() {
        // Architecture for On-Demand Firebase Realtime Database command listener
        // Actions like "CAPTURE_SNAPSHOT" or "RECORD_AUDIO_CLIP" are triggered on signal
        // Hardware camera/mic are accessed on demand and closed immediately after execution.
        Log.d(TAG, "On-demand remote command listener initialized.")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Family Guard Protection Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows persistent protection status for child safety and parental controls."
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun buildPersistentNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Family Guard Protection Active")
            .setContentText("Parental control and child protection active in background")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Family Guard Foreground Service stopped.")
    }
}
