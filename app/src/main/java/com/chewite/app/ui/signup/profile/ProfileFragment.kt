package com.chewite.app.ui.signup.profile

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.chewite.app.R
import com.chewite.app.data.signup.NicknameGenerator
import com.chewite.app.databinding.FragmentSignupProfileBinding
import com.chewite.app.ui.signup.NextButtonHost
import com.chewite.app.ui.signup.OnNextClickHandler
import com.chewite.app.ui.signup.SignUpViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(), OnNextClickHandler {

    private var _binding: FragmentSignupProfileBinding? = null
    private val binding get() = _binding!!

    private val signUpViewModel: SignUpViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setProfileImage()
        setProfileButton()
        setEditText()
        setNextButton()

        focusEditText()
    }

    private fun focusEditText() {
        binding.signupProfileNicknameEdittext.post {
            binding.signupProfileNicknameEdittext.requestFocus()
            binding.signupProfileNicknameEdittext.showKeyboard()
        }
    }

    private fun setProfileImage() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                signUpViewModel.profileImage
                    .collectLatest { uri ->
                        if (uri != Uri.EMPTY) {
                            binding.signupProfileImageImageview.load(uri) {
                                transformations(CircleCropTransformation())
                            }
                        } else {
                            binding.signupProfileImageImageview.load(R.drawable.signup_profile_image)
                        }
                    }
            }
        }
    }

    private fun setProfileButton() {
        binding.signupProfileSelectImageContainer.setOnClickListener {
            ProfileBottomSheetFragment().show(parentFragmentManager, "PickPhotoSheet")
        }
    }

    private fun setEditText() {
        binding.signupProfileNicknameEdittextLayout.setOnClickListener {
            binding.signupProfileNicknameEdittext.requestFocus()
            binding.signupProfileNicknameEdittext.showKeyboard()
        }
        binding.signupProfileNicknameEdittext.doAfterTextChanged {
            signUpViewModel.updateNickname(
                it?.toString().orEmpty()
            )
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                signUpViewModel.nickname.collect {
                    binding.signupProfileNicknameLengthTextview.text = "${it.length}/20"
                }
            }
        }
        binding.signupProfileNicknameEdittext.setText(NicknameGenerator.generate())
        binding.signupProfileNicknameEdittext.post {
            binding.signupProfileNicknameEdittext.setSelection(binding.signupProfileNicknameEdittext.text?.length ?: 0)
        }
        binding.signupProfileNicknameRemoveButton.setOnClickListener { binding.signupProfileNicknameEdittext.setText("") }
    }

    private fun setNextButton() {
        val host = requireActivity() as NextButtonHost
        host.setNextOnClick { onNextClicked(findNavController()) }
        host.bindNextEnabled(
            viewLifecycleOwner,
            signUpViewModel.nickname.map { it.isNotEmpty() && it.length >= 2 && it.length <= 20 }.distinctUntilChanged()
        )
    }

    override fun onNextClicked(navController: NavController) {
        navController.navigate(R.id.navigation_signup_finish)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun View.showKeyboard() {
        post {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}