package com.bitchat.android.util

import android.content.Context

private const val PREF_NAME = "bitchat_prefs"
private const val KEY_PEER_ID = "mesh_peer_id"

/**
 * Simple helper for storing and retrieving the mesh peer ID.
 */
object PeerIdStore {
    fun getOrCreatePeerId(context: Context, generator: () -> String): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val existing = prefs.getString(KEY_PEER_ID, null)
        return if (existing != null) {
            existing
        } else {
            val newId = generator()
            prefs.edit().putString(KEY_PEER_ID, newId).apply()
            newId
        }
    }
}
