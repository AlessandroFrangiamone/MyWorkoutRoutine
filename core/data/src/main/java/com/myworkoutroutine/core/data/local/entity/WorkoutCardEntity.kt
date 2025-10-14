package com.myworkoutroutine.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.myworkoutroutine.core.domain.model.WorkoutCard

@Entity(tableName = "workout_cards")
data class WorkoutCardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val timers: String, // Stored as comma-separated string of integers
    val createdAt: Long
)

fun WorkoutCardEntity.toDomain(): WorkoutCard {
    return WorkoutCard(
        id = id,
        name = name,
        description = description,
        timers = if (timers.isBlank()) {
            emptyList()
        } else {
            timers.split(",").mapNotNull { it.trim().toIntOrNull() }
        },
        createdAt = createdAt
    )
}

fun WorkoutCard.toEntity(): WorkoutCardEntity {
    return WorkoutCardEntity(
        id = id,
        name = name,
        description = description,
        timers = timers.joinToString(","),
        createdAt = createdAt
    )
}
