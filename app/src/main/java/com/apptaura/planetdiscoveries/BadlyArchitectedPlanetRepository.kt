package com.apptaura.planetdiscoveries

import com.apptaura.planetdiscoveries.data.Planet
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


