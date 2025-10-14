package com.myworkoutroutine.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.myworkoutroutine.core.domain.model.WorkoutSession

@Entity(tableName = "workout_sessions")
data class WorkoutSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val workoutCardId: Long,
    val startTime: Long,
    val endTime: Long?,
    val completed: Boolean
)

fun WorkoutSessionEntity.toDomain(): WorkoutSession {
    return WorkoutSession(
        id = id,
        workoutCardId = workoutCardId,
        startTime = startTime,
        endTime = endTime,
        completed = completed
    )
}

fun WorkoutSession.toEntity(): WorkoutSessionEntity {
    return WorkoutSessionEntity(
        id = id,
        workoutCardId = workoutCardId,
        startTime = startTime,
        endTime = endTime,
        completed = completed
    )
}
