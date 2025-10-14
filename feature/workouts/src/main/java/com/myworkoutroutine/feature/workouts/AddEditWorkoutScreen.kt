package com.myworkoutroutine.feature.workouts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.myworkoutroutine.core.ui.theme.MyWorkoutRoutineTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditWorkoutScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddEditWorkoutViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is AddEditWorkoutViewModel.UiEvent.Success -> onNavigateBack()
                is AddEditWorkoutViewModel.UiEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message.asString(context))
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_edit_workout_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(com.myworkoutroutine.core.ui.R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = viewModel.name,
                onValueChange = viewModel::onNameChange,
                label = { Text(stringResource(R.string.workout_name_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = viewModel.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text(stringResource(R.string.workout_description_label)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            Text(
                text = stringResource(R.string.workout_timers_label),
                style = MaterialTheme.typography.labelLarge
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(30, 60, 90, 120).forEach { seconds ->
                    FilterChip(
                        selected = viewModel.selectedTimers.contains(seconds),
                        onClick = { viewModel.onTimerToggle(seconds) },
                        label = {
                            Text(
                                text = when (seconds) {
                                    30 -> stringResource(R.string.timer_30)
                                    60 -> stringResource(R.string.timer_60)
                                    90 -> stringResource(R.string.timer_90)
                                    120 -> stringResource(R.string.timer_120)
                                    else -> "${seconds}s"
                                }
                            )
                        }
                    )
                }
            }

            Button(
                onClick = viewModel::saveWorkout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save_workout))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditWorkoutScreenContent(
    name: String,
    description: String,
    selectedTimers: Set<Int>,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onTimerToggle: (Int) -> Unit,
    onSave: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_edit_workout_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(com.myworkoutroutine.core.ui.R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text(stringResource(R.string.workout_name_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text(stringResource(R.string.workout_description_label)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            Text(
                text = stringResource(R.string.workout_timers_label),
                style = MaterialTheme.typography.labelLarge
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(30, 60, 90, 120).forEach { seconds ->
                    FilterChip(
                        selected = selectedTimers.contains(seconds),
                        onClick = { onTimerToggle(seconds) },
                        label = {
                            Text(
                                text = when (seconds) {
                                    30 -> stringResource(R.string.timer_30)
                                    60 -> stringResource(R.string.timer_60)
                                    90 -> stringResource(R.string.timer_90)
                                    120 -> stringResource(R.string.timer_120)
                                    else -> "${seconds}s"
                                }
                            )
                        }
                    )
                }
            }

            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save_workout))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AddEditWorkoutScreenEmptyPreview() {
    MyWorkoutRoutineTheme {
        AddEditWorkoutScreenContent(
            name = "",
            description = "",
            selectedTimers = emptySet(),
            onNameChange = {},
            onDescriptionChange = {},
            onTimerToggle = {},
            onSave = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AddEditWorkoutScreenFilledPreview() {
    MyWorkoutRoutineTheme {
        AddEditWorkoutScreenContent(
            name = "Push-ups",
            description = "Standard push-ups for chest and triceps. Keep your body straight and lower yourself until your chest nearly touches the ground.",
            selectedTimers = setOf(30, 60, 90),
            onNameChange = {},
            onDescriptionChange = {},
            onTimerToggle = {},
            onSave = {},
            onNavigateBack = {}
        )
    }
}
