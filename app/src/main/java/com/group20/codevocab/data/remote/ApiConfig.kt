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
    // Main Backend URLs
    private const val EMULATOR_URL = "http://10.0.2.2:8000/"
    private const val REAL_DEVICE_URL = "http://192.168.1.175:8000/"

    // OCR Service URLs
    private const val EMULATOR_OCR_URL = "http://10.0.2.2:8001/"
    private const val REAL_DEVICE_OCR_URL = "http://192.168.1.87:8001/"

    // Dynamic Base URL for the main backend
    val BASE_URL: String
        get() = if (isEmulator()) EMULATOR_URL else REAL_DEVICE_URL

    // Dynamic Base URL for the OCR service
    val OCR_BASE_URL: String
        get() = if (isEmulator()) EMULATOR_OCR_URL else REAL_DEVICE_OCR_URL
}