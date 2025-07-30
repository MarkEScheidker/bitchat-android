package com.bitchat.android.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Simple helper for persisting the mesh peer ID using [SharedPreferences].
 */
class PeerIdStore(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("bitchat_prefs", Context.MODE_PRIVATE)

    fun loadPeerId(): String? = prefs.getString(SettingsConstants.PREF_PEER_ID, null)

    fun savePeerId(peerId: String) {
        prefs.edit().putString(SettingsConstants.PREF_PEER_ID, peerId).apply()
    }
}
