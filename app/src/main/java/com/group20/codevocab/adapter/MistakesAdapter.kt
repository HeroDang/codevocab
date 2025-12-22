package com.group20.codevocab.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.group20.codevocab.databinding.ItemMistakeReviewBinding
import com.group20.codevocab.model.QuizMistake

class MistakesAdapter(private var mistakes: MutableList<QuizMistake>) : RecyclerView.Adapter<MistakesAdapter.MistakeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MistakeViewHolder {
        val binding = ItemMistakeReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MistakeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MistakeViewHolder, position: Int) {
        holder.bind(mistakes[position], position)
    }

    override fun getItemCount(): Int = mistakes.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newMistakes: List<QuizMistake>) {
        mistakes.clear()
        mistakes.addAll(newMistakes)
        notifyDataSetChanged()
    }

    inner class MistakeViewHolder(private val binding: ItemMistakeReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mistake: QuizMistake, position: Int) {
            binding.chipQuestionNum.text = "Question ${position + 1}"
            binding.tvMistakeQuestion.text = mistake.question.question
            binding.tvYourAnswer.text = "Your answer: ${mistake.yourAnswer}"
            binding.tvCorrectAnswer.text = "Correct answer: ${mistake.question.correctAnswer}"
        }
    }
}