package com.group20.codevocab.ui.dictionary

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.group20.codevocab.databinding.DialogCreateModuleBinding

class CreateModuleDialogFragment : DialogFragment() {

    private var _binding: DialogCreateModuleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCreateModuleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Make the dialog background transparent to show the CardView's rounded corners
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.ivClose.setOnClickListener {
            dismiss()
        }

        binding.btnCreateModule.setOnClickListener {
            // TODO: Handle module creation logic
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}