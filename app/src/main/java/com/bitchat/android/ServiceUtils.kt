package com.bitchat.android

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

fun startMeshForegroundService(context: Context) {
    val intent = Intent(context, ForegroundMeshService::class.java)
    ContextCompat.startForegroundService(context, intent)
}

fun stopMeshForegroundService(context: Context) {
    val intent = Intent(context, ForegroundMeshService::class.java)
    context.stopService(intent)
}

@Suppress("DEPRECATION")
fun isServiceRunning(context: Context, serviceClass: Class<out Service>): Boolean {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
    manager?.getRunningServices(Int.MAX_VALUE)?.forEach { service ->
        if (serviceClass.name == service.service.className) {
            return true
        }
    }
    return false
}
