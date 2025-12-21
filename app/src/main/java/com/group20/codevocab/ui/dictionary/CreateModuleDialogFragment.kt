package com.group20.codevocab.ui.dictionary

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.group20.codevocab.databinding.DialogCreateModuleBinding
import com.group20.codevocab.viewmodel.ModuleViewModel
import com.group20.codevocab.viewmodel.ModuleViewModelFactory

class CreateModuleDialogFragment : DialogFragment() {

    private var _binding: DialogCreateModuleBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ModuleViewModel

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
        
        // Initialize ViewModel
        val factory = ModuleViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[ModuleViewModel::class.java]

        binding.ivClose.setOnClickListener {
            dismiss()
        }

        binding.btnCreateModule.setOnClickListener {
            val moduleName = binding.etModuleName.text.toString().trim()
            if (moduleName.isNotEmpty()) {
                viewModel.createModuleLocal(moduleName)
                dismiss()
            } else {
                Toast.makeText(context, "Please enter module name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}