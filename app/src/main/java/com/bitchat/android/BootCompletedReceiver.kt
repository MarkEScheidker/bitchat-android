package com.bitchat.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.preference.PreferenceManager
import androidx.core.content.ContextCompat
import android.os.Build
import android.content.pm.PackageManager

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val enabled = prefs.getBoolean(PREF_AUTO_START_MESH_SERVICE, false)
            if (enabled) {
                val hasPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                if (hasPermission) {
                    val serviceIntent = Intent(context, ForegroundMeshService::class.java)
                    ContextCompat.startForegroundService(context, serviceIntent)
                }
            }
        }
    }
}
