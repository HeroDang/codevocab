package com.group20.codevocab.ui.dictionary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.group20.codevocab.databinding.FragmentEditWordBinding
import com.group20.codevocab.model.ReviewableWord
import com.group20.codevocab.model.WordItem
import com.group20.codevocab.ui.common.speaker.Speaker
import com.group20.codevocab.ui.common.speaker.SpeakerFactory

class EditWordFragment : DialogFragment() {

    private var _binding: FragmentEditWordBinding? = null
    private val binding get() = _binding!!
    private var editingWord: ReviewableWord? = null
    
    // New variable for WordItem compatibility
    private var editingWordItem: WordItem? = null
    
    private lateinit var speaker: Speaker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        speaker = SpeakerFactory.create(requireContext())
        
        // Apply full screen style if used as Dialog
        if (showsDialog) {
             setStyle(STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
        }
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
            
            @Suppress("DEPRECATION")
            editingWordItem = it.getParcelable(ARG_WORD_ITEM)
        }

        setupToolbar()
        populateData()
        setupButtons()
        setupSpeakerIcon()
    }

    private fun populateData() {
        if (editingWord != null) {
            editingWord?.let { word ->
                binding.etWord.setText(word.textEn)
                binding.etIpa.setText(word.ipa)
                binding.etMeaning.setText(word.meaningVi)
                binding.etExample.setText(word.exampleSentence)
            }
        } else if (editingWordItem != null) {
            editingWordItem?.let { word ->
                binding.etWord.setText(word.textEn)
                binding.etIpa.setText(word.ipa)
                binding.etMeaning.setText(word.meaningVi)
                binding.etExample.setText(word.exampleSentence)
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.title = "Edit Word"
        binding.toolbar.setNavigationOnClickListener {
            if (showsDialog) {
                dismiss()
            } else {
                findNavController().navigateUp()
            }
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
            // Logic for ReviewableWord (old)
            if (editingWord != null) {
                val updatedWord = editingWord?.copy(
                    textEn = binding.etWord.text.toString(),
                    meaningVi = binding.etMeaning.text.toString(),
                    ipa = binding.etIpa.text.toString(),
                    exampleSentence = binding.etExample.text.toString()
                )

                if (updatedWord != null) {
                    setFragmentResult("editWordResult", bundleOf(
                        "originalWord" to editingWord,
                        "updatedWord" to updatedWord
                    ))
                }
                findNavController().navigateUp()
            } 
            // Logic for WordItem (new)
            else if (editingWordItem != null) {
                val updatedWordItem = editingWordItem?.copy(
                    textEn = binding.etWord.text.toString(),
                    meaningVi = binding.etMeaning.text.toString(),
                    ipa = binding.etIpa.text.toString(),
                    exampleSentence = binding.etExample.text.toString()
                )
                
                if (updatedWordItem != null) {
                    setFragmentResult(REQUEST_KEY, bundleOf(
                        BUNDLE_KEY_UPDATED_WORD to updatedWordItem
                    ))
                }
                if (showsDialog) {
                    dismiss()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::speaker.isInitialized) {
            speaker.shutdown()
        }
        _binding = null
    }
    
    companion object {
        // Constants required by WordListActivity
        const val TAG = "EditWordFragment"
        const val REQUEST_KEY = "edit_word_request"
        const val BUNDLE_KEY_UPDATED_WORD = "updated_word"
        const val ARG_WORD_ITEM = "word_item"

        fun newInstance(wordItem: WordItem): EditWordFragment {
            val fragment = EditWordFragment()
            val args = Bundle()
            args.putParcelable(ARG_WORD_ITEM, wordItem)
            fragment.arguments = args
            return fragment
        }
    }
}
