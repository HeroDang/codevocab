package com.group20.codevocab.ui.quiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.group20.codevocab.databinding.ItemMistakeReviewBinding
import com.group20.codevocab.model.QuizMistake

/**
 * A simple adapter that displays a given list of mistakes.
 * It has no internal logic for expanding or limiting items.
 */
class MistakesAdapter(private val mistakes: List<QuizMistake>) : RecyclerView.Adapter<MistakesAdapter.MistakeViewHolder>() {

    inner class MistakeViewHolder(private val binding: ItemMistakeReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mistake: QuizMistake) {
            binding.chipQuestionNum.visibility = View.GONE
            binding.tvMistakeQuestion.text = mistake.question.question
            binding.tvYourAnswer.text = "Your answer: ${mistake.yourAnswer}"
            binding.tvCorrectAnswer.text = "Correct answer: ${mistake.question.correctAnswer}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MistakeViewHolder {
        val binding = ItemMistakeReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MistakeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MistakeViewHolder, position: Int) {
        holder.bind(mistakes[position])
    }

    override fun getItemCount(): Int {
        return mistakes.size
    }
}