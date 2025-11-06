package com.chewite.app.ui.signup.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.chewite.app.R
import com.chewite.app.databinding.FragmentSignupProfileBinding
import com.chewite.app.ui.signup.NextButtonHost
import com.chewite.app.ui.signup.OnNextClickHandler
import com.chewite.app.ui.signup.SignUpViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlin.math.max

class ProfileFragment : Fragment(), OnNextClickHandler {

    private var _binding: FragmentSignupProfileBinding? = null
    private val binding get() = _binding!!

    private val signUpViewModel: SignUpViewModel by activityViewModels()

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            binding.signupProfileImageImageview.load(uri) {
                crossfade(true)
                transformations(CircleCropTransformation())
                ProfileImageUtils.saveToDownloadFolder(viewLifecycleOwner, requireContext(), uri)
            }
        }
    }

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
        setProfileButton()
        setEditText()
        setNextButton()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            v.updatePadding(bottom = max(ime, bars))   // 키보드/내비 둘 중 큰 값
            insets
        }
    }

    private fun setProfileButton() {
        binding.signupProfileSelectImageContainer.setOnClickListener {
            ProfileBottomSheetFragment(
                onSelectAlbumButtonClicked = {
                    pickImage.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            ).show(parentFragmentManager, "PickPhotoSheet")
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
    }

    private fun setNextButton() {
        val host = requireActivity() as NextButtonHost
        host.setNextOnClick { onNextClicked(findNavController()) }
        host.bindNextEnabled(
            viewLifecycleOwner,
            signUpViewModel.nickname.map { it.isNotEmpty() }.distinctUntilChanged()
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