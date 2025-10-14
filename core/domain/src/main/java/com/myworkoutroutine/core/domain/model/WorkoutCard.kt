package com.myworkoutroutine.core.domain.model

data class WorkoutCard(
    val id: Long = 0,
    val name: String,
    val description: String,
    val timers: List<Int> = emptyList(), // Timer values in seconds (30, 60, 90, 120)
    val createdAt: Long = System.currentTimeMillis()
)
