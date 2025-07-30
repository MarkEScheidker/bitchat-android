package com.bitchat.android

import android.content.Context
import com.bitchat.android.mesh.BluetoothMeshDelegate
import com.bitchat.android.model.BitchatMessage
import com.bitchat.android.model.DeliveryAck
import com.bitchat.android.model.ReadReceipt
import com.bitchat.android.ui.NotificationManager
import com.bitchat.android.ui.DataManager
import androidx.preference.PreferenceManager
import com.bitchat.android.ui.PREF_AUTO_START_MESH_SERVICE
import com.bitchat.android.services.PrivateMessageRetentionService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Delegate used by the foreground service to show basic notifications
 * when the app UI is not active.
 */
object ForegroundServiceDelegate : BluetoothMeshDelegate {
    private lateinit var appContext: Context
    private lateinit var notificationManager: NotificationManager
    private lateinit var dataManager: DataManager
    private lateinit var messageStore: PrivateMessageRetentionService
    private var nickname: String? = null
    private var messageStoreScope: CoroutineScope? = null

    fun init(context: Context) {
        appContext = context.applicationContext
        notificationManager = NotificationManager(appContext)
        dataManager = DataManager(appContext)
        messageStore = PrivateMessageRetentionService.getInstance(appContext)
        messageStoreScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        nickname = dataManager.loadNickname()
        notificationManager.setAppBackgroundState(true)
    }

    fun attach(meshService: com.bitchat.android.mesh.BluetoothMeshService) {
        nickname = dataManager.loadNickname()
        if (messageStoreScope == null) {
            messageStoreScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        }

        val prefs = PreferenceManager.getDefaultSharedPreferences(appContext)
        val persistent = prefs.getBoolean(PREF_AUTO_START_MESH_SERVICE, false)
        if (persistent) {
            meshService.connectionManager.setAppBackgroundState(false)
            meshService.sendBroadcastAnnounce()
        }

        meshService.delegate = this
    }

    override fun didReceiveMessage(message: BitchatMessage) {
        if (message.isPrivate) {
            val peerID = message.senderPeerID ?: message.sender
            notificationManager.showPrivateMessageNotification(
                peerID,
                message.sender,
                message.content
            )
            // Persist the message so the UI can load it later
            messageStoreScope?.launch {
                messageStore.saveMessage(peerID, message)
            }
        }
    }

    override fun didConnectToPeer(peerID: String) {}
    override fun didDisconnectFromPeer(peerID: String) {}
    override fun didUpdatePeerList(peers: List<String>) {}
    override fun didReceiveChannelLeave(channel: String, fromPeer: String) {}
    override fun didReceiveDeliveryAck(ack: DeliveryAck) {}
    override fun didReceiveReadReceipt(receipt: ReadReceipt) {}
    override fun decryptChannelMessage(encryptedContent: ByteArray, channel: String): String? = null
    override fun getNickname(): String? = nickname
    override fun isFavorite(peerID: String): Boolean = false
}
