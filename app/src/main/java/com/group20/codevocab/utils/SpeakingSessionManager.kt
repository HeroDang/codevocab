package com.group20.codevocab.utils

import com.group20.codevocab.model.SpeakingResult

/**
 * Singleton để quản lý dữ liệu phiên luyện tập nói.
 */
object SpeakingSessionManager {
    private val results = mutableListOf<SpeakingResult>()

    /**
     * Thêm hoặc cập nhật kết quả.
     * Nếu đã tồn tại kết quả cho câu này, sẽ ghi đè lên kết quả cũ.
     */
    fun addOrUpdateResult(result: SpeakingResult) {
        val existingIndex = results.indexOfFirst { it.originalSentence == result.originalSentence }
        if (existingIndex != -1) {
            results[existingIndex] = result
        } else {
            results.add(result)
        }
    }

    /**
     * Lấy danh sách kết quả hiện tại.
     */
    fun getResults(): List<SpeakingResult> {
        return results.toList()
    }

    /**
     * Xóa sạch dữ liệu phiên luyện tập (gọi khi bắt đầu phiên mới).
     */
    fun clearSession() {
        results.clear()
    }
}
