package com.apptaura.planetdiscoveries.planets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptaura.planetdiscoveries.R
import com.apptaura.planetdiscoveries.data.Async
import com.apptaura.planetdiscoveries.data.Planet
import com.apptaura.planetdiscoveries.data.WorkResult
import com.apptaura.planetdiscoveries.data.source.PlanetsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlanetsUiState(
    val planets: List<Planet> = emptyList(),
    val isLoading: Boolean = false,
    val error: Int? = null
)

@HiltViewModel
class PlanetsViewModel @Inject constructor(
    private val planetsRepository: PlanetsRepository,
): ViewModel() {
    private val planets = planetsRepository.getPlanetsStream()
        .map { Async.Completed(it) }
        .onStart<Async<WorkResult<List<Planet>>>> { emit (Async.Loading) }

    private val isLoading = MutableStateFlow(false)

    val uiState: StateFlow<PlanetsUiState> = combine(isLoading, planets) { isLoading, planets ->
        if (isLoading) {
            return@combine PlanetsUiState(isLoading = true)
        }
        when(planets) {
            Async.Loading -> PlanetsUiState(isLoading = true)
            is Async.Completed -> when (planets.data) {
                is WorkResult.Success -> PlanetsUiState(planets = planets.data.data)
                else -> PlanetsUiState(error = R.string.error_loading_planets)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlanetsUiState(isLoading = true)
    )

    init {
        viewModelScope.launch {
            isLoading.value = true
            planetsRepository.refreshPlanets()
            isLoading.value = false
        }
    }

    fun getPlanetDistanceDescription(planet: Planet): Int {
        return if (planet.distanceLy < 10.0F)
            R.string.reachable_description
        else if (planet.distanceLy < 80.0F)
            R.string.reachable_but_far_description
        else R.string.not_reachable_description
    }
}