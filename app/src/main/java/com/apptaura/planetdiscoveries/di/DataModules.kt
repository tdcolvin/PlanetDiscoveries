/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.apptaura.planetdiscoveries.di

import android.content.Context
import androidx.room.Room
import com.apptaura.planetdiscoveries.data.source.DefaultPlanetsRepository
import com.apptaura.planetdiscoveries.data.source.PlanetsDataSource
import com.apptaura.planetdiscoveries.data.source.PlanetsRepository
import com.apptaura.planetdiscoveries.data.source.local.PlanetsDatabase
import com.apptaura.planetdiscoveries.data.source.local.PlanetsLocalDataSource
import com.apptaura.planetdiscoveries.data.source.remote.PlanetsRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RemotePlanetsDataSource

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LocalPlanetsDataSource

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun providePlanetsRepository(
        @RemotePlanetsDataSource remoteDataSource: PlanetsDataSource,
        @LocalPlanetsDataSource localDataSource: PlanetsDataSource,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): PlanetsRepository {
        return DefaultPlanetsRepository(remoteDataSource, localDataSource, ioDispatcher)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Singleton
    @RemotePlanetsDataSource
    @Provides
    fun providePlanetsRemoteDataSource(): PlanetsDataSource = PlanetsRemoteDataSource

    @Singleton
    @LocalPlanetsDataSource
    @Provides
    fun providePlanetsLocalDataSource(
        database: PlanetsDatabase,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): PlanetsDataSource {
        return PlanetsLocalDataSource(database.planetsDao(), ioDispatcher)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): PlanetsDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            PlanetsDatabase::class.java,
            "Planets.db"
        ).build()
    }
}
