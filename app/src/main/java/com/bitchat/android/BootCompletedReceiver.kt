package com.bitchat.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.bitchat.android.ui.PREF_AUTO_START_MESH_SERVICE
import com.bitchat.android.ui.PREF_START_ON_BOOT
import android.util.Log

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_LOCKED_BOOT_COMPLETED
        ) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val persistent = prefs.getBoolean(PREF_AUTO_START_MESH_SERVICE, false)
            val startOnBoot = prefs.getBoolean(PREF_START_ON_BOOT, false)
            if (persistent && startOnBoot) {
                Log.d("BootCompletedReceiver", "Starting mesh service on boot")
                val appContext = context.applicationContext
                val serviceIntent = Intent(appContext, ForegroundMeshService::class.java)
                ContextCompat.startForegroundService(appContext, serviceIntent)
            } else {
                Log.d(
                    "BootCompletedReceiver",
                    "Not starting service - persistent=$persistent startOnBoot=$startOnBoot"
                )
            }
        }
    }
}
