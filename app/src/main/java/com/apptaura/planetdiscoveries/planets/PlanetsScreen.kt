package com.apptaura.planetdiscoveries.planets

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.apptaura.planetdiscoveries.data.Planet
import com.apptaura.planetdiscoveries.R

@Composable
fun PlanetsScreen(
    onAddPlanet: () -> Unit,
    onPlanetClick: (Planet) -> Unit,
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: PlanetsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        scaffoldState = scaffoldState,
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = onAddPlanet) {
                Icon(Icons.Filled.Add, stringResource(id = R.string.add_planet))
            }
        }
    ) { paddingValues ->
        PlanetsContent(
            loading = uiState.isLoading,
            planets = uiState.planets,
            onPlanetClick = onPlanetClick,
            getPlanetDistanceDescription = viewModel::getPlanetDistanceDescription,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun PlanetsContent(
    loading: Boolean,
    planets: List<Planet>,
    onPlanetClick: (Planet) -> Unit,
    getPlanetDistanceDescription: (Planet) -> Int,
    modifier: Modifier = Modifier
) {
    if (loading) {
        PlanetsLoadingContent()
    }
    else if (planets.isEmpty()) {
        PlanetsEmptyContent()
    }
    else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn {
                items(planets) { planet ->
                    PlanetItem(
                        planet = planet,
                        onPlanetClick = onPlanetClick,
                        getPlanetDistanceDescription = getPlanetDistanceDescription
                    )
                }
            }
        }
    }
}

@Composable
private fun PlanetItem(
    planet: Planet,
    onPlanetClick: (Planet) -> Unit,
    getPlanetDistanceDescription: (Planet) -> Int
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp,
            )
            .clickable { onPlanetClick(planet) }
    ) {
        Text(
            text = planet.name,
            style = MaterialTheme.typography.h4,
        )
        Text(text = stringResource(id = getPlanetDistanceDescription(planet)))
    }
}

@Composable
private fun PlanetsLoadingContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = Color.DarkGray)
    }
}

@Composable
private fun PlanetsEmptyContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.planet_image),
            contentDescription = stringResource(R.string.planet_image_content_description),
            modifier = Modifier.size(96.dp)
        )
        Text(stringResource(R.string.no_planets_label))
    }
}