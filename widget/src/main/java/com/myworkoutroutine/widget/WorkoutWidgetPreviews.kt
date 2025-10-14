package com.myworkoutroutine.widget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.myworkoutroutine.core.ui.theme.MyWorkoutRoutineTheme

/**
 * Mock previews for WorkoutWidget appearance
 * Note: Actual widget uses Glance API which doesn't support @Preview
 * These are visual approximations using standard Compose
 */

@Composable
private fun WidgetMockContainer(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier
            .width(300.dp)
            .height(400.dp),
        color = Color(0xFF1A1A1D),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.padding(12.dp)) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WorkoutWidgetEmptyStatePreview() {
    MyWorkoutRoutineTheme {
        WidgetMockContainer {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No current training set",
                    fontSize = 14.sp,
                    color = Color(0xFFF5F5F7),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WorkoutWidgetWithCardPreview() {
    MyWorkoutRoutineTheme {
        WidgetMockContainer {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header with navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Morning Workout",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF5F5F7)
                    )
                    Row {
                        Surface(
                            modifier = Modifier.size(32.dp),
                            color = Color(0xFF2D2D30),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "<",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF5F5F7)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Surface(
                            modifier = Modifier.size(32.dp),
                            color = Color(0xFF2D2D30),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = ">",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF5F5F7)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Card info
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF2D2D30),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Push-ups",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE63946)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Standard push-ups for chest and triceps",
                            fontSize = 12.sp,
                            color = Color(0xFFF5F5F7),
                            maxLines = 4
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Timer display (not running)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF1F1F22),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier.padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "01:30",
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF5F5F7)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Timer chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    listOf("30s", "60s", "90s", "120s").forEach { time ->
                        Surface(
                            modifier = Modifier.padding(4.dp),
                            color = Color(0xFF2D2D30),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = time,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFF5F5F7)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Start button
                Surface(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color(0xFFE63946),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "START",
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF5F5F7)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WorkoutWidgetRunningTimerPreview() {
    MyWorkoutRoutineTheme {
        WidgetMockContainer {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header (no navigation when running)
                Text(
                    text = "Evening Workout",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF5F5F7)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Card info
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF2D2D30),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Plank",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE63946)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Core stability exercise maintaining straight body",
                            fontSize = 12.sp,
                            color = Color(0xFFF5F5F7),
                            maxLines = 4
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Timer display (running)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF1F1F22),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "00:45",
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE63946)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "RUNNING",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFE63946)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Pause and Reset buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Surface(
                        color = Color(0xFFFFA500),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "PAUSE",
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF5F5F7)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        color = Color(0xFF666666),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "RESET",
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF5F5F7)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WorkoutWidgetNoTimersPreview() {
    MyWorkoutRoutineTheme {
        WidgetMockContainer {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Text(
                    text = "Custom Workout",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF5F5F7)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Card info
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF2D2D30),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Free Exercise",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE63946)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Exercise without pre-defined timers",
                            fontSize = 12.sp,
                            color = Color(0xFFF5F5F7),
                            maxLines = 4
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Timer display
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF1F1F22),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier.padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "00:00",
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF5F5F7)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // No timers message
                Text(
                    text = "No timers available",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 12.sp,
                    color = Color(0xFFF5F5F7),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
