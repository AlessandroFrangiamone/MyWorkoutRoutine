package com.myworkoutroutine.feature.trainings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myworkoutroutine.core.domain.model.TrainingPlan
import com.myworkoutroutine.core.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingsViewModel @Inject constructor(
    private val repository: WorkoutRepository,
    private val widgetUpdater: com.myworkoutroutine.core.domain.util.WidgetUpdater
) : ViewModel() {

    val trainingPlans: StateFlow<List<TrainingPlan>> = repository.getAllTrainingPlans()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteTrainingPlan(plan: TrainingPlan) {
        viewModelScope.launch {
            repository.deleteTrainingPlan(plan)
            // Update widget to reflect changes
            widgetUpdater.updateWidgets()
        }
    }

    fun setCurrentTraining(planId: Long) {
        viewModelScope.launch {
            android.util.Log.d("TrainingsViewModel", "setCurrentTraining called for planId: $planId")
            repository.setCurrentPlan(planId)
            // Update widget to reflect changes
            android.util.Log.d("TrainingsViewModel", "About to call widgetUpdater.updateWidgets()")
            widgetUpdater.updateWidgets()
            android.util.Log.d("TrainingsViewModel", "widgetUpdater.updateWidgets() completed")
        }
    }
}
