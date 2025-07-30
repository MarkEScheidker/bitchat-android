package com.bitchat.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bitchat.android.services.PrivateMessageRetentionService
import com.bitchat.android.ui.DataManager
import com.bitchat.android.ui.PREF_AUTO_START_MESH_SERVICE
import com.bitchat.android.ui.PREF_START_ON_BOOT

class ShutdownReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_SHUTDOWN) {
            val appContext = context.applicationContext
            // Preserve persistent settings while clearing everything else
            val dataManager = DataManager(appContext)
            dataManager.clearAllData(preservePersistentSettings = true)
            PrivateMessageRetentionService.getInstance(appContext).clearAllMessages()
        }
    }
}
