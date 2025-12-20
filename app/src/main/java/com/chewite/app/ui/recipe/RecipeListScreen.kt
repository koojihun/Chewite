package com.chewite.app.ui.recipe

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun RecipeListScreen(navController: NavController) {
    val recipeListViewModel: RecipeListViewModel = hiltViewModel()
    recipeListViewModel.test()
}