package com.myworkoutroutine.feature.workouts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myworkoutroutine.core.domain.model.WorkoutCard
import com.myworkoutroutine.core.domain.repository.WorkoutRepository
import com.myworkoutroutine.core.domain.usecase.SaveWorkoutCardUseCase
import com.myworkoutroutine.core.ui.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditWorkoutViewModel @Inject constructor(
    private val saveWorkoutCardUseCase: SaveWorkoutCardUseCase,
    private val repository: WorkoutRepository,
    private val widgetUpdater: com.myworkoutroutine.core.domain.util.WidgetUpdater,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var name by mutableStateOf("")
        private set

    var description by mutableStateOf("")
        private set

    var selectedTimers by mutableStateOf(setOf<Int>())
        private set

    private var currentCardId: Long = 0

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        val cardId = savedStateHandle.get<Long>("cardId")
        if (cardId != null && cardId != 0L) {
            viewModelScope.launch {
                repository.getWorkoutCardById(cardId).firstOrNull()?.let { card ->
                    currentCardId = card.id
                    name = card.name
                    description = card.description
                    selectedTimers = card.timers.toSet()
                }
            }
        }
    }

    fun onNameChange(value: String) {
        name = value
    }

    fun onDescriptionChange(value: String) {
        description = value
    }

    fun onTimerToggle(seconds: Int) {
        selectedTimers = if (selectedTimers.contains(seconds)) {
            selectedTimers - seconds
        } else {
            selectedTimers + seconds
        }
    }

    fun saveWorkout() {
        viewModelScope.launch {
            if (name.isBlank()) {
                _uiEvent.emit(
                    UiEvent.ShowError(
                        UiText.StringResource(R.string.error_name_empty)
                    )
                )
                return@launch
            }

            if (description.isBlank()) {
                _uiEvent.emit(
                    UiEvent.ShowError(
                        UiText.StringResource(R.string.error_description_empty)
                    )
                )
                return@launch
            }

            val card = WorkoutCard(
                id = currentCardId,
                name = name,
                description = description,
                timers = selectedTimers.sorted()
            )

            saveWorkoutCardUseCase(card)

            // Update widget to reflect changes
            widgetUpdater.updateWidgets()

            _uiEvent.emit(UiEvent.Success)
        }
    }

    sealed class UiEvent {
        object Success : UiEvent()
        data class ShowError(val message: UiText) : UiEvent()
    }
}
