package com.myworkoutroutine.core.domain.usecase

import com.myworkoutroutine.core.domain.model.WorkoutCard
import com.myworkoutroutine.core.domain.repository.WorkoutRepository
import javax.inject.Inject

class SaveWorkoutCardUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke(card: WorkoutCard): Long {
        return if (card.id == 0L) {
            repository.insertWorkoutCard(card)
        } else {
            repository.updateWorkoutCard(card)
            card.id
        }
    }
}
