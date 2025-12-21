package com.group20.codevocab.ui.dictionary

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.group20.codevocab.R
import com.group20.codevocab.databinding.FragmentImportImageDicBinding

class ImportImageDicFragment : Fragment() {

    private var _binding: FragmentImportImageDicBinding? = null
    private val binding get() = _binding!!

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data
            binding.ivPreview.setImageURI(imageUri)
            binding.ivPreview.visibility = View.VISIBLE
            binding.llUploadContainer.visibility = View.GONE
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

        setupToolbar()
        setupButtons()

        // Kiểm tra xem có URI được gửi từ màn hình Scan sang không
        arguments?.getString("image_uri")?.let { uriString ->
            val imageUri = android.net.Uri.parse(uriString)

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
            findNavController().navigate(R.id.action_importImageDicFragment_to_reviewWordFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}