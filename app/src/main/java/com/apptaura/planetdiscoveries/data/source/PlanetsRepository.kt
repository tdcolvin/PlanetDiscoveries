package com.apptaura.planetdiscoveries.data.source

import com.apptaura.planetdiscoveries.data.Planet
import com.apptaura.planetdiscoveries.data.WorkResult
import kotlinx.coroutines.flow.Flow

interface PlanetsRepository {
    fun getPlanetsStream(): Flow<WorkResult<List<Planet>>>

    suspend fun getPlanets(forceUpdate: Boolean = false): WorkResult<List<Planet>>

    suspend fun refreshPlanets()

    fun getPlanetStream(planetId: String): Flow<WorkResult<Planet>>

    suspend fun getPlanet(planetId: String, forceUpdate: Boolean = false): WorkResult<Planet>

    suspend fun refreshPlanet(planetId: String)

    suspend fun savePlanet(planet: Planet)

    suspend fun deleteAllPlanets()

    suspend fun deletePlanet(planetId: String)
}