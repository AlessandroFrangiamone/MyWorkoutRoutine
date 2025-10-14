package com.myworkoutroutine.core.data.di

import android.content.Context
import androidx.room.Room
import com.myworkoutroutine.core.data.local.WorkoutDatabase
import com.myworkoutroutine.core.data.local.dao.TrainingPlanDao
import com.myworkoutroutine.core.data.local.dao.WorkoutCardDao
import com.myworkoutroutine.core.data.local.dao.WorkoutSessionDao
import com.myworkoutroutine.core.data.repository.WorkoutRepositoryImpl
import com.myworkoutroutine.core.domain.repository.WorkoutRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideWorkoutDatabase(
        @ApplicationContext context: Context
    ): WorkoutDatabase {
        return Room.databaseBuilder(
            context,
            WorkoutDatabase::class.java,
            WorkoutDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideWorkoutCardDao(database: WorkoutDatabase): WorkoutCardDao {
        return database.workoutCardDao()
    }

    @Provides
    @Singleton
    fun provideTrainingPlanDao(database: WorkoutDatabase): TrainingPlanDao {
        return database.trainingPlanDao()
    }

    @Provides
    @Singleton
    fun provideWorkoutSessionDao(database: WorkoutDatabase): WorkoutSessionDao {
        return database.workoutSessionDao()
    }

    @Provides
    @Singleton
    fun provideWorkoutRepository(
        workoutCardDao: WorkoutCardDao,
        trainingPlanDao: TrainingPlanDao,
        workoutSessionDao: WorkoutSessionDao
    ): WorkoutRepository {
        return WorkoutRepositoryImpl(workoutCardDao, trainingPlanDao, workoutSessionDao)
    }
}
