package com.myworkoutroutine.feature.trainings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.myworkoutroutine.core.domain.model.WorkoutCard
import com.myworkoutroutine.core.ui.theme.MyWorkoutRoutineTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTrainingScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddEditTrainingViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val availableCards by viewModel.availableCards.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is AddEditTrainingViewModel.UiEvent.Success -> onNavigateBack()
                is AddEditTrainingViewModel.UiEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message.asString(context))
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_edit_training_title)) },
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
                label = { Text(stringResource(R.string.training_name_label)) },
                placeholder = { Text(stringResource(R.string.training_name_hint)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.set_as_current),
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = viewModel.isCurrentTraining,
                    onCheckedChange = viewModel::onCurrentTrainingToggle
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.select_workout_cards),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${viewModel.selectedCardIds.size}/4",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (viewModel.selectedCardIds.size > 4) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            if (availableCards.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No workout cards available.\nCreate some first!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    availableCards.forEach { card ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (viewModel.selectedCardIds.contains(card.id)) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = card.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (viewModel.selectedCardIds.contains(card.id)) {
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                    if (card.timers.isNotEmpty()) {
                                        Text(
                                            text = "Timers: ${card.timers.joinToString(", ") { "${it}s" }}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (viewModel.selectedCardIds.contains(card.id)) {
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                            } else {
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                            }
                                        )
                                    }
                                }
                                Checkbox(
                                    checked = viewModel.selectedCardIds.contains(card.id),
                                    onCheckedChange = { viewModel.onCardToggle(card.id) }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = viewModel::saveTraining,
                modifier = Modifier.fillMaxWidth(),
                enabled = availableCards.isNotEmpty()
            ) {
                Text(stringResource(com.myworkoutroutine.core.ui.R.string.save))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditTrainingScreenContent(
    name: String,
    isCurrentTraining: Boolean,
    availableCards: List<WorkoutCard>,
    selectedCardIds: Set<Long>,
    onNameChange: (String) -> Unit,
    onCurrentTrainingToggle: (Boolean) -> Unit,
    onCardToggle: (Long) -> Unit,
    onSave: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_edit_training_title)) },
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
                label = { Text(stringResource(R.string.training_name_label)) },
                placeholder = { Text(stringResource(R.string.training_name_hint)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.set_as_current),
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = isCurrentTraining,
                    onCheckedChange = onCurrentTrainingToggle
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.select_workout_cards),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${selectedCardIds.size}/4",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (selectedCardIds.size > 4) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            if (availableCards.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No workout cards available.\\nCreate some first!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    availableCards.forEach { card ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedCardIds.contains(card.id)) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = card.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (selectedCardIds.contains(card.id)) {
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                    if (card.timers.isNotEmpty()) {
                                        Text(
                                            text = "Timers: ${card.timers.joinToString(", ") { "${it}s" }}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (selectedCardIds.contains(card.id)) {
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                            } else {
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                            }
                                        )
                                    }
                                }
                                Checkbox(
                                    checked = selectedCardIds.contains(card.id),
                                    onCheckedChange = { onCardToggle(card.id) }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                enabled = availableCards.isNotEmpty()
            ) {
                Text(stringResource(com.myworkoutroutine.core.ui.R.string.save))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AddEditTrainingScreenNoCardsPreview() {
    MyWorkoutRoutineTheme {
        AddEditTrainingScreenContent(
            name = "",
            isCurrentTraining = false,
            availableCards = emptyList(),
            selectedCardIds = emptySet(),
            onNameChange = {},
            onCurrentTrainingToggle = {},
            onCardToggle = {},
            onSave = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AddEditTrainingScreenWithCardsPreview() {
    MyWorkoutRoutineTheme {
        AddEditTrainingScreenContent(
            name = "Morning Workout",
            isCurrentTraining = true,
            availableCards = listOf(
                WorkoutCard(
                    id = 1,
                    name = "Push-ups",
                    description = "Standard push-ups",
                    timers = listOf(30, 60, 90)
                ),
                WorkoutCard(
                    id = 2,
                    name = "Squats",
                    description = "Bodyweight squats",
                    timers = listOf(60, 90, 120)
                ),
                WorkoutCard(
                    id = 3,
                    name = "Plank",
                    description = "Core stability",
                    timers = listOf(30, 60)
                ),
                WorkoutCard(
                    id = 4,
                    name = "Lunges",
                    description = "Forward lunges",
                    timers = listOf(30, 60)
                )
            ),
            selectedCardIds = setOf(1, 3),
            onNameChange = {},
            onCurrentTrainingToggle = {},
            onCardToggle = {},
            onSave = {},
            onNavigateBack = {}
        )
    }
}
