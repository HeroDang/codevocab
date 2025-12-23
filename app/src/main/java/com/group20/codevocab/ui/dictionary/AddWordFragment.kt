package com.group20.codevocab.ui.dictionary

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.group20.codevocab.R
import com.group20.codevocab.data.local.entity.ModuleEntity
import com.group20.codevocab.data.local.entity.WordEntity
import com.group20.codevocab.databinding.FragmentAddWordBinding
import com.group20.codevocab.ui.common.speaker.Speaker
import com.group20.codevocab.ui.common.speaker.SpeakerFactory
import com.group20.codevocab.viewmodel.AddWordViewModel
import com.group20.codevocab.viewmodel.AddWordViewModelFactory
import com.group20.codevocab.viewmodel.IpaState
import com.group20.codevocab.viewmodel.ModuleViewModel
import com.group20.codevocab.viewmodel.ModuleViewModelFactory
import com.group20.codevocab.viewmodel.WordViewModel
import com.group20.codevocab.viewmodel.WordViewModelFactory
import kotlinx.coroutines.launch
import java.util.UUID

class AddWordFragment : Fragment() {

    private var _binding: FragmentAddWordBinding? = null
    private val binding get() = _binding!!
    private var photoUri: Uri? = null

    private lateinit var moduleViewModel: ModuleViewModel
    private lateinit var addWordViewModel: AddWordViewModel
    private lateinit var wordViewModel: WordViewModel
    private lateinit var speaker: Speaker
    private var localModules: List<ModuleEntity> = emptyList()
    private var selectedModuleId: String? = null

    private val textChangeHandler = Handler(Looper.getMainLooper())

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        speaker = SpeakerFactory.create(requireContext())
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

        // Initialize ViewModels
        val moduleFactory = ModuleViewModelFactory(requireContext())
        moduleViewModel = ViewModelProvider(this, moduleFactory)[ModuleViewModel::class.java]
        val addWordFactory = AddWordViewModelFactory()
        addWordViewModel = ViewModelProvider(this, addWordFactory)[AddWordViewModel::class.java]
        val wordFactory = WordViewModelFactory(requireContext())
        wordViewModel = ViewModelProvider(this, wordFactory)[WordViewModel::class.java]

        setupToolbar()
        setupModuleSpinner()
        setupButtons()
        setupIpaFetching()
        observeIpaState()

        // Load local modules
        moduleViewModel.loadModules()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupModuleSpinner() {
        moduleViewModel.modules.observe(viewLifecycleOwner) { modules ->
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

    private fun setupIpaFetching() {
        binding.etWord.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textChangeHandler.removeCallbacksAndMessages(null)
            }

            override fun afterTextChanged(s: Editable?) {
                textChangeHandler.postDelayed({
                    val word = s.toString().trim()
                    if (word.isNotBlank()) {
                        addWordViewModel.getPhonetic(word)
                    }
                }, 500) // 500ms delay
            }
        })

        binding.tilIpa.setEndIconOnClickListener {
            val wordToSpeak = binding.etWord.text.toString()
            if (wordToSpeak.isNotBlank()) {
                speaker.speak(wordToSpeak)
            }
        }
    }

    private fun observeIpaState() {
        lifecycleScope.launch {
            addWordViewModel.ipaState.collect { state ->
                when (state) {
                    is IpaState.Loading -> {
                        binding.etIpa.setText("Loading...")
                    }
                    is IpaState.Success -> {
                        binding.etIpa.setText(state.phonetic)
                    }
                    is IpaState.Error -> {
                        binding.etIpa.setText("")
                    }
                    is IpaState.Idle -> {
                        binding.etIpa.setText("")
                    }
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

        binding.btnAddWord.setOnClickListener {
            saveWordToDatabase()
        }
    }

    private fun saveWordToDatabase() {
        val wordText = binding.etWord.text.toString().trim()
        val meaningText = binding.etMeaning.text.toString().trim()

        if (wordText.isEmpty()) {
            Toast.makeText(context, "Please enter a word", Toast.LENGTH_SHORT).show()
            return
        }
        if (meaningText.isEmpty()) {
            Toast.makeText(context, "Please enter a meaning", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedModuleId == null) {
            Toast.makeText(context, "Please select a module", Toast.LENGTH_SHORT).show()
            return
        }

        val wordToSave = WordEntity(
            id = UUID.randomUUID().toString(),
            moduleId = selectedModuleId!!,
            textEn = wordText,
            meaningVi = meaningText,
            ipa = binding.etIpa.text.toString(),
            partOfSpeech = null, // Not available in this UI
            exampleSentence = binding.etExample.text.toString(),
            audioUrl = null,
            createdAt = System.currentTimeMillis().toString()
        )

        wordViewModel.saveWords(listOf(wordToSave))
        Toast.makeText(context, "Word added successfully", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
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
        if (::speaker.isInitialized) {
            speaker.shutdown()
        }
        textChangeHandler.removeCallbacksAndMessages(null)
        _binding = null
    }
}