package com.bitchat.android

import android.content.Context
import com.bitchat.android.mesh.BluetoothMeshService

object MeshServiceHolder {
    @Volatile
    private var instance: BluetoothMeshService? = null

    fun getInstance(context: Context): BluetoothMeshService {
        return instance ?: synchronized(this) {
            instance ?: BluetoothMeshService(context.applicationContext).also { instance = it }
        }
    }
}
