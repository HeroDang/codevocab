package com.group20.codevocab.ui.dictionary

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.group20.codevocab.R
import com.group20.codevocab.data.local.entity.ModuleEntity
import com.group20.codevocab.databinding.FragmentAddWordBinding
import com.group20.codevocab.viewmodel.ModuleViewModel
import com.group20.codevocab.viewmodel.ModuleViewModelFactory

class AddWordFragment : Fragment() {

    private var _binding: FragmentAddWordBinding? = null
    private val binding get() = _binding!!
    private var photoUri: Uri? = null

    private lateinit var viewModel: ModuleViewModel
    private var localModules: List<ModuleEntity> = emptyList()
    private var selectedModuleId: String? = null

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            launchCamera()
        } else {
            Toast.makeText(requireContext(), "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    private val takePhotoLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            photoUri?.let { uri ->
                val bundle = Bundle().apply {
                    putString("image_uri", uri.toString())
                }
                findNavController().navigate(R.id.action_addWordFragment_to_importImageDicFragment, bundle)
            }
        } else {
            Toast.makeText(requireContext(), "Failed to take photo", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddWordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        val factory = ModuleViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[ModuleViewModel::class.java]

        setupToolbar()
        setupModuleSpinner()
        setupButtons()

        // Load local modules
        viewModel.loadModules()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupModuleSpinner() {
        viewModel.modules.observe(viewLifecycleOwner) { modules ->
            localModules = modules
            val moduleNames = modules.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, moduleNames)
            binding.autoCompleteModule.setAdapter(adapter)

            binding.autoCompleteModule.setOnItemClickListener { _, _, position, _ ->
                if (position < localModules.size) {
                    selectedModuleId = localModules[position].id
                }
            }
        }
    }

    private fun setupButtons() {
        binding.btnImport.setOnClickListener {
            findNavController().navigate(R.id.action_addWordFragment_to_importImageDicFragment)
        }
        
        binding.btnScan.setOnClickListener {
             checkCameraPermissionAndLaunch()
        }
    }

    private fun checkCameraPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            launchCamera()
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun launchCamera() {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "codevocab_${System.currentTimeMillis()}.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CodeVocab")
            }
        }

        photoUri = requireContext().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )

        photoUri?.let { uri ->
            takePhotoLauncher.launch(uri)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}