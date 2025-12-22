package com.group20.codevocab.ui.dictionary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.group20.codevocab.databinding.FragmentEditWordBinding
import com.group20.codevocab.model.ReviewableWord
import com.group20.codevocab.ui.common.speaker.Speaker
import com.group20.codevocab.ui.common.speaker.SpeakerFactory

class EditWordFragment : Fragment() {

    private var _binding: FragmentEditWordBinding? = null
    private val binding get() = _binding!!
    private var editingWord: ReviewableWord? = null
    private lateinit var speaker: Speaker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        speaker = SpeakerFactory.create(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditWordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            @Suppress("DEPRECATION")
            editingWord = it.getParcelable("wordToEdit")
        }

        setupToolbar()
        populateData()
        setupButtons()
        setupSpeakerIcon()
    }

    private fun populateData() {
        editingWord?.let { word ->
            binding.etWord.setText(word.textEn)
            binding.etIpa.setText(word.ipa)
            binding.etMeaning.setText(word.meaningVi)
            binding.etExample.setText(word.exampleSentence)
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupSpeakerIcon() {
        binding.tilIpa.setEndIconOnClickListener {
            val wordToSpeak = binding.etWord.text.toString()
            if (wordToSpeak.isNotBlank()) {
                speaker.speak(wordToSpeak)
            }
        }
    }

    private fun setupButtons() {
        binding.btnSaveWord.setOnClickListener {
            val updatedWord = editingWord?.copy(
                textEn = binding.etWord.text.toString(),
                meaningVi = binding.etMeaning.text.toString(),
                ipa = binding.etIpa.text.toString(),
                exampleSentence = binding.etExample.text.toString()
            )

            if (editingWord != null && updatedWord != null) {
                setFragmentResult("editWordResult", bundleOf(
                    "originalWord" to editingWord,
                    "updatedWord" to updatedWord
                ))
            }
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::speaker.isInitialized) {
            speaker.shutdown()
        }
        _binding = null
    }
}