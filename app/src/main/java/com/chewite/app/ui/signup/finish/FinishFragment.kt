package com.chewite.app.ui.signup.finish

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.chewite.app.databinding.FragmentSignupFinishBinding
import com.chewite.app.ui.signup.OnNextClickHandler

class FinishFragment : Fragment(), OnNextClickHandler {

    private var _binding: FragmentSignupFinishBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupFinishBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onNextClicked(navController: NavController) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}