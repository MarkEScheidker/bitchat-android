package com.bitchat.android.mesh

import android.content.Context
import com.bitchat.android.util.PeerIdStore

/**
 * Singleton holder for [BluetoothMeshService]. Ensures a single instance
 * is shared across the application and the foreground service.
 */
object MeshServiceHolder {
    @Volatile
    private var instance: BluetoothMeshService? = null

    fun getInstance(context: Context): BluetoothMeshService {
        return instance ?: synchronized(this) {
            instance ?: run {
                val store = PeerIdStore(context.applicationContext)
                BluetoothMeshService(context.applicationContext, store.loadPeerId()).also {
                    instance = it
                }
            }
        }
    }
}
