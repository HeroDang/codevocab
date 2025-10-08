package com.group20.codevocab.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.group20.codevocab.R
import com.group20.codevocab.databinding.FragmentProfileBinding
import com.group20.codevocab.utils.LocaleHelper

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnEnglish = view.findViewById<Button>(R.id.btnEnglish)
        val btnVietnamese = view.findViewById<Button>(R.id.btnVietnamese)

        btnEnglish.setOnClickListener {
            LocaleHelper.saveLanguage(requireContext(), "en")
            requireActivity().recreate()
        }

        btnVietnamese.setOnClickListener {
            LocaleHelper.saveLanguage(requireContext(), "vi")
            requireActivity().recreate()
        }
    }
}
