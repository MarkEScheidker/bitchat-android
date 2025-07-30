package com.bitchat.android.boot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bitchat.android.mesh.ForegroundMeshService
import com.bitchat.android.mesh.MeshServiceHolder
import com.bitchat.android.util.SettingsConstants

/**
 * Receives BOOT_COMPLETED to start the mesh service when persistent
 * connections are enabled.
 */
class BootBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = context.getSharedPreferences("bitchat_prefs", Context.MODE_PRIVATE)
            if (prefs.getBoolean(SettingsConstants.PREF_PERSISTENT_CONNECTION, false)) {
                // Ensure mesh service instance exists and start foreground service
                MeshServiceHolder.getInstance(context)
                val serviceIntent = Intent(context, ForegroundMeshService::class.java)
                context.startForegroundService(serviceIntent)
            }
        }
    }
}
