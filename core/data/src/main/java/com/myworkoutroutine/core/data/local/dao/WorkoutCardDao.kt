package com.myworkoutroutine.core.data.local.dao

import androidx.room.*
import com.myworkoutroutine.core.data.local.entity.WorkoutCardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutCardDao {
    @Query("SELECT * FROM workout_cards ORDER BY createdAt DESC")
    fun getAllWorkoutCards(): Flow<List<WorkoutCardEntity>>

    @Query("SELECT * FROM workout_cards WHERE id = :id")
    fun getWorkoutCardById(id: Long): Flow<WorkoutCardEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutCard(card: WorkoutCardEntity): Long

    @Update
    suspend fun updateWorkoutCard(card: WorkoutCardEntity)

    @Delete
    suspend fun deleteWorkoutCard(card: WorkoutCardEntity)
}
