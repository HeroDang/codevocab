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
import com.group20.codevocab.databinding.DialogAcceptModuleBinding

class AcceptModuleDialogFragment : DialogFragment() {

    private var _binding: DialogAcceptModuleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAcceptModuleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Make the dialog background transparent
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Get module name and ID from arguments
        val moduleName = arguments?.getString(ARG_MODULE_NAME)
        val moduleId = arguments?.getString(ARG_MODULE_ID)
        
        binding.tvModuleName.text = moduleName

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnViewModule.setOnClickListener {
            // Send result back to Fragment to handle navigation
            setFragmentResult(
                REQUEST_KEY,
                bundleOf(
                    BUNDLE_KEY_VIEW_MODULE to true,
                    BUNDLE_KEY_MODULE_ID to moduleId,
                    BUNDLE_KEY_MODULE_NAME to moduleName
                )
            )
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AcceptModuleDialog"
        const val REQUEST_KEY = "request_accept_module"
        const val BUNDLE_KEY_VIEW_MODULE = "view_module"
        const val BUNDLE_KEY_MODULE_ID = "module_id"
        const val BUNDLE_KEY_MODULE_NAME = "module_name"
        
        private const val ARG_MODULE_NAME = "module_name"
        private const val ARG_MODULE_ID = "module_id"

        fun newInstance(moduleName: String, moduleId: String? = null): AcceptModuleDialogFragment {
            val fragment = AcceptModuleDialogFragment()
            val args = Bundle()
            args.putString(ARG_MODULE_NAME, moduleName)
            if (moduleId != null) {
                args.putString(ARG_MODULE_ID, moduleId)
            }
            fragment.arguments = args
            return fragment
        }
    }
}