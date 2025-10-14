package com.myworkoutroutine.widget

data class WorkoutTimerState(
    val isRunning: Boolean = false,
    val selectedTimerSeconds: Int = 0,
    val remainingSeconds: Int = 0,
    val currentCardIndex: Int = 0
)
