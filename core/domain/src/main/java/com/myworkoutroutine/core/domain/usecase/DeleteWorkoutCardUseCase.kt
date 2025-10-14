package com.myworkoutroutine.core.domain.usecase

import com.myworkoutroutine.core.domain.model.WorkoutCard
import com.myworkoutroutine.core.domain.repository.WorkoutRepository
import javax.inject.Inject

class DeleteWorkoutCardUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke(card: WorkoutCard) {
        repository.deleteWorkoutCard(card)
    }
}
