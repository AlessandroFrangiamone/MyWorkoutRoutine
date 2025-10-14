package com.myworkoutroutine.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.myworkoutroutine.core.domain.model.WorkoutCard
import com.myworkoutroutine.core.ui.R
import com.myworkoutroutine.core.ui.theme.MyWorkoutRoutineTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutCardItem(
    card: WorkoutCard,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with title and actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = card.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = card.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Timers
            if (card.timers.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = stringResource(R.string.timer),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    card.timers.forEach { seconds ->
                        AssistChip(
                            onClick = { },
                            label = {
                                Text(text = "${seconds}s")
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WorkoutCardItemPreview() {
    MyWorkoutRoutineTheme {
        WorkoutCardItem(
            card = WorkoutCard(
                id = 1,
                name = "Push-ups",
                description = "Standard push-ups for chest and triceps. Keep your body straight and lower yourself until your chest nearly touches the ground.",
                timers = listOf(30, 60, 90)
            ),
            onEdit = {},
            onDelete = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WorkoutCardItemWithAllTimersPreview() {
    MyWorkoutRoutineTheme {
        WorkoutCardItem(
            card = WorkoutCard(
                id = 1,
                name = "Plank Hold",
                description = "Core stability exercise maintaining straight body position",
                timers = listOf(30, 60, 90, 120)
            ),
            onEdit = {},
            onDelete = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WorkoutCardItemNoTimersPreview() {
    MyWorkoutRoutineTheme {
        WorkoutCardItem(
            card = WorkoutCard(
                id = 1,
                name = "Free Weight Exercise",
                description = "Custom exercise without pre-defined timers",
                timers = emptyList()
            ),
            onEdit = {},
            onDelete = {}
        )
    }
}
