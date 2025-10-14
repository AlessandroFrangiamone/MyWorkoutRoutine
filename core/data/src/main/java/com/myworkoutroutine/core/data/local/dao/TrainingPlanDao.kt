package com.myworkoutroutine.core.data.local.dao

import androidx.room.*
import com.myworkoutroutine.core.data.local.entity.TrainingPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingPlanDao {
    @Query("SELECT * FROM training_plans ORDER BY createdAt DESC")
    fun getAllTrainingPlans(): Flow<List<TrainingPlanEntity>>

    @Query("SELECT * FROM training_plans WHERE isCurrentTraining = 1 LIMIT 1")
    fun getCurrentTrainingPlan(): Flow<TrainingPlanEntity?>

    @Query("SELECT * FROM training_plans WHERE id = :id")
    fun getTrainingPlanById(id: Long): Flow<TrainingPlanEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrainingPlan(plan: TrainingPlanEntity): Long

    @Update
    suspend fun updateTrainingPlan(plan: TrainingPlanEntity)

    @Delete
    suspend fun deleteTrainingPlan(plan: TrainingPlanEntity)

    @Query("UPDATE training_plans SET isCurrentTraining = 0")
    suspend fun clearAllCurrentTrainings()

    @Query("UPDATE training_plans SET isCurrentTraining = 1 WHERE id = :planId")
    suspend fun setCurrentTraining(planId: Long)
}
