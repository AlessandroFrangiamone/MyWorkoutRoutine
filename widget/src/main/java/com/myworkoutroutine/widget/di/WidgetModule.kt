package com.myworkoutroutine.widget.di

import com.myworkoutroutine.core.domain.util.WidgetUpdater
import com.myworkoutroutine.widget.WorkoutWidgetUpdater
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class WidgetModule {

    @Binds
    @Singleton
    abstract fun bindWidgetUpdater(
        workoutWidgetUpdater: WorkoutWidgetUpdater
    ): WidgetUpdater
}
