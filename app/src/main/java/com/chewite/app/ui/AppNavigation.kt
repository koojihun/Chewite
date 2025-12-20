package com.chewite.app.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.chewite.app.data.model.LoginStatus
import com.chewite.app.ui.login.LoginScreen
import com.chewite.app.ui.mypage.MyPageScreen
import com.chewite.app.ui.recipe.RecipeDetailScreen
import com.chewite.app.ui.recipe.RecipeListScreen
import com.chewite.app.ui.signup.AgreementDetailScreen
import com.chewite.app.ui.signup.SignUpFinishScreen
import com.chewite.app.ui.signup.SignUpScreen

@Composable
fun AppNavigation(loginStatus: LoginStatus) {

    val navController = rememberNavController()

    val startDestination = when (loginStatus) {
        LoginStatus.NEW -> Route.SIGNUP
        LoginStatus.ACTIVE -> Route.RECIPE_LIST
        LoginStatus.NO_AUTH -> Route.LOGIN
        else -> return
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }) {
        authGraph(navController)
        mainGraph(navController)
    }
}

fun NavGraphBuilder.authGraph(navController: NavController) {
    composable(Route.LOGIN) { LoginScreen(navController) }
    composable(Route.SIGNUP) { SignUpScreen(navController) }
    composable(Route.SIGNUP_FINISH) { SignUpFinishScreen(navController) }
    composable(
        Route.SIGNUP_AGREEMENT,
        arguments = listOf(navArgument("type") { type = NavType.StringType })
    ) { backStackEntry ->
        val type = backStackEntry.arguments?.getString("type")
        if (type != null) AgreementDetailScreen(navController, type)
    }
}

fun NavGraphBuilder.mainGraph(navController: NavController) {
    composable(Route.RECIPE_LIST) { RecipeListScreen(navController) }
    composable(Route.MY_PAGE) { MyPageScreen(navController) }
    composable(
        Route.RECIPE_DETAIL,
        arguments = listOf(navArgument("id") { type = NavType.StringType })
    ) { backStackEntry ->
        RecipeDetailScreen(
            navController = navController,
            id = backStackEntry.arguments?.getString("id")
        )
    }
}

object Route {
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val SIGNUP_FINISH = "signup_finish"
    const val RECIPE_LIST = "recipe_list"
    const val MY_PAGE = "my_page"

    const val SIGNUP_AGREEMENT = "signup_agreement_detail/{type}"
    const val RECIPE_DETAIL = "recipe_detail/{id}"

    fun makeRecipeDetailRoute(id: String) = "recipe_detail/$id"
    fun makeAgreementRoute(type: String) = "signup_agreement_detail/$type"
}