package com.bitchat.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.bitchat.android.ui.PREF_AUTO_START_MESH_SERVICE
import android.util.Log

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val enabled = prefs.getBoolean(PREF_AUTO_START_MESH_SERVICE, false)
            if (enabled) {
                Log.d("BootCompletedReceiver", "Starting mesh service on boot")
                val serviceIntent = Intent(context, ForegroundMeshService::class.java)
                ContextCompat.startForegroundService(context, serviceIntent)
            }
        }
    }
}
