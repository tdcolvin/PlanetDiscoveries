package com.apptaura.planetdiscoveries.planets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptaura.planetdiscoveries.BadlyArchitectedPlanetRepository
import com.apptaura.planetdiscoveries.Planet
import com.apptaura.planetdiscoveries.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlanetsUiState(
    val planets: List<Planet> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class PlanetsViewModel @Inject constructor(): ViewModel() {
    private val _uiState = MutableStateFlow(PlanetsUiState())
    val uiState: StateFlow<PlanetsUiState> = _uiState.asStateFlow()

    init {
        loadPlanets()
    }

    private fun loadPlanets() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        val planets = BadlyArchitectedPlanetRepository.getPlanets()
        _uiState.update { it.copy(planets = planets, isLoading = false) }
    }

    fun getPlanetDistanceDescription(planet: Planet): Int {
        return if (planet.distanceLy < 10.0F)
            R.string.reachable_description
        else if (planet.distanceLy < 80.0F)
            R.string.reachable_but_far_description
        else R.string.not_reachable_description
    }
}