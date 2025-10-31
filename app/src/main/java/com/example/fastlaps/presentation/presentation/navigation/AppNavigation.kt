

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fastlaps.presentation.presentation.screen.SessionResultsScreen
import com.example.fastlaps.presentation.presentation.viewmodel.RaceViewModel

@Composable
fun AppNavigation(viewModel: RaceViewModel, currentLang: String, onLanguageChange: () -> Unit) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "mainScreen") {
        composable("mainScreen") {
            MainScreen(
                onCircuitsClick = { navController.navigate("sessionList") },
                onPilotsClick = { navController.navigate("pilotList") },
                onAboutClick = { navController.navigate("about") },
                onConstructorsClick = { navController.navigate("constructors") },
                onNewsClick = { navController.navigate("news") },
                currentLang = currentLang,
                onLanguageChange = onLanguageChange
            )
        }

        composable("about") {
            AboutScreen(
            )
        }

        composable("sessionList") {
            SessionListScreen(
                viewModel = viewModel,
                onSessionClick = { sessionKey ->
                    viewModel.loadSessionData(sessionKey)
                    navController.navigate("sessionResults/$sessionKey")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("pilotList") {
            PilotsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("constructors") {
            ConstructorsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("news") {
            NewsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                currentLang = currentLang
            )
        }

        composable(
            route = "sessionResults/{sessionKey}",
            arguments = listOf(navArgument("sessionKey") { type = NavType.IntType })
        ) { backStackEntry ->
            val finalPositions by viewModel.finalPositions.collectAsState()
            SessionResultsScreen(
                finalPositions = finalPositions,
                onBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
    }
}