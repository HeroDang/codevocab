package com.group20.codevocab.ui.dictionary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.group20.codevocab.databinding.FragmentEditWordBinding

class EditWordFragment : Fragment() {

    private var _binding: FragmentEditWordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditWordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupButtons()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupButtons() {
        binding.btnSaveWord.setOnClickListener {
            // TODO: Handle saving the edited word
            findNavController().navigateUp() // Go back for now
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}