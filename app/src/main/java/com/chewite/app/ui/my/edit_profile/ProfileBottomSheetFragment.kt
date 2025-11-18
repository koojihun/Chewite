package com.chewite.app.ui.my.edit_profile

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.chewite.app.databinding.FragmentMyProfileBottomSheetBinding
import com.chewite.app.databinding.FragmentMyProfileBottomSheetWithDeleteBinding
import com.google.android.material.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

sealed class BottomSheetBinding {
    abstract val root: View

    class Normal(val binding: FragmentMyProfileBottomSheetBinding) : BottomSheetBinding() {
        override val root: View get() = binding.root
    }

    class WithDelete(val binding: FragmentMyProfileBottomSheetWithDeleteBinding) :
        BottomSheetBinding() {
        override val root: View get() = binding.root
    }
}

class ProfileBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditProfileViewModel by activityViewModels()

//    private val pickImage = registerForActivityResult(
//        ActivityResultContracts.PickVisualMedia()
//    ) { uri ->
//        uri?.let { viewModel.updateProfileImage(it) }
//        dismiss()
//    }

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data ?: return@registerForActivityResult
            viewModel.updateProfileImage(uri)
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val sheet = dialog.findViewById<View>(R.id.design_bottom_sheet)
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
        val hasImage = viewModel.profileImage.value != Uri.EMPTY
        _binding = if (hasImage) {
            val b =
                FragmentMyProfileBottomSheetWithDeleteBinding.inflate(inflater, container, false)
            BottomSheetBinding.WithDelete(b)
        } else {
            val b = FragmentMyProfileBottomSheetBinding.inflate(inflater, container, false)
            BottomSheetBinding.Normal(b)
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

    private fun setSelectAlbumButton(bottomSheetBinding: BottomSheetBinding) {
        when (bottomSheetBinding) {
            is BottomSheetBinding.WithDelete -> bottomSheetBinding.binding.profileBottomSheetSelectAlbumButton.setOnClickListener { launchPickPhoto() }
            is BottomSheetBinding.Normal -> bottomSheetBinding.binding.profileBottomSheetSelectAlbumButton.setOnClickListener { launchPickPhoto() }
        }
    }

    private fun setDeleteButton(bottomSheetBinding: BottomSheetBinding) {
        if (bottomSheetBinding is BottomSheetBinding.WithDelete) bottomSheetBinding.binding.profileBottomSheetDeleteButton.setOnClickListener { viewModel.clearProfileImage() }
    }

    private fun setCloseButton(bottomSheetBinding: BottomSheetBinding) {
        when (bottomSheetBinding) {
            is BottomSheetBinding.WithDelete -> bottomSheetBinding.binding.profileBottomSheetCloseButton.setOnClickListener { dismiss() }
            is BottomSheetBinding.Normal -> bottomSheetBinding.binding.profileBottomSheetCloseButton.setOnClickListener { dismiss() }
        }
    }

    private fun launchPickPhoto() {
//        pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        val intent = Intent(Intent.ACTION_PICK).apply {
            setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        }
        pickImage.launch(intent)
    }
}
