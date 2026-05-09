package com.group20.codevocab.data.remote

import android.os.Build
import com.group20.codevocab.BuildConfig

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
    // Host configurations
    // private const val EMULATOR_HOST = "10.0.2.2"
    // private const val REAL_DEVICE_HOST = "172.16.0.248"

    // Lấy giá trị từ BuildConfig thay vì hardcode
    private val EMULATOR_HOST = BuildConfig.EMULATOR_HOST
    private val REAL_DEVICE_HOST = BuildConfig.REAL_DEVICE_HOST

    // Ports
    private const val PORT_MAIN = "8000"
    private const val PORT_OCR = "8001"
    private const val PORT_AI = "8002"

    // Helper to get the current host based on environment
    private val currentHost: String
        get() = if (isEmulator()) EMULATOR_HOST else REAL_DEVICE_HOST

    // Dynamic Base URLs
    val BASE_URL: String
        get() = "http://$currentHost:$PORT_MAIN/"

    val OCR_BASE_URL: String
        get() = "http://$currentHost:$PORT_OCR/"

    val AI_BASE_URL: String
        get() = "http://$currentHost:$PORT_AI/"
}