package com.group20.codevocab.ui.dictionary

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
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
            // TODO: Handle rename logic
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_MODULE_NAME = "module_name"

        fun newInstance(moduleName: String): RenameModuleDialogFragment {
            val fragment = RenameModuleDialogFragment()
            val args = Bundle()
            args.putString(ARG_MODULE_NAME, moduleName)
            fragment.arguments = args
            return fragment
        }
    }
}