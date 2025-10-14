package com.myworkoutroutine.core.data.repository

import com.myworkoutroutine.core.data.local.dao.TrainingPlanDao
import com.myworkoutroutine.core.data.local.dao.WorkoutCardDao
import com.myworkoutroutine.core.data.local.dao.WorkoutSessionDao
import com.myworkoutroutine.core.data.local.entity.toDomain
import com.myworkoutroutine.core.data.local.entity.toEntity
import com.myworkoutroutine.core.domain.model.TrainingPlan
import com.myworkoutroutine.core.domain.model.WorkoutCard
import com.myworkoutroutine.core.domain.model.WorkoutSession
import com.myworkoutroutine.core.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WorkoutRepositoryImpl @Inject constructor(
    private val workoutCardDao: WorkoutCardDao,
    private val trainingPlanDao: TrainingPlanDao,
    private val workoutSessionDao: WorkoutSessionDao
) : WorkoutRepository {

    override fun getAllWorkoutCards(): Flow<List<WorkoutCard>> {
        return workoutCardDao.getAllWorkoutCards().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getWorkoutCardById(id: Long): Flow<WorkoutCard?> {
        return workoutCardDao.getWorkoutCardById(id).map { it?.toDomain() }
    }

    override suspend fun insertWorkoutCard(card: WorkoutCard): Long {
        return workoutCardDao.insertWorkoutCard(card.toEntity())
    }

    override suspend fun updateWorkoutCard(card: WorkoutCard) {
        workoutCardDao.updateWorkoutCard(card.toEntity())
    }

    override suspend fun deleteWorkoutCard(card: WorkoutCard) {
        workoutCardDao.deleteWorkoutCard(card.toEntity())
    }

    override fun getAllTrainingPlans(): Flow<List<TrainingPlan>> {
        return trainingPlanDao.getAllTrainingPlans().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getCurrentTrainingPlan(): Flow<TrainingPlan?> {
        return trainingPlanDao.getCurrentTrainingPlan().map { it?.toDomain() }
    }

    override fun getTrainingPlanById(id: Long): Flow<TrainingPlan?> {
        return trainingPlanDao.getTrainingPlanById(id).map { it?.toDomain() }
    }

    override suspend fun insertTrainingPlan(plan: TrainingPlan): Long {
        return trainingPlanDao.insertTrainingPlan(plan.toEntity())
    }

    override suspend fun updateTrainingPlan(plan: TrainingPlan) {
        trainingPlanDao.updateTrainingPlan(plan.toEntity())
    }

    override suspend fun deleteTrainingPlan(plan: TrainingPlan) {
        trainingPlanDao.deleteTrainingPlan(plan.toEntity())
    }

    override suspend fun setCurrentPlan(planId: Long) {
        trainingPlanDao.clearAllCurrentTrainings()
        trainingPlanDao.setCurrentTraining(planId)
    }

    override fun getAllWorkoutSessions(): Flow<List<WorkoutSession>> {
        return workoutSessionDao.getAllWorkoutSessions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getSessionsByWorkoutCard(workoutCardId: Long): Flow<List<WorkoutSession>> {
        return workoutSessionDao.getSessionsByWorkoutCard(workoutCardId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertWorkoutSession(session: WorkoutSession): Long {
        return workoutSessionDao.insertWorkoutSession(session.toEntity())
    }

    override suspend fun updateWorkoutSession(session: WorkoutSession) {
        workoutSessionDao.updateWorkoutSession(session.toEntity())
    }
}
