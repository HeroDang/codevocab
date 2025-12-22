package com.group20.codevocab.ui.dictionary

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.group20.codevocab.R
import com.group20.codevocab.databinding.FragmentImportImageDicBinding
import com.group20.codevocab.viewmodel.ScannerState
import com.group20.codevocab.viewmodel.ScannerViewModel
import com.group20.codevocab.viewmodel.ScannerViewModelFactory
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class ImportImageDicFragment : Fragment() {

    private var _binding: FragmentImportImageDicBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var scannerViewModel: ScannerViewModel
    private var currentImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data
            if (imageUri != null) {
                currentImageUri = imageUri
                binding.ivPreview.setImageURI(imageUri)
                binding.ivPreview.visibility = View.VISIBLE
                binding.llUploadContainer.visibility = View.GONE
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImportImageDicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ScannerViewModelFactory()
        scannerViewModel = ViewModelProvider(this, factory)[ScannerViewModel::class.java]

        setupToolbar()
        setupButtons()
        observeViewModel()

        // Kiểm tra xem có URI được gửi từ màn hình Scan sang không
        arguments?.getString("image_uri")?.let { uriString ->
            val imageUri = Uri.parse(uriString)
            currentImageUri = imageUri

            // Hiển thị ảnh
            binding.ivPreview.setImageURI(imageUri)

            // Ẩn khu vực upload, hiện ảnh preview
            binding.ivPreview.visibility = View.VISIBLE
            binding.llUploadContainer.visibility = View.GONE
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupButtons() {
        binding.btnBrowse.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }

        binding.btnReview.setOnClickListener {
            val uri = currentImageUri
            if (uri != null) {
                val file = getFileFromUri(requireContext(), uri)
                if (file != null) {
                    scannerViewModel.scanImage(file)
                } else {
                    Toast.makeText(requireContext(), "Cannot process file", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please select an image first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                scannerViewModel.state.collect { state ->
                    when (state) {
                        is ScannerState.Idle -> {
                            binding.btnReview.isEnabled = true
                            binding.btnReview.text = "Review"
                        }
                        is ScannerState.Loading -> {
                            binding.btnReview.isEnabled = false
                            binding.btnReview.text = "Scanning..."
                        }
                        is ScannerState.Success -> {
                            // Check if fragment is still added to prevent crash on navigation
                            if (!isAdded) return@collect

                            val bundle = Bundle().apply {
                                putString("ocr_text", state.text)
                            }
                            // Reset state BEFORE navigating to prevent double navigation
                            scannerViewModel.onResultConsumed()
                            
                            findNavController().navigate(R.id.action_importImageDicFragment_to_reviewWordFragment, bundle)
                        }
                        is ScannerState.Error -> {
                            binding.btnReview.isEnabled = true
                            binding.btnReview.text = "Review"
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.cacheDir, "temp_ocr_image.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}