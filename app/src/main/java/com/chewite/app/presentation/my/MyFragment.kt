package com.chewite.app.presentation.my

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.chewite.app.data.terms.TermsType
import com.chewite.app.databinding.FragmentMyBinding
import com.chewite.app.presentation.my.edit_profile.EditProfileActivity
import com.chewite.app.presentation.terms.TermsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyFragment : Fragment() {

    private var _binding: FragmentMyBinding? = null
    private val binding get() = _binding!!
    private lateinit var myViewModel: MyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myViewModel = ViewModelProvider(this)[MyViewModel::class.java]
        setEditProfileButton()
        setTermsButtons()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                myViewModel.currentUser.collect { user ->
                    binding.nicknameTextview.text = user?.nickname ?: "등록되지 않은 사용자"
                }
            }
        }
    }

    private fun setEditProfileButton() {
        binding.profileEditButton.setOnClickListener {
            startActivity(Intent(requireActivity(), EditProfileActivity::class.java))
        }
    }

    private fun startTermsActivity(termsType: TermsType) {
        startActivity(
            Intent(requireActivity(), TermsActivity::class.java).putExtra(
                TermsType.EXTRA_KEY, termsType.name
            )
        )
    }

    private fun setTermsButtons() {
        binding.serviceButton.setOnClickListener { startTermsActivity(TermsType.SERVICE) }
        binding.personalInfoButtonButton.setOnClickListener { startTermsActivity(TermsType.PERSONAL_INFO) }
        binding.marketingButton.setOnClickListener { startTermsActivity(TermsType.MARKETING) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}