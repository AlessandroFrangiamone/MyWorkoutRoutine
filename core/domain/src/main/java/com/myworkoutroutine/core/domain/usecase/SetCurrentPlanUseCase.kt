package com.myworkoutroutine.core.domain.usecase

import com.myworkoutroutine.core.domain.repository.WorkoutRepository
import javax.inject.Inject

class SetCurrentPlanUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke(planId: Long) {
        repository.setCurrentPlan(planId)
    }
}
