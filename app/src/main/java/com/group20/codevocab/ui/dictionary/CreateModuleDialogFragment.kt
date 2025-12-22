package com.group20.codevocab.ui.dictionary

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
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
            val moduleName = binding.etModuleName.text.toString().trim()
            if (moduleName.isNotEmpty()) {
                // Pass the module name back to the fragment via FragmentResult API
                setFragmentResult(REQUEST_KEY, bundleOf(BUNDLE_KEY_NAME to moduleName))
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

    companion object {
        const val REQUEST_KEY = "request_create_module"
        const val BUNDLE_KEY_NAME = "bundle_key_module_name"
    }
}