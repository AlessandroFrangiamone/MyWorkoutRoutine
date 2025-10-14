package com.myworkoutroutine.feature.workouts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myworkoutroutine.core.domain.model.WorkoutCard
import com.myworkoutroutine.core.domain.repository.WorkoutRepository
import com.myworkoutroutine.core.domain.usecase.DeleteWorkoutCardUseCase
import com.myworkoutroutine.core.domain.usecase.GetAllWorkoutCardsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutsViewModel @Inject constructor(
    private val getAllWorkoutCardsUseCase: GetAllWorkoutCardsUseCase,
    private val deleteWorkoutCardUseCase: DeleteWorkoutCardUseCase,
    private val repository: WorkoutRepository,
    private val widgetUpdater: com.myworkoutroutine.core.domain.util.WidgetUpdater
) : ViewModel() {

    val workoutCards: StateFlow<List<WorkoutCard>> = getAllWorkoutCardsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _cardInUseEvent = MutableSharedFlow<Unit>()
    val cardInUseEvent = _cardInUseEvent.asSharedFlow()

    fun deleteWorkoutCard(card: WorkoutCard) {
        viewModelScope.launch {
            // Check if card is used in any training plan
            val trainingPlans = repository.getAllTrainingPlans().first()
            val isCardUsed = trainingPlans.any { plan ->
                plan.workoutCardIds.contains(card.id)
            }

            if (isCardUsed) {
                _cardInUseEvent.emit(Unit)
            } else {
                deleteWorkoutCardUseCase(card)
                // Update widget to reflect changes
                widgetUpdater.updateWidgets()
            }
        }
    }
}
