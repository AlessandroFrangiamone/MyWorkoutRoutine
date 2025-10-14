package com.myworkoutroutine.feature.trainings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.myworkoutroutine.core.domain.model.TrainingPlan
import com.myworkoutroutine.core.ui.theme.MyWorkoutRoutineTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingsScreen(
    onNavigateToAddEdit: (Long?) -> Unit,
    viewModel: TrainingsViewModel = hiltViewModel()
) {
    val trainingPlans by viewModel.trainingPlans.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf<TrainingPlan?>(null) }
    var showSetCurrentDialog by remember { mutableStateOf<TrainingPlan?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.trainings_title)) },
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
                    contentDescription = stringResource(R.string.add_training)
                )
            }
        }
    ) { padding ->
        if (trainingPlans.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.no_trainings),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.no_trainings_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(trainingPlans, key = { it.id }) { plan ->
                    TrainingPlanItem(
                        plan = plan,
                        onEdit = { onNavigateToAddEdit(plan.id) },
                        onDelete = { showDeleteDialog = plan },
                        onSetCurrent = { showSetCurrentDialog = plan }
                    )
                }
            }
        }
    }

    // Delete confirmation dialog
    showDeleteDialog?.let { plan ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text(stringResource(R.string.confirm)) },
            text = { Text(stringResource(R.string.confirm_delete_training)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTrainingPlan(plan)
                        showDeleteDialog = null
                    }
                ) {
                    Text(stringResource(com.myworkoutroutine.core.ui.R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text(stringResource(com.myworkoutroutine.core.ui.R.string.cancel))
                }
            }
        )
    }

    // Set current training dialog
    showSetCurrentDialog?.let { plan ->
        AlertDialog(
            onDismissRequest = { showSetCurrentDialog = null },
            title = { Text(stringResource(R.string.confirm)) },
            text = {
                Column {
                    Text(stringResource(R.string.confirm_set_current))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.confirm_set_current_description),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.setCurrentTraining(plan.id)
                        showSetCurrentDialog = null
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showSetCurrentDialog = null }) {
                    Text(stringResource(com.myworkoutroutine.core.ui.R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun TrainingPlanItem(
    plan: TrainingPlan,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSetCurrent: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (plan.isCurrentTraining) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = plan.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = if (plan.isCurrentTraining) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                    if (plan.isCurrentTraining) {
                        Text(
                            text = stringResource(R.string.is_current_training),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(com.myworkoutroutine.core.ui.R.string.edit),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(com.myworkoutroutine.core.ui.R.string.delete),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    IconButton(onClick = onSetCurrent) {
                        Icon(
                            imageVector = if (plan.isCurrentTraining) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = stringResource(R.string.set_current_training),
                            tint = if (plan.isCurrentTraining) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${plan.workoutCardIds.size} workout cards",
                style = MaterialTheme.typography.bodyMedium,
                color = if (plan.isCurrentTraining) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TrainingPlanItemPreview() {
    MyWorkoutRoutineTheme {
        TrainingPlanItem(
            plan = TrainingPlan(
                id = 1,
                name = "Morning Workout",
                workoutCardIds = listOf(1, 2, 3),
                isCurrentTraining = false
            ),
            onEdit = {},
            onDelete = {},
            onSetCurrent = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TrainingPlanItemCurrentPreview() {
    MyWorkoutRoutineTheme {
        TrainingPlanItem(
            plan = TrainingPlan(
                id = 1,
                name = "Evening Workout",
                workoutCardIds = listOf(1, 2, 3, 4),
                isCurrentTraining = true
            ),
            onEdit = {},
            onDelete = {},
            onSetCurrent = {}
        )
    }
}
