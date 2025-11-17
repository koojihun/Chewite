package com.chewite.app.ui.signup.agree

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.chewite.app.R
import com.chewite.app.data.signup.CONSENT_MARKETING_KEY
import com.chewite.app.data.signup.CONSENT_PERSONAL_INFO_KEY
import com.chewite.app.data.signup.CONSENT_SERVICE_KEY
import com.chewite.app.data.signup.ConsentState
import com.chewite.app.databinding.FragmentSignupAgreeBinding
import com.chewite.app.ui.signup.SignUpViewModel
import kotlinx.coroutines.launch

class AgreeFragment : Fragment() {

    private var _binding: FragmentSignupAgreeBinding? = null
    private val binding get() = _binding!!

    private val signUpViewModel: SignUpViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupAgreeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLeftButton()
        setAgreeAllButton()
        setServiceButton()
        setPersonalInfoButton()
        setMarketingButton()
        setNextButton()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                signUpViewModel.consent.collect { state -> render(state) }
            }
        }
    }

    private fun setLeftButton() {
        binding.leftBtn.setOnClickListener { requireActivity().finish() }
    }

    private fun setAgreeAllButton() {
        binding.signupAllAgreeButton.setOnClickListener { button ->
            if (button.isSelected) signUpViewModel.declineAll()
            else signUpViewModel.agreeAll()
        }
    }

    private fun setServiceButton() {
        binding.signupServiceAgreeButton.setOnClickListener {
            toggle(CONSENT_SERVICE_KEY)
        }
        binding.signupServiceAgreeRightArrow.setOnClickListener {
            findNavController().navigate(R.id.navigation_signup_agree_service)
        }
    }

    private fun setPersonalInfoButton() {
        binding.signupPersonalInfoAgreeButton.setOnClickListener {
            toggle(CONSENT_PERSONAL_INFO_KEY)
        }
        binding.signupPersonalInfoAgreeRightArrow.setOnClickListener {
            findNavController().navigate(R.id.navigation_signup_agree_personal_info)
        }
    }

    private fun setMarketingButton() {
        binding.signupMarketingAgreeButton.setOnClickListener {
            toggle(CONSENT_MARKETING_KEY)
        }
        binding.signupMarketingAgreeRightArrow.setOnClickListener {
            findNavController().navigate(R.id.navigation_signup_agree_marketing)
        }
    }

    private fun toggle(key: String) {
        val current = signUpViewModel.consent.value.items
            .firstOrNull { it.key == key }?.agreed ?: false
        signUpViewModel.setAgreed(key, !current)
    }

    private fun render(state: ConsentState) {
        fun agreedOf(key: String) =
            state.items.firstOrNull { it.key == key }?.agreed == true

        binding.signupServiceAgreeButton.isSelected = agreedOf(CONSENT_SERVICE_KEY)
        binding.signupPersonalInfoAgreeButton.isSelected = agreedOf(CONSENT_PERSONAL_INFO_KEY)
        binding.signupMarketingAgreeButton.isSelected = agreedOf(CONSENT_MARKETING_KEY)

        val all = state.items.all { it.agreed }
        binding.signupAllAgreeButton.isSelected = all
    }

    private fun setNextButton() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                signUpViewModel.consent.collect { state ->
                    binding.nextButton.isEnabled = state.isAgreedAllRequired
                }
            }
        }
        binding.nextButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_signup_finish)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}