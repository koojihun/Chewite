package com.chewite.app.ui.my.edit_profile

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import coil.transform.CircleCropTransformation
import com.chewite.app.R
import com.chewite.app.databinding.ActivityEditProfileBinding
import com.chewite.app.ui.BaseActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EditProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val viewModel: EditProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setBackButton()
        setProfileImage()
        setProfileButton()
        setEditText()
        setFinishButton()
    }

    override fun setSystemPaddings() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setBackButton() {
        binding.leftBtn.setOnClickListener { finish() }
    }

    private fun setProfileImage() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.profileImage.collectLatest { uri ->
                    if (uri != Uri.EMPTY) {
                        binding.profileImageImageview.load(uri) {
                            transformations(CircleCropTransformation())
                        }
                    } else {
                        binding.profileImageImageview.load(R.drawable.my_profile_default_image)
                    }
                }
            }
        }
    }

    private fun setProfileButton() {
        binding.profileSelectImageContainer.setOnClickListener {
            ProfileBottomSheetFragment().show(supportFragmentManager, "PickPhotoSheet")
        }
    }

    private fun setEditText() {
        binding.profileNicknameEdittextLayout.setOnClickListener {
            binding.profileNicknameEdittext.requestFocus()
            binding.profileNicknameEdittext.showKeyboard()
        }
        binding.profileNicknameEdittext.doAfterTextChanged {
            viewModel.updateNickname(it?.toString().orEmpty())
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.nickname.collect {
                    binding.profileNicknameLengthTextview.text = "${it.length}/20"
                }
            }
        }
        binding.profileNicknameEdittext.post {
            binding.profileNicknameEdittext.setSelection(
                binding.profileNicknameEdittext.text?.length ?: 0
            )
        }
        binding.profileNicknameRemoveButton.setOnClickListener {
            binding.profileNicknameEdittext.setText("")
        }
    }

    private fun setFinishButton() {
        binding.finishButton.setOnClickListener { finish() }
    }

    private fun View.showKeyboard() {
        post {
            val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}