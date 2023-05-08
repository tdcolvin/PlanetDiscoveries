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
package com.apptaura.planetdiscoveries.data.source.local

import com.apptaura.planetdiscoveries.data.Planet
import com.apptaura.planetdiscoveries.data.WorkResult
import com.apptaura.planetdiscoveries.data.source.PlanetsDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Concrete implementation of a data source as a db.
 */
class PlanetsLocalDataSource internal constructor(
    private val planetsDao: PlanetsDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : PlanetsDataSource {

    override fun getPlanetsStream(): Flow<WorkResult<List<Planet>>> {
        return planetsDao.observePlanets().map {
            WorkResult.Success(it)
        }
    }

    override fun getPlanetStream(planetId: String): Flow<WorkResult<Planet>> {
        return planetsDao.observePlanetById(planetId).map {
            WorkResult.Success(it)
        }
    }

    override suspend fun refreshPlanet(planetId: String) {
        // NO-OP
    }

    override suspend fun refreshPlanets() {
        // NO-OP
    }

    override suspend fun getPlanets(): WorkResult<List<Planet>> = withContext(ioDispatcher) {
        return@withContext try {
            WorkResult.Success(planetsDao.getPlanets())
        } catch (e: Exception) {
            WorkResult.Error(e)
        }
    }

    override suspend fun getPlanet(planetId: String): WorkResult<Planet> = withContext(ioDispatcher) {
        try {
            val planet = planetsDao.getPlanetById(planetId)
            if (planet != null) {
                return@withContext WorkResult.Success(planet)
            } else {
                return@withContext WorkResult.Error(Exception("Planet not found!"))
            }
        } catch (e: Exception) {
            return@withContext WorkResult.Error(e)
        }
    }

    override suspend fun savePlanet(planet: Planet) = withContext(ioDispatcher) {
        planetsDao.insertPlanet(planet)
    }

    override suspend fun deleteAllPlanets() = withContext(ioDispatcher) {
        planetsDao.deletePlanets()
    }

    override suspend fun deletePlanet(planetId: String) = withContext<Unit>(ioDispatcher) {
        planetsDao.deletePlanetById(planetId)
    }
}
