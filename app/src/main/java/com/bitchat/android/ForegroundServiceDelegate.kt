package com.bitchat.android

import android.content.Context
import com.bitchat.android.mesh.BluetoothMeshDelegate
import com.bitchat.android.model.BitchatMessage
import com.bitchat.android.model.DeliveryAck
import com.bitchat.android.model.ReadReceipt
import com.bitchat.android.ui.NotificationManager

/**
 * Delegate used by the foreground service to show basic notifications
 * when the app UI is not active.
 */
object ForegroundServiceDelegate : BluetoothMeshDelegate {
    private lateinit var notificationManager: NotificationManager

    fun init(context: Context) {
        notificationManager = NotificationManager(context.applicationContext)
        notificationManager.setAppBackgroundState(true)
    }

    fun attach(meshService: com.bitchat.android.mesh.BluetoothMeshService) {
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
        }
    }

    override fun didConnectToPeer(peerID: String) {}
    override fun didDisconnectFromPeer(peerID: String) {}
    override fun didUpdatePeerList(peers: List<String>) {}
    override fun didReceiveChannelLeave(channel: String, fromPeer: String) {}
    override fun didReceiveDeliveryAck(ack: DeliveryAck) {}
    override fun didReceiveReadReceipt(receipt: ReadReceipt) {}
    override fun decryptChannelMessage(encryptedContent: ByteArray, channel: String): String? = null
    override fun getNickname(): String? = null
    override fun isFavorite(peerID: String): Boolean = false
}
