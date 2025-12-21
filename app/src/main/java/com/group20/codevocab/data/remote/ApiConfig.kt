package com.group20.codevocab.data.remote

import android.os.Build

fun isEmulator(): Boolean {
    return (
            Build.FINGERPRINT.startsWith("generic")
                    || Build.FINGERPRINT.lowercase().contains("vbox")
                    || Build.FINGERPRINT.lowercase().contains("test-keys")
                    || Build.MODEL.contains("Emulator")
                    || Build.MODEL.contains("Android SDK built for")
                    || Build.MANUFACTURER.contains("Genymotion")
                    || Build.HARDWARE.contains("goldfish")
                    || Build.HARDWARE.contains("ranchu")   // ✅ QUAN TRỌNG
                    || Build.DEVICE.startsWith("generic")
            )
}

object ApiConfig {
    private const val EMULATOR_URL = "http://10.0.2.2:8000/"
    private const val REAL_DEVICE_URL = "http://192.168.0.215:8000/"

    // Emulator AVD -> host machine
    val BASE_URL: String
        get() = if (isEmulator()) EMULATOR_URL else REAL_DEVICE_URL
    // Máy thật (nếu cần): "http://192.168.1.10:8000/"
}