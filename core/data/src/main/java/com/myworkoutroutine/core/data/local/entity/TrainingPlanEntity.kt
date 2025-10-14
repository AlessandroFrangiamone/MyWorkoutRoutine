package com.myworkoutroutine.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.myworkoutroutine.core.domain.model.TrainingPlan

@Entity(tableName = "training_plans")
data class TrainingPlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val workoutCardIds: String, // Stored as comma-separated string
    val isCurrentTraining: Boolean,
    val createdAt: Long
)

fun TrainingPlanEntity.toDomain(): TrainingPlan {
    return TrainingPlan(
        id = id,
        name = name,
        workoutCardIds = workoutCardIds.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { it.toLong() },
        isCurrentTraining = isCurrentTraining,
        createdAt = createdAt
    )
}

fun TrainingPlan.toEntity(): TrainingPlanEntity {
    return TrainingPlanEntity(
        id = id,
        name = name,
        workoutCardIds = workoutCardIds.joinToString(","),
        isCurrentTraining = isCurrentTraining,
        createdAt = createdAt
    )
}
