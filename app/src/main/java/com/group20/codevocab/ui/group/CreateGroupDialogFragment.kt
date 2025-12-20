package com.group20.codevocab.ui.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.group20.codevocab.R
import com.group20.codevocab.databinding.DialogCreateGroupBinding
import com.google.android.material.color.MaterialColors

class CreateGroupDialogFragment : BottomSheetDialogFragment() {

    private var _binding: DialogCreateGroupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using a context wrapper that enforces the M3 theme
        val contextThemeWrapper = com.google.android.material.theme.overlay.MaterialThemeOverlay.wrap(requireContext(), null, 0, R.style.Theme_CodeVocab)
        _binding = DialogCreateGroupBinding.inflate(inflater.cloneInContext(contextThemeWrapper), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}