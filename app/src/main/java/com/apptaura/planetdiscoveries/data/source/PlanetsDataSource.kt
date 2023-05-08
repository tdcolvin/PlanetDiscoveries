/*
 * Copyright (C) 2019 The Android Open Source Project
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
package com.apptaura.planetdiscoveries.data.source

import com.apptaura.planetdiscoveries.data.Planet
import com.apptaura.planetdiscoveries.data.WorkResult
import kotlinx.coroutines.flow.Flow

/**
 * Main entry point for accessing planets data.
 */
interface PlanetsDataSource {

    fun getPlanetsStream(): Flow<WorkResult<List<Planet>>>

    suspend fun getPlanets(): WorkResult<List<Planet>>

    suspend fun refreshPlanets()

    fun getPlanetStream(planetId: String): Flow<WorkResult<Planet>>

    suspend fun getPlanet(planetId: String): WorkResult<Planet>

    suspend fun refreshPlanet(planetId: String)

    suspend fun savePlanet(planet: Planet)

    suspend fun deleteAllPlanets()

    suspend fun deletePlanet(planetId: String)
}
