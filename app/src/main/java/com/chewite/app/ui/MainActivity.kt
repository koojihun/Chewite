package com.chewite.app.ui

import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import com.chewite.app.R
import com.chewite.app.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initBottomNavView()
        setBackPressed()
    }

    private fun setBackPressed() {
        onBackPressedDispatcher.addCallback(this) {
            val current = navController.currentDestination?.id

            if (current == R.id.navigation_home) {
                finish()
            }

            val popped = navController.popBackStack(R.id.navigation_home, false)
            if (!popped) {
                navController.navigate(
                    R.id.navigation_home, null, navOptions {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    })
            }
        }
    }

    private fun initBottomNavView() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

        listOf(
            binding.mainBottomNavHomeContainer to R.id.navigation_home,
            binding.mainBottomNavSearchContainer to R.id.navigation_search,
            binding.mainBottomNavBookmarkContainer to R.id.navigation_bookmark,
            binding.mainBottomNavMyContainer to R.id.navigation_my
        ).forEach { (button, navigation) ->
            button.setOnClickListener { navigateIfNeeded(navigation) }
        }

        bindDestinationChange()
    }

    private fun navigateIfNeeded(destId: Int) {
        val current = navController.currentDestination?.id
        if (current != destId) navController.navigate(destId)
    }

    private fun bindDestinationChange() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home -> setSelectedTab(binding.mainBottomNavHomeContainer)
                R.id.navigation_search -> setSelectedTab(binding.mainBottomNavSearchContainer)
                R.id.navigation_bookmark -> setSelectedTab(binding.mainBottomNavBookmarkContainer)
                R.id.navigation_my -> setSelectedTab(binding.mainBottomNavMyContainer)
            }
        }
    }

    private fun setSelectedTab(selected: ViewGroup) {
        listOf(
            binding.mainBottomNavHomeContainer,
            binding.mainBottomNavSearchContainer,
            binding.mainBottomNavBookmarkContainer,
            binding.mainBottomNavMyContainer
        ).forEach { container ->
            val isSelected = (container == selected)
            for (i in 0 until container.childCount) {
                when (val child = container.getChildAt(i)) {
                    is ImageView -> child.isSelected = isSelected
                    is TextView -> child.isSelected = isSelected
                }
            }
        }
    }

    override fun setSystemPaddings() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}