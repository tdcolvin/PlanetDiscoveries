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

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.apptaura.planetdiscoveries.data.Planet
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the planets table.
 */
@Dao
interface PlanetsDao {

    /**
     * Observes list of planets.
     *
     * @return all planets.
     */
    @Query("SELECT * FROM Planets")
    fun observePlanets(): Flow<List<Planet>>

    /**
     * Observes a single planet.
     *
     * @param planetId the planet id.
     * @return the planet with planetId.
     */
    @Query("SELECT * FROM Planets WHERE entryid = :planetId")
    fun observePlanetById(planetId: String): Flow<Planet>

    /**
     * Select all planets from the planets table.
     *
     * @return all planets.
     */
    @Query("SELECT * FROM Planets")
    suspend fun getPlanets(): List<Planet>

    /**
     * Select a planet by id.
     *
     * @param planetId the planet id.
     * @return the planet with planetId.
     */
    @Query("SELECT * FROM Planets WHERE entryid = :planetId")
    suspend fun getPlanetById(planetId: String): Planet?

    /**
     * Insert a planet in the database. If the planet already exists, replace it.
     *
     * @param planet the planet to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlanet(planet: Planet)

    /**
     * Update a planet.
     *
     * @param planet planet to be updated
     * @return the number of planets updated. This should always be 1.
     */
    @Update
    suspend fun updatePlanet(planet: Planet): Int

    /**
     * Delete a planet by id.
     *
     * @return the number of planets deleted. This should always be 1.
     */
    @Query("DELETE FROM Planets WHERE entryid = :planetId")
    suspend fun deletePlanetById(planetId: String): Int

    /**
     * Delete all planets.
     */
    @Query("DELETE FROM Planets")
    suspend fun deletePlanets()
}
