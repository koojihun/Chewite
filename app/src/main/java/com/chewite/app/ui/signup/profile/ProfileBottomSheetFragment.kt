package com.chewite.app.ui.signup.profile

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.chewite.app.databinding.FragmentSignupBottomSheetBinding
import com.chewite.app.databinding.FragmentSignupBottomSheetWithDeleteBinding
import com.chewite.app.ui.signup.SignUpViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

sealed class SignupBottomSheetBinding {
    abstract val root: View

    class Normal(val binding: FragmentSignupBottomSheetBinding) : SignupBottomSheetBinding() {
        override val root: View get() = binding.root
    }

    class WithDelete(val binding: FragmentSignupBottomSheetWithDeleteBinding) :
        SignupBottomSheetBinding() {
        override val root: View get() = binding.root
    }
}

class ProfileBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: SignupBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val signUpViewModel: SignUpViewModel by activityViewModels()

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { signUpViewModel.updateProfileImage(it) }
        dismiss()
    }

//    private val pickImage = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            val uri = result.data?.data ?: return@registerForActivityResult
//            // signUpViewModel.updateProfileImage(uri)
//            dismiss()
//        }
//    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val sheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            val behavior = BottomSheetBehavior.from(sheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val hasImage = signUpViewModel.profileImage.value != Uri.EMPTY
        _binding = if (hasImage) {
            val b = FragmentSignupBottomSheetWithDeleteBinding.inflate(inflater, container, false)
            SignupBottomSheetBinding.WithDelete(b)
        } else {
            val b = FragmentSignupBottomSheetBinding.inflate(inflater, container, false)
            SignupBottomSheetBinding.Normal(b)
        }
        setSelectAlbumButton(binding)
        setDeleteButton(binding)
        setCloseButton(binding)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setSelectAlbumButton(signupBottomSheetBinding: SignupBottomSheetBinding) {
        when (signupBottomSheetBinding) {
            is SignupBottomSheetBinding.WithDelete -> signupBottomSheetBinding.binding.signupProfileBottomSheetSelectAlbumButton.setOnClickListener { launchPickPhoto() }
            is SignupBottomSheetBinding.Normal -> signupBottomSheetBinding.binding.signupProfileBottomSheetSelectAlbumButton.setOnClickListener { launchPickPhoto() }
        }
    }

    private fun setDeleteButton(signupBottomSheetBinding: SignupBottomSheetBinding) {
        if (signupBottomSheetBinding is SignupBottomSheetBinding.WithDelete)
            signupBottomSheetBinding.binding.signupProfileBottomSheetDeleteButton.setOnClickListener { signUpViewModel.clearProfileImage() }
    }

    private fun setCloseButton(signupBottomSheetBinding: SignupBottomSheetBinding) {
        when (signupBottomSheetBinding) {
            is SignupBottomSheetBinding.WithDelete -> signupBottomSheetBinding.binding.signupProfileBottomSheetCloseButton.setOnClickListener { dismiss() }
            is SignupBottomSheetBinding.Normal -> signupBottomSheetBinding.binding.signupProfileBottomSheetCloseButton.setOnClickListener { dismiss() }
        }
    }

    private fun launchPickPhoto() {
        pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
//        val intent = Intent(Intent.ACTION_PICK).apply {
//            setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
//        }
//        pickImage.launch(intent)
    }
}
