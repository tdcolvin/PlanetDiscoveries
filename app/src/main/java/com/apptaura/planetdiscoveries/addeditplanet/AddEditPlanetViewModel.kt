package com.apptaura.planetdiscoveries.addeditplanet

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptaura.planetdiscoveries.BadlyArchitectedPlanetRepository
import com.apptaura.planetdiscoveries.Planet
import com.apptaura.planetdiscoveries.PlanetsDestinationsArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class AddEditPlanetUiState(
    val planetName: String = "",
    val planetDistanceLy: Float = 1.0F,
    val planetDiscovered: Date = Date(),
    val isLoading: Boolean = false,
    val isPlanetSaved: Boolean = false
)

@HiltViewModel
class AddEditPlanetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val planetId: String? = savedStateHandle[PlanetsDestinationsArgs.PLANET_ID_ARG]

    private val _uiState = MutableStateFlow(AddEditPlanetUiState())
    val uiState: StateFlow<AddEditPlanetUiState> = _uiState.asStateFlow()

    init {
        if (planetId != null) {
            loadPlanet(planetId)
        }
    }

    fun savePlanet() {
        val id = planetId ?: (1..5).map { "ABCDEFGHIJKLMNOPQRSTUVWXYZ".random() }.toString()
        BadlyArchitectedPlanetRepository.savePlanet(
            Planet(
                id = id,
                name = _uiState.value.planetName,
                distanceLy = uiState.value.planetDistanceLy,
                discovered = uiState.value.planetDiscovered
            )
        )
        _uiState.update { it.copy(isPlanetSaved = true) }
    }

    private fun loadPlanet(planetId: String) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val planet = BadlyArchitectedPlanetRepository.getPlanet(planetId)
            if (planet == null) {
                _uiState.update { it.copy(isLoading = false) }
            }
            else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        planetName = planet.name,
                        planetDistanceLy = planet.distanceLy,
                        planetDiscovered = planet.discovered
                    )
                }
            }
        }
    }

    fun setPlanetName(name: String) {
        _uiState.update { it.copy(planetName = name) }
    }

    fun setPlanetDistanceLy(distanceLy: Float) {
        _uiState.update { it.copy(planetDistanceLy = distanceLy) }
    }
}