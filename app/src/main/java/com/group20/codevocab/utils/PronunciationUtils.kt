package com.group20.codevocab.utils
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import com.group20.codevocab.model.WordAnalysis

object PronunciationUtils {

    fun getFormattedSentence(
        originalSentence: String,
        analysis: List<WordAnalysis>?
    ): SpannableStringBuilder {
        val builder = SpannableStringBuilder()

        if (analysis.isNullOrEmpty()) {
            return builder.append(originalSentence)
        }

        analysis.forEachIndexed { index, wordData ->
            // Duyệt qua từng segment của từ
            wordData.segments.forEach { segment ->
                val start = builder.length
                builder.append(segment.text)

                if (!segment.isCorrect) {
                    // Tô đỏ đoạn phát âm sai (Màu đỏ Red-500)
                    builder.setSpan(
                        ForegroundColorSpan(Color.parseColor("#EF4444")),
                        start,
                        builder.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }

            // Thêm dấu cách giữa các từ
            if (index < analysis.size - 1) {
                builder.append(" ")
            }
        }

        // Xử lý dấu câu ở cuối (nếu API không trả về dấu câu trong list analysis)
        if (originalSentence.isNotEmpty() && !builder.endsWith(originalSentence.last())) {
            val lastChar = originalSentence.last()
            if (!lastChar.isLetterOrDigit()) {
                builder.append(lastChar)
            }
        }

        return builder
    }
}