package com.myworkoutroutine.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.myworkoutroutine.core.data.local.dao.TrainingPlanDao
import com.myworkoutroutine.core.data.local.dao.WorkoutCardDao
import com.myworkoutroutine.core.data.local.dao.WorkoutSessionDao
import com.myworkoutroutine.core.data.local.entity.TrainingPlanEntity
import com.myworkoutroutine.core.data.local.entity.WorkoutCardEntity
import com.myworkoutroutine.core.data.local.entity.WorkoutSessionEntity

@Database(
    entities = [
        WorkoutCardEntity::class,
        TrainingPlanEntity::class,
        WorkoutSessionEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class WorkoutDatabase : RoomDatabase() {
    abstract fun workoutCardDao(): WorkoutCardDao
    abstract fun trainingPlanDao(): TrainingPlanDao
    abstract fun workoutSessionDao(): WorkoutSessionDao

    companion object {
        const val DATABASE_NAME = "workout_database"
    }
}
