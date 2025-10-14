package com.myworkoutroutine.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.myworkoutroutine.core.domain.model.TrainingPlan
import com.myworkoutroutine.core.domain.model.WorkoutCard
import com.myworkoutroutine.core.domain.repository.WorkoutRepository
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class WorkoutWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        android.util.Log.d("WorkoutWidget", "provideGlance() called - widget is refreshing")
        val repository = try {
            getRepository(context)
        } catch (e: Exception) {
            android.util.Log.e("WorkoutWidget", "Error getting repository", e)
            null
        }
        val prefs = context.getSharedPreferences(TimerWorker.WIDGET_PREFS, Context.MODE_PRIVATE)

        val currentTraining = repository?.getCurrentTrainingPlan()?.first()
        val cards = if (currentTraining != null && repository != null) {
            currentTraining.workoutCardIds.mapNotNull { cardId ->
                repository.getWorkoutCardById(cardId).first()
            }
        } else {
            emptyList()
        }

        val currentCardIndex = prefs.getInt(TimerWorker.KEY_CURRENT_CARD_INDEX, 0)
            .coerceIn(0, (cards.size - 1).coerceAtLeast(0))
        val isRunning = prefs.getBoolean(TimerWorker.KEY_IS_RUNNING, false)
        val remainingSeconds = prefs.getInt(TimerWorker.KEY_REMAINING_SECONDS, 0)

        provideContent {
            WorkoutWidgetContent(
                training = currentTraining,
                cards = cards,
                currentCardIndex = currentCardIndex,
                isRunning = isRunning,
                remainingSeconds = remainingSeconds
            )
        }
    }

    @Composable
    private fun WorkoutWidgetContent(
        training: TrainingPlan?,
        cards: List<WorkoutCard>,
        currentCardIndex: Int,
        isRunning: Boolean,
        remainingSeconds: Int
    ) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(WidgetColors.CarbonFiber)
                .padding(12.dp)
        ) {
            if (training == null || cards.isEmpty()) {
                EmptyState()
            } else {
                WorkoutContent(
                    training = training,
                    card = cards[currentCardIndex],
                    cardIndex = currentCardIndex,
                    totalCards = cards.size,
                    isRunning = isRunning,
                    remainingSeconds = remainingSeconds
                )
            }
        }
    }

    @Composable
    private fun EmptyState() {
        Box(
            modifier = GlanceModifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No current training set",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = WidgetColors.OffWhite,
                    textAlign = TextAlign.Center
                )
            )
        }
    }

    @Composable
    private fun WorkoutContent(
        training: TrainingPlan,
        card: WorkoutCard,
        cardIndex: Int,
        totalCards: Int,
        isRunning: Boolean,
        remainingSeconds: Int
    ) {
        Column(modifier = GlanceModifier.fillMaxSize()) {
            // Header with navigation
            HeaderWithNavigation(
                trainingName = training.name,
                cardIndex = cardIndex,
                totalCards = totalCards,
                isRunning = isRunning
            )

            Spacer(modifier = GlanceModifier.height(8.dp))

            // Card info
            CardInfo(card)

            Spacer(modifier = GlanceModifier.height(12.dp))

            // Timer display
            TimerDisplay(remainingSeconds, isRunning)

            Spacer(modifier = GlanceModifier.height(8.dp))

            // Timer buttons
            TimerButtons(card, isRunning)
        }
    }

    @Composable
    private fun HeaderWithNavigation(
        trainingName: String,
        cardIndex: Int,
        totalCards: Int,
        isRunning: Boolean
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = trainingName,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = WidgetColors.OffWhite
                ),
                modifier = GlanceModifier.defaultWeight()
            )

            if (totalCards > 1 && !isRunning) {
                NavigationArrows(cardIndex, totalCards)
            }
        }
    }

    @Composable
    private fun NavigationArrows(cardIndex: Int, totalCards: Int) {
        Row(
            horizontalAlignment = Alignment.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous button
            if (cardIndex > 0) {
                Box(
                    modifier = GlanceModifier
                        .size(32.dp)
                        .background(WidgetColors.CarbonFiberLight)
                        .cornerRadius(4.dp)
                        .clickable(WidgetActions.previousCard()),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "<",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = WidgetColors.OffWhite
                        )
                    )
                }
            }

            Spacer(modifier = GlanceModifier.width(4.dp))

            // Next button
            if (cardIndex < totalCards - 1) {
                Box(
                    modifier = GlanceModifier
                        .size(32.dp)
                        .background(WidgetColors.CarbonFiberLight)
                        .cornerRadius(4.dp)
                        .clickable(WidgetActions.nextCard()),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = ">",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = WidgetColors.OffWhite
                        )
                    )
                }
            }
        }
    }

    @Composable
    private fun CardInfo(card: WorkoutCard) {
        Column(
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(WidgetColors.CarbonFiberLight)
                .cornerRadius(8.dp)
                .padding(12.dp)
        ) {
            Text(
                text = card.name,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = WidgetColors.FitnessRed
                )
            )

            Spacer(modifier = GlanceModifier.height(6.dp))

            Text(
                text = card.description,
                style = TextStyle(
                    fontSize = 12.sp,
                    color = WidgetColors.OffWhite
                ),
                maxLines = 4
            )
        }
    }

    @Composable
    private fun TimerDisplay(remainingSeconds: Int, isRunning: Boolean) {
        Box(
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(WidgetColors.CarbonFiberDark)
                .cornerRadius(12.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = formatTime(remainingSeconds),
                    style = TextStyle(
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isRunning) WidgetColors.FitnessRed else WidgetColors.OffWhite
                    )
                )
                if (isRunning) {
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Text(
                        text = "RUNNING",
                        style = TextStyle(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = WidgetColors.FitnessRed
                        )
                    )
                }
            }
        }
    }

    @Composable
    private fun TimerButtons(card: WorkoutCard, isRunning: Boolean) {
        if (card.timers.isEmpty()) {
            Text(
                text = "No timers available",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = WidgetColors.OffWhite,
                    textAlign = TextAlign.Center
                ),
                modifier = GlanceModifier.fillMaxWidth()
            )
            return
        }

        Column {
            // Timer selection chips (only when not running)
            if (!isRunning) {
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    card.timers.forEach { seconds ->
                        Box(
                            modifier = GlanceModifier
                                .padding(4.dp)
                                .background(WidgetColors.CarbonFiberLight)
                                .cornerRadius(16.dp)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .clickable(WidgetActions.selectTimer(seconds)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${seconds}s",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = WidgetColors.OffWhite
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = GlanceModifier.height(8.dp))
            }

            // Control buttons
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isRunning) {
                    // Start button
                    ControlButton(
                        text = "START",
                        backgroundColor = WidgetColors.FitnessRed,
                        action = WidgetActions.startTimer()
                    )
                } else {
                    // Pause button
                    ControlButton(
                        text = "PAUSE",
                        backgroundColor = WidgetColors.Orange,
                        action = WidgetActions.pauseTimer()
                    )

                    Spacer(modifier = GlanceModifier.width(8.dp))

                    // Reset button
                    ControlButton(
                        text = "RESET",
                        backgroundColor = WidgetColors.Gray,
                        action = WidgetActions.resetTimer()
                    )
                }
            }
        }
    }

    @Composable
    private fun ControlButton(
        text: String,
        backgroundColor: ColorProvider,
        action: androidx.glance.action.Action
    ) {
        Box(
            modifier = GlanceModifier
                .background(backgroundColor)
                .cornerRadius(8.dp)
                .padding(horizontal = 24.dp, vertical = 10.dp)
                .clickable(action),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = WidgetColors.OffWhite
                )
            )
        }
    }

    private fun formatTime(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", mins, secs)
    }

    private fun getRepository(context: Context): WorkoutRepository {
        val appContext = context.applicationContext
        val entryPoint = EntryPointAccessors.fromApplication(
            appContext,
            WidgetEntryPoint::class.java
        )
        return entryPoint.workoutRepository()
    }
}

class WorkoutWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WorkoutWidget()
}
