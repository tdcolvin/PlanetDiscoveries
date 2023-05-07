package com.apptaura.planetdiscoveries.addeditplanet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.apptaura.planetdiscoveries.Planet
import com.apptaura.planetdiscoveries.BadlyArchitectedPlanetRepository
import com.apptaura.planetdiscoveries.R
import java.lang.NumberFormatException
import java.util.Date

@Composable
fun AddEditPlanetScreen(
    planetId: String?,
    onPlanetUpdate: () -> Unit,
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
) {
    val planet = remember { planetId?.let { BadlyArchitectedPlanetRepository.getPlanet(planetId) } }
    val isLoading = false
    var planetName by remember { mutableStateOf(planet?.name ?: "") }
    var planetDistanceLy by remember { mutableStateOf(planet?.distanceLy ?: 1.0F) }
    val planetDiscovered by remember { mutableStateOf(planet?.discovered ?: Date()) }
    var isPlanetSaved by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(onClick = {
                BadlyArchitectedPlanetRepository.savePlanet(Planet(
                    id = planetId ?: (1..5).map { "ABCDEFGHIJKLMNOPQRSTUVWXYZ".random() }.toString(),
                    name = planetName,
                    distanceLy = planetDistanceLy,
                    discovered = planetDiscovered
                ))
                isPlanetSaved = true
            }) {
                Icon(Icons.Filled.Done, stringResource(R.string.save_planet_description))
            }
        }
    ) { paddingValues ->
        AddEditPlanetContent(
            loading = isLoading,
            name = planetName,
            distanceLy = planetDistanceLy,
            onNameChanged = { planetName = it },
            onDistanceLyChanged = { planetDistanceLy = it },
            modifier = Modifier.padding(paddingValues)
        )

        // Check if the planet is saved and call onPlanetUpdate event
        LaunchedEffect(isPlanetSaved) {
            if (isPlanetSaved) {
                onPlanetUpdate()
            }
        }
    }
}

@Composable
private fun AddEditPlanetContent(
    loading: Boolean,
    name: String,
    distanceLy: Float,
    onNameChanged: (String) -> Unit,
    onDistanceLyChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    if (loading) {
        LoadingContent()
    }
    else {
        Column(
            modifier
                .fillMaxWidth()
                .padding(all = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = MaterialTheme.colors.secondary.copy(alpha = ContentAlpha.high)
            )
            OutlinedTextField(
                value = name,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = onNameChanged,
                label = { Text(stringResource(R.string.plane_name_label)) },
                placeholder = {
                    Text(
                        text = stringResource(R.string.name_hint),
                        style = MaterialTheme.typography.h6
                    )
                },
                textStyle = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                colors = textFieldColors
            )
            OutlinedTextField(
                value = distanceLy.toString(),
                onValueChange = { try { it.toFloat().run { onDistanceLyChanged(this) }  } catch (_: NumberFormatException) { } },
                label = { Text(stringResource(R.string.distance_label)) },
                placeholder = { Text(stringResource(R.string.distance_description)) },
                modifier = Modifier
                    .height(350.dp)
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                colors = textFieldColors
            )
        }
    }
}

@Composable
private fun LoadingContent(
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