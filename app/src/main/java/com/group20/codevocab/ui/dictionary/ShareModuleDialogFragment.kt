package com.group20.codevocab.ui.dictionary

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.group20.codevocab.databinding.DialogShareModuleBinding
import com.group20.codevocab.viewmodel.DictionaryViewModel
import com.group20.codevocab.viewmodel.DictionaryViewModelFactory
import com.group20.codevocab.viewmodel.ShareResult

class ShareModuleDialogFragment : DialogFragment() {

    private var _binding: DialogShareModuleBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DictionaryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogShareModuleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = DictionaryViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[DictionaryViewModel::class.java]

        // Make the dialog background transparent
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Get arguments
        val moduleName = arguments?.getString(ARG_MODULE_NAME)
        val moduleId = arguments?.getString(ARG_MODULE_ID)
        val isLocal = arguments?.getBoolean(ARG_IS_LOCAL) ?: false
        
        val finalModuleId = moduleId ?: "" 
        
        binding.tvModuleName.text = moduleName

        binding.ivClose.setOnClickListener {
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSend.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val isPublish = binding.cbShareToMarket.isChecked
            
            if (email.isEmpty() && !isPublish) {
                Toast.makeText(context, "Please enter email or check 'Share to Market'", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Pass isLocal to viewModel.shareModule
            viewModel.shareModule(finalModuleId, if(email.isEmpty()) null else email, isPublish, isLocal)
        }
        
        viewModel.shareResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ShareResult.Success -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetShareResult()
                    dismiss()
                }
                is ShareResult.Error -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetShareResult()
                }
                is ShareResult.UserNotFound -> {
                    showUserNotFoundDialog()
                    viewModel.resetShareResult()
                }
                null -> {}
            }
        }
    }
    
    private fun showUserNotFoundDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("User Not Found")
            .setMessage("The email address you entered does not exist in our system.")
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_MODULE_NAME = "module_name"
        private const val ARG_MODULE_ID = "module_id"
        private const val ARG_IS_LOCAL = "is_local"

        fun newInstance(moduleName: String, moduleId: String, isLocal: Boolean): ShareModuleDialogFragment {
            val fragment = ShareModuleDialogFragment()
            val args = Bundle()
            args.putString(ARG_MODULE_NAME, moduleName)
            args.putString(ARG_MODULE_ID, moduleId)
            args.putBoolean(ARG_IS_LOCAL, isLocal)
            fragment.arguments = args
            return fragment
        }
    }
}
