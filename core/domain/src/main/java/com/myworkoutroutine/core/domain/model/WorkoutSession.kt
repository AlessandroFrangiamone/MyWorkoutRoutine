package com.myworkoutroutine.core.domain.model

data class WorkoutSession(
    val id: Long = 0,
    val workoutCardId: Long,
    val startTime: Long,
    val endTime: Long? = null,
    val completed: Boolean = false
)
