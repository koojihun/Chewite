package com.chewite.app.ui.my

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chewite.app.data.terms.TermsType
import com.chewite.app.databinding.FragmentMyBinding
import com.chewite.app.ui.my.edit_profile.EditProfileActivity
import com.chewite.app.ui.terms.TermsActivity

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