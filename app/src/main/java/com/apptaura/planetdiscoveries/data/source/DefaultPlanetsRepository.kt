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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Default implementation of [PlanetsRepository]. Single entry point for managing planets' data.
 */
class DefaultPlanetsRepository(
    private val planetsRemoteDataSource: PlanetsDataSource,
    private val planetsLocalDataSource: PlanetsDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : PlanetsRepository {

    override suspend fun getPlanets(forceUpdate: Boolean): WorkResult<List<Planet>> {
        if (forceUpdate) {
            try {
                updatePlanetsFromRemoteDataSource()
            } catch (ex: Exception) {
                return WorkResult.Error(ex)
            }
        }
        return planetsLocalDataSource.getPlanets()
    }

    override suspend fun refreshPlanets() {
        updatePlanetsFromRemoteDataSource()
    }

    override fun getPlanetsStream(): Flow<WorkResult<List<Planet>>> {
        return planetsLocalDataSource.getPlanetsStream()
    }

    override suspend fun refreshPlanet(planetId: String) {
        updatePlanetFromRemoteDataSource(planetId)
    }

    private suspend fun updatePlanetsFromRemoteDataSource() {
        val remotePlanets = planetsRemoteDataSource.getPlanets()

        if (remotePlanets is WorkResult.Success) {
            // Real apps might want to do a proper sync, deleting, modifying or adding each planet.
            planetsLocalDataSource.deleteAllPlanets()
            remotePlanets.data.forEach { planet ->
                planetsLocalDataSource.savePlanet(planet)
            }
        } else if (remotePlanets is WorkResult.Error) {
            throw remotePlanets.exception
        }
    }

    override fun getPlanetStream(planetId: String): Flow<WorkResult<Planet>> {
        return planetsLocalDataSource.getPlanetStream(planetId)
    }

    private suspend fun updatePlanetFromRemoteDataSource(planetId: String) {
        val remotePlanet = planetsRemoteDataSource.getPlanet(planetId)

        if (remotePlanet is WorkResult.Success) {
            planetsLocalDataSource.savePlanet(remotePlanet.data)
        }
    }

    /**
     * Relies on [getPlanets] to fetch data and picks the planet with the same ID.
     */
    override suspend fun getPlanet(planetId: String, forceUpdate: Boolean): WorkResult<Planet> {
        if (forceUpdate) {
            updatePlanetFromRemoteDataSource(planetId)
        }
        return planetsLocalDataSource.getPlanet(planetId)
    }

    override suspend fun savePlanet(planet: Planet) {
        coroutineScope {
            launch { planetsRemoteDataSource.savePlanet(planet) }
            launch { planetsLocalDataSource.savePlanet(planet) }
        }
    }

    override suspend fun deleteAllPlanets() {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { planetsRemoteDataSource.deleteAllPlanets() }
                launch { planetsLocalDataSource.deleteAllPlanets() }
            }
        }
    }

    override suspend fun deletePlanet(planetId: String) {
        coroutineScope {
            launch { planetsRemoteDataSource.deletePlanet(planetId) }
            launch { planetsLocalDataSource.deletePlanet(planetId) }
        }
    }
}
