package com.myworkoutroutine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.myworkoutroutine.core.ui.theme.MyWorkoutRoutineTheme
import com.myworkoutroutine.feature.settings.SettingsScreen
import com.myworkoutroutine.feature.trainings.AddEditTrainingScreen
import com.myworkoutroutine.feature.trainings.TrainingsScreen
import com.myworkoutroutine.feature.workouts.AddEditWorkoutScreen
import com.myworkoutroutine.feature.workouts.WorkoutsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            MyWorkoutRoutineTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        BottomNavItem(
            route = "workouts",
            icon = Icons.Default.FitnessCenter,
            labelResId = R.string.nav_workouts
        ),
        BottomNavItem(
            route = "trainings",
            icon = Icons.Default.FolderSpecial,
            labelResId = R.string.nav_trainings
        ),
        BottomNavItem(
            route = "settings",
            icon = Icons.Default.Settings,
            labelResId = R.string.nav_settings
        )
    )

    Scaffold(
        bottomBar = {
            if (currentDestination?.route in bottomNavItems.map { it.route }) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = stringResource(item.labelResId)
                                )
                            },
                            label = { Text(stringResource(item.labelResId)) },
                            selected = currentDestination?.hierarchy?.any {
                                it.route == item.route
                            } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "workouts",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("workouts") {
                WorkoutsScreen(
                    onNavigateToAddEdit = { cardId ->
                        navController.navigate("add_edit_workout/${cardId ?: 0}")
                    }
                )
            }

            composable(
                route = "add_edit_workout/{cardId}",
                arguments = listOf(
                    navArgument("cardId") {
                        type = NavType.LongType
                        defaultValue = 0L
                    }
                )
            ) {
                AddEditWorkoutScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("trainings") {
                TrainingsScreen(
                    onNavigateToAddEdit = { trainingId ->
                        navController.navigate("add_edit_training/${trainingId ?: 0}")
                    }
                )
            }

            composable(
                route = "add_edit_training/{trainingId}",
                arguments = listOf(
                    navArgument("trainingId") {
                        type = NavType.LongType
                        defaultValue = 0L
                    }
                )
            ) {
                AddEditTrainingScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("settings") {
                SettingsScreen()
            }
        }
    }
}

data class BottomNavItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val labelResId: Int
)
