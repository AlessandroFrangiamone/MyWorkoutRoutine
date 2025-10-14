package com.myworkoutroutine.core.domain.usecase

import com.myworkoutroutine.core.domain.model.TrainingPlan
import com.myworkoutroutine.core.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentTrainingPlanUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    operator fun invoke(): Flow<TrainingPlan?> {
        return repository.getCurrentTrainingPlan()
    }
}
