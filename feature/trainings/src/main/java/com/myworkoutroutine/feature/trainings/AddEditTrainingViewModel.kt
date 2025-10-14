package com.myworkoutroutine.feature.trainings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myworkoutroutine.core.domain.model.TrainingPlan
import com.myworkoutroutine.core.domain.model.WorkoutCard
import com.myworkoutroutine.core.domain.repository.WorkoutRepository
import com.myworkoutroutine.core.ui.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTrainingViewModel @Inject constructor(
    private val repository: WorkoutRepository,
    private val widgetUpdater: com.myworkoutroutine.core.domain.util.WidgetUpdater,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var name by mutableStateOf("")
        private set

    var selectedCardIds by mutableStateOf(setOf<Long>())
        private set

    var isCurrentTraining by mutableStateOf(false)
        private set

    private var currentTrainingId: Long = 0

    val availableCards: StateFlow<List<WorkoutCard>> = repository.getAllWorkoutCards()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        android.util.Log.d("AddEditTrainingVM", "ViewModel created, widgetUpdater injected: ${widgetUpdater != null}")
        val trainingId = savedStateHandle.get<Long>("trainingId")
        if (trainingId != null && trainingId != 0L) {
            viewModelScope.launch {
                repository.getTrainingPlanById(trainingId).firstOrNull()?.let { plan ->
                    currentTrainingId = plan.id
                    name = plan.name
                    selectedCardIds = plan.workoutCardIds.toSet()
                    isCurrentTraining = plan.isCurrentTraining
                }
            }
        }
    }

    fun onNameChange(value: String) {
        name = value
    }

    fun onCardToggle(cardId: Long) {
        selectedCardIds = if (selectedCardIds.contains(cardId)) {
            selectedCardIds - cardId
        } else {
            selectedCardIds + cardId
        }
    }

    fun onCurrentTrainingToggle(value: Boolean) {
        isCurrentTraining = value
    }

    fun saveTraining() {
        viewModelScope.launch {
            if (name.isBlank()) {
                _uiEvent.emit(
                    UiEvent.ShowError(
                        UiText.StringResource(R.string.error_training_name_empty)
                    )
                )
                return@launch
            }

            if (selectedCardIds.isEmpty()) {
                _uiEvent.emit(
                    UiEvent.ShowError(
                        UiText.StringResource(R.string.error_no_cards_selected)
                    )
                )
                return@launch
            }

            if (selectedCardIds.size > 4) {
                _uiEvent.emit(
                    UiEvent.ShowError(
                        UiText.StringResource(R.string.error_too_many_cards)
                    )
                )
                return@launch
            }

            val training = TrainingPlan(
                id = currentTrainingId,
                name = name,
                workoutCardIds = selectedCardIds.toList(),
                isCurrentTraining = isCurrentTraining
            )

            val savedId = repository.insertTrainingPlan(training)

            if (isCurrentTraining && savedId > 0) {
                repository.setCurrentPlan(savedId)
            }

            // Update widget to reflect changes
            android.util.Log.d("AddEditTrainingVM", "About to call widgetUpdater.updateWidgets()")
            widgetUpdater.updateWidgets()
            android.util.Log.d("AddEditTrainingVM", "widgetUpdater.updateWidgets() completed")

            _uiEvent.emit(UiEvent.Success)
        }
    }

    sealed class UiEvent {
        object Success : UiEvent()
        data class ShowError(val message: UiText) : UiEvent()
    }
}
