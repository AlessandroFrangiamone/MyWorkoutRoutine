package com.myworkoutroutine.core.domain.model

data class TrainingPlan(
    val id: Long = 0,
    val name: String,
    val workoutCardIds: List<Long>,
    val isCurrentTraining: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
