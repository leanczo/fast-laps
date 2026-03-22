

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fastlaps.presentation.presentation.screen.AboutScreen
import com.example.fastlaps.presentation.presentation.screen.CalendarScreen
import com.example.fastlaps.presentation.presentation.screen.ConstructorDetailScreen
import com.example.fastlaps.presentation.presentation.screen.DriverDetailScreen
import com.example.fastlaps.presentation.presentation.screen.FastestLapsScreen
import com.example.fastlaps.presentation.presentation.screen.MainScreen
import com.example.fastlaps.presentation.presentation.screen.RaceReplayScreen
import com.example.fastlaps.presentation.presentation.screen.ReactionTimeScreen
import com.example.fastlaps.presentation.presentation.screen.HeadToHeadScreen
import com.example.fastlaps.presentation.presentation.screen.RaceDetailScreen
import com.example.fastlaps.presentation.presentation.screen.SessionResultsScreen
import com.example.fastlaps.presentation.presentation.viewmodel.RaceViewModel

@Composable
fun AppNavigation(viewModel: RaceViewModel, currentLang: String, onLanguageChange: () -> Unit) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "mainScreen") {
        composable("mainScreen") {
            MainScreen(
                viewModel = viewModel,
                onRacesClick = { navController.navigate("calendar") },
                onNextRaceClick = { round -> navController.navigate("raceDetail/$round") },
                onPilotsClick = { navController.navigate("pilotList") },
                onAboutClick = { navController.navigate("about") },
                onConstructorsClick = { navController.navigate("constructors") },
                onNewsClick = { navController.navigate("news") },
                onFastestLapsClick = { navController.navigate("fastestLaps") },
                onReactionGameClick = { navController.navigate("reactionTime") },
                currentLang = currentLang,
                onLanguageChange = onLanguageChange
            )
        }

        composable("about") {
            AboutScreen()
        }

        composable(
            route = "raceReplay/{round}",
            arguments = listOf(navArgument("round") { type = NavType.IntType })
        ) { backStackEntry ->
            val replayRound = backStackEntry.arguments?.getInt("round") ?: return@composable
            RaceReplayScreen(
                viewModel = viewModel,
                round = replayRound,
                onBack = { navController.popBackStack() }
            )
        }

        composable("reactionTime") {
            ReactionTimeScreen(onBack = { navController.popBackStack() })
        }

        composable("fastestLaps") {
            FastestLapsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("calendar") {
            CalendarScreen(
                viewModel = viewModel,
                onRaceClick = { round, raceName ->
                    viewModel.loadRaceResults(round, raceName)
                    navController.navigate("raceResults/$round")
                },
                onFutureRaceClick = { round ->
                    navController.navigate("raceDetail/$round")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("sessionList") {
            SessionListScreen(
                viewModel = viewModel,
                onRaceClick = { round, raceName ->
                    viewModel.loadRaceResults(round, raceName)
                    navController.navigate("raceResults/$round")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("pilotList") {
            PilotsScreen(
                viewModel = viewModel,
                onDriverClick = { driverId ->
                    navController.navigate("driverDetail/$driverId")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "driverDetail/{driverId}",
            arguments = listOf(navArgument("driverId") { type = NavType.StringType })
        ) { backStackEntry ->
            val driverId = backStackEntry.arguments?.getString("driverId") ?: return@composable
            DriverDetailScreen(
                viewModel = viewModel,
                driverId = driverId,
                onBack = { navController.popBackStack() }
            )
        }

        composable("constructors") {
            ConstructorsScreen(
                viewModel = viewModel,
                onConstructorClick = { constructorId ->
                    navController.navigate("constructorDetail/$constructorId")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "constructorDetail/{constructorId}",
            arguments = listOf(navArgument("constructorId") { type = NavType.StringType })
        ) { backStackEntry ->
            val constructorId = backStackEntry.arguments?.getString("constructorId") ?: return@composable
            ConstructorDetailScreen(
                viewModel = viewModel,
                constructorId = constructorId,
                onHeadToHead = { navController.navigate("headToHead/$constructorId") },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "headToHead/{constructorId}",
            arguments = listOf(navArgument("constructorId") { type = NavType.StringType })
        ) { backStackEntry ->
            val cId = backStackEntry.arguments?.getString("constructorId") ?: return@composable
            HeadToHeadScreen(
                viewModel = viewModel,
                constructorId = cId,
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
            route = "raceDetail/{round}",
            arguments = listOf(navArgument("round") { type = NavType.IntType })
        ) { backStackEntry ->
            val round = backStackEntry.arguments?.getInt("round") ?: return@composable
            RaceDetailScreen(
                viewModel = viewModel,
                round = round,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "raceResults/{round}",
            arguments = listOf(navArgument("round") { type = NavType.IntType })
        ) {
            val raceResults by viewModel.raceResults.collectAsState()
            val qualifyingResults by viewModel.qualifyingResults.collectAsState()
            val sprintResults by viewModel.sprintResults.collectAsState()
            val raceName by viewModel.currentRaceName.collectAsState()
            val round = it.arguments?.getInt("round") ?: 0
            SessionResultsScreen(
                raceResults = raceResults,
                qualifyingResults = qualifyingResults,
                sprintResults = sprintResults,
                raceName = raceName,
                onBack = { navController.popBackStack() },
                onReplayClick = { navController.navigate("raceReplay/$round") },
                viewModel = viewModel
            )
        }
    }
}
