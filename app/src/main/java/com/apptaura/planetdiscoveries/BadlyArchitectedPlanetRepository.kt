package com.apptaura.planetdiscoveries

import java.util.Date

object BadlyArchitectedPlanetRepository {
    private val planets = hashMapOf(
        "AAA" to Planet("AAA", "Trenzalore", 123.45F, Date()),
        "BBB" to Planet("BBB", "Gallifrey", 0.5F, Date()),
        "CCC" to Planet("CCC", "Skaro", 10.2F, Date()),
    )

    fun savePlanet(planet: Planet) {
        planets[planet.id] = planet
    }

    fun getPlanet(id: String): Planet? {
        return planets[id]
    }

    fun getPlanets(): List<Planet> {
        return planets.values.toList().sortedBy { it.name }
    }
}

data class Planet (
    val id: String,
    val name: String,
    val distanceLy: Float,
    val discovered: Date,
)
