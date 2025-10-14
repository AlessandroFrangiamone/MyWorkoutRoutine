package com.myworkoutroutine.feature.workouts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.myworkoutroutine.core.domain.model.WorkoutCard
import com.myworkoutroutine.core.ui.components.WorkoutCardItem
import com.myworkoutroutine.core.ui.theme.MyWorkoutRoutineTheme
import kotlinx.coroutines.flow.collect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutsScreen(
    onNavigateToAddEdit: (Long?) -> Unit,
    viewModel: WorkoutsViewModel = hiltViewModel()
) {
    val workoutCards by viewModel.workoutCards.collectAsStateWithLifecycle()
    var showCardInUseDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.cardInUseEvent.collect {
            showCardInUseDialog = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.workouts_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToAddEdit(null) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_workout)
                )
            }
        }
    ) { padding ->
        if (workoutCards.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.no_workouts_message),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(workoutCards, key = { it.id }) { card ->
                    WorkoutCardItem(
                        card = card,
                        onEdit = { onNavigateToAddEdit(card.id) },
                        onDelete = { viewModel.deleteWorkoutCard(card) }
                    )
                }
            }
        }
    }

    // Card in use dialog
    if (showCardInUseDialog) {
        AlertDialog(
            onDismissRequest = { showCardInUseDialog = false },
            title = { Text(stringResource(R.string.error_card_in_use)) },
            text = { Text(stringResource(R.string.error_card_in_use_description)) },
            confirmButton = {
                TextButton(onClick = { showCardInUseDialog = false }) {
                    Text(stringResource(com.myworkoutroutine.core.ui.R.string.cancel))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkoutsScreenContent(
    workoutCards: List<WorkoutCard>,
    onNavigateToAddEdit: (Long?) -> Unit,
    onDelete: (WorkoutCard) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.workouts_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToAddEdit(null) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_workout)
                )
            }
        }
    ) { padding ->
        if (workoutCards.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.no_workouts_message),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(workoutCards, key = { it.id }) { card ->
                    WorkoutCardItem(
                        card = card,
                        onEdit = { onNavigateToAddEdit(card.id) },
                        onDelete = { onDelete(card) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun WorkoutsScreenEmptyPreview() {
    MyWorkoutRoutineTheme {
        WorkoutsScreenContent(
            workoutCards = emptyList(),
            onNavigateToAddEdit = {},
            onDelete = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun WorkoutsScreenWithCardsPreview() {
    MyWorkoutRoutineTheme {
        WorkoutsScreenContent(
            workoutCards = listOf(
                WorkoutCard(
                    id = 1,
                    name = "Push-ups",
                    description = "Standard push-ups for chest and triceps",
                    timers = listOf(30, 60, 90)
                ),
                WorkoutCard(
                    id = 2,
                    name = "Squats",
                    description = "Bodyweight squats for legs and glutes",
                    timers = listOf(60, 90, 120)
                ),
                WorkoutCard(
                    id = 3,
                    name = "Plank",
                    description = "Core stability exercise",
                    timers = listOf(30, 60)
                )
            ),
            onNavigateToAddEdit = {},
            onDelete = {}
        )
    }
}
