package com.myworkoutroutine.core.domain.usecase

import com.myworkoutroutine.core.domain.model.TrainingPlan
import com.myworkoutroutine.core.domain.repository.WorkoutRepository
import javax.inject.Inject

class SaveTrainingPlanUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke(plan: TrainingPlan): Result<Long> {
        return if (plan.workoutCardIds.size > 4) {
            Result.failure(IllegalArgumentException("Training plan cannot have more than 4 workout cards"))
        } else {
            val id = if (plan.id == 0L) {
                repository.insertTrainingPlan(plan)
            } else {
                repository.updateTrainingPlan(plan)
                plan.id
            }
            Result.success(id)
        }
    }
}
