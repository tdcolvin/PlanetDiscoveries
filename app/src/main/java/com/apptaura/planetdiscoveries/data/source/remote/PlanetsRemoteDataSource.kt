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

package com.apptaura.planetdiscoveries.data.source.remote

import com.apptaura.planetdiscoveries.data.Planet
import com.apptaura.planetdiscoveries.data.WorkResult
import com.apptaura.planetdiscoveries.data.source.PlanetsDataSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.util.Date

/**
 * Implementation of the data source that adds a latency simulating network.
 */
object PlanetsRemoteDataSource: PlanetsDataSource {

    private const val SERVICE_LATENCY_IN_MILLIS = 2000L

    private var TASKS_SERVICE_DATA = LinkedHashMap<String, Planet>(2)

    init {
        addPlanet("Trenzalore", 123.45F, Date())
        addPlanet("Gallifrey", 0.5F, Date())
        addPlanet("Skaro", 10.2F, Date())
    }

    private val observablePlanets = MutableStateFlow(runBlocking { getPlanets() })

    override suspend fun refreshPlanets() {
        observablePlanets.value = getPlanets()
    }

    override fun getPlanetStream(planetId: String): Flow<WorkResult<Planet>> {
        return observablePlanets.map { planets ->
            when (planets) {
                is WorkResult.Error -> WorkResult.Error(planets.exception)
                is WorkResult.Success -> {
                    val planet = planets.data.firstOrNull() { it.id == planetId }
                        ?: return@map WorkResult.Error(Exception("Not found"))
                    WorkResult.Success(planet)
                }
            }
        }
    }

    override suspend fun refreshPlanet(planetId: String) {
        refreshPlanets()
    }

    override fun getPlanetsStream(): Flow<WorkResult<List<Planet>>> {
        return observablePlanets
    }

    override suspend fun getPlanets(): WorkResult<List<Planet>> {
        // Simulate network by delaying the execution.
        val planets = TASKS_SERVICE_DATA.values.toList()
        delay(SERVICE_LATENCY_IN_MILLIS)
        return WorkResult.Success(planets)
    }

    override suspend fun getPlanet(planetId: String): WorkResult<Planet> {
        // Simulate network by delaying the execution.
        delay(SERVICE_LATENCY_IN_MILLIS)
        TASKS_SERVICE_DATA[planetId]?.let {
            return WorkResult.Success(it)
        }
        return WorkResult.Error(Exception("Planet not found"))
    }

    private fun addPlanet(name: String, distanceLy: Float, discovered: Date) {
        val newPlanet = Planet(name = name, distanceLy = distanceLy, discovered = discovered)
        TASKS_SERVICE_DATA[newPlanet.id] = newPlanet
    }

    override suspend fun savePlanet(planet: Planet) {
        TASKS_SERVICE_DATA[planet.id] = planet
    }

    override suspend fun deleteAllPlanets() {
        TASKS_SERVICE_DATA.clear()
    }

    override suspend fun deletePlanet(planetId: String) {
        TASKS_SERVICE_DATA.remove(planetId)
    }
}
