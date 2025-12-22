package com.group20.codevocab.ui.quiz

import android.content.Context
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import com.group20.codevocab.R
import com.group20.codevocab.databinding.ActivityQuizBinding
import com.group20.codevocab.model.QuizQuestion

/**
 * A helper class to adapt a QuizQuestion to the ActivityQuizBinding.
 * This is not a RecyclerView adapter, but a helper to keep the Activity code clean.
 */
class QuizAdapter(private val binding: ActivityQuizBinding, private val context: Context) {

    private val radioButtons: List<RadioButton> = listOf(
        binding.option1,
        binding.option2,
        binding.option3,
        binding.option4
    )

    /**
     * Binds a QuizQuestion to the UI elements.
     */
    fun bindQuestion(question: QuizQuestion) {
        resetRadioButtons()
        binding.tvQuestion.text = question.question
        for (i in radioButtons.indices) {
            radioButtons[i].text = question.options.getOrNull(i) ?: ""
        }
    }

    /**
     * Shows the result of the answer by changing the background of the radio buttons.
     */
    fun showResult(correctAnswerIndex: Int, selectedOptionIndex: Int) {
        for (i in radioButtons.indices) {
            val button = radioButtons[i]

            when (i) {
                correctAnswerIndex -> {
                    // Always show the correct answer in green
                    button.setBackgroundResource(R.drawable.bg_radio_correct)
                }
                selectedOptionIndex -> {
                    // If the selected answer is not the correct one, show it in red
                    button.setBackgroundResource(R.drawable.bg_radio_incorrect)
                }
                else -> {
                    // Reset other buttons to the default state
                    button.setBackgroundResource(R.drawable.bg_radio_option)
                }
            }
            button.isEnabled = false // Disable all buttons after submission
        }
    }

    /**
     * Resets the radio buttons to their default state for the next question.
     */
    private fun resetRadioButtons() {
        binding.radioGroupOptions.clearCheck()
        for (button in radioButtons) {
            button.setBackgroundResource(R.drawable.bg_radio_option)
            button.isEnabled = true
        }
    }
}