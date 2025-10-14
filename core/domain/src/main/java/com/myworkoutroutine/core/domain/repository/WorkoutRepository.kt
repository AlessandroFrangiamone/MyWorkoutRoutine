package com.myworkoutroutine.core.domain.repository

import com.myworkoutroutine.core.domain.model.WorkoutCard
import com.myworkoutroutine.core.domain.model.TrainingPlan
import com.myworkoutroutine.core.domain.model.WorkoutSession
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    // Workout Cards
    fun getAllWorkoutCards(): Flow<List<WorkoutCard>>
    fun getWorkoutCardById(id: Long): Flow<WorkoutCard?>
    suspend fun insertWorkoutCard(card: WorkoutCard): Long
    suspend fun updateWorkoutCard(card: WorkoutCard)
    suspend fun deleteWorkoutCard(card: WorkoutCard)

    // Training Plans
    fun getAllTrainingPlans(): Flow<List<TrainingPlan>>
    fun getCurrentTrainingPlan(): Flow<TrainingPlan?>
    fun getTrainingPlanById(id: Long): Flow<TrainingPlan?>
    suspend fun insertTrainingPlan(plan: TrainingPlan): Long
    suspend fun updateTrainingPlan(plan: TrainingPlan)
    suspend fun deleteTrainingPlan(plan: TrainingPlan)
    suspend fun setCurrentPlan(planId: Long)

    // Workout Sessions
    fun getAllWorkoutSessions(): Flow<List<WorkoutSession>>
    fun getSessionsByWorkoutCard(workoutCardId: Long): Flow<List<WorkoutSession>>
    suspend fun insertWorkoutSession(session: WorkoutSession): Long
    suspend fun updateWorkoutSession(session: WorkoutSession)
}
