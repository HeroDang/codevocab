package com.group20.codevocab.ui.dictionary

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.group20.codevocab.databinding.DialogRenameModuleBinding

class RenameModuleDialogFragment : DialogFragment() {

    private var _binding: DialogRenameModuleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogRenameModuleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Make the dialog background transparent
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Get module name from arguments and set it
        val moduleName = arguments?.getString(ARG_MODULE_NAME)
        binding.etModuleName.setText(moduleName)

        binding.ivClose.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            val newName = binding.etModuleName.text.toString().trim()

            if (newName.isNotEmpty()) {
                // Return the result to the parent fragment
                setFragmentResult(
                    REQUEST_KEY,
                    bundleOf(BUNDLE_KEY_NEW_NAME to newName)
                )
                dismiss()
            } else {
                // Optional: Show error if empty
                binding.etModuleName.error = "Name cannot be empty"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_MODULE_NAME = "module_name"

        // Constants for Fragment Result API
        const val REQUEST_KEY = "request_rename_module"
        const val BUNDLE_KEY_NEW_NAME = "new_module_name"

        fun newInstance(moduleName: String): RenameModuleDialogFragment {
            val fragment = RenameModuleDialogFragment()
            val args = Bundle()
            args.putString(ARG_MODULE_NAME, moduleName)
            fragment.arguments = args
            return fragment
        }
    }
}