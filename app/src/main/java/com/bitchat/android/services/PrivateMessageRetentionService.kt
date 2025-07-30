package com.bitchat.android.services

import android.content.Context
import android.util.Log
import com.bitchat.android.model.BitchatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * Simple persistence layer for private messages. Each peer gets its own file
 * inside the app's private storage. This keeps messages available when the UI
 * process is restarted while the foreground service continues running.
 */
class PrivateMessageRetentionService private constructor(private val context: Context) {

    companion object {
        private const val TAG = "PrivateMessageRetentionService"
        @Volatile
        private var INSTANCE: PrivateMessageRetentionService? = null

        fun getInstance(context: Context): PrivateMessageRetentionService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PrivateMessageRetentionService(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val retentionDir = File(context.filesDir, "private_messages")

    init {
        if (!retentionDir.exists()) {
            retentionDir.mkdirs()
        }
    }

    suspend fun saveMessage(peerID: String, message: BitchatMessage) = withContext(Dispatchers.IO) {
        try {
            val file = getPeerFile(peerID)
            val existing = loadMessagesFromFile(file).toMutableList()
            if (existing.none { it.id == message.id }) {
                existing.add(message)
                existing.sortBy { it.timestamp }
                saveMessagesToFile(file, existing)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save message for $peerID", e)
        }
    }

    suspend fun loadMessages(peerID: String): List<BitchatMessage> = withContext(Dispatchers.IO) {
        try {
            val file = getPeerFile(peerID)
            return@withContext loadMessagesFromFile(file)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load messages for $peerID", e)
            emptyList()
        }
    }

    fun getPeers(): List<String> {
        return retentionDir.listFiles()?.mapNotNull { file ->
            val name = file.name
            if (name.startsWith("peer_") && name.endsWith(".dat")) {
                name.removePrefix("peer_").removeSuffix(".dat")
            } else null
        } ?: emptyList()
    }

    private fun getPeerFile(peerID: String): File = File(retentionDir, "peer_${peerID}.dat")

    private fun loadMessagesFromFile(file: File): List<BitchatMessage> {
        if (!file.exists()) return emptyList()
        return try {
            FileInputStream(file).use { fis ->
                ObjectInputStream(fis).use { ois ->
                    @Suppress("UNCHECKED_CAST")
                    ois.readObject() as List<BitchatMessage>
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to load messages from ${file.name}", e)
            emptyList()
        }
    }

    private fun saveMessagesToFile(file: File, messages: List<BitchatMessage>) {
        FileOutputStream(file).use { fos ->
            ObjectOutputStream(fos).use { oos ->
                oos.writeObject(messages)
            }
        }
    }
}
