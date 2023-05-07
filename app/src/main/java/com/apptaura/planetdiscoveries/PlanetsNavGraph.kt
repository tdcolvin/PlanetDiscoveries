/*
 * Copyright (C) 2022 The Android Open Source Project
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

package com.apptaura.planetdiscoveries

import android.app.Activity
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.apptaura.planetdiscoveries.PlanetsDestinationsArgs.PLANET_ID_ARG
import com.apptaura.planetdiscoveries.PlanetsDestinationsArgs.TITLE_ARG
import com.apptaura.planetdiscoveries.PlanetsDestinationsArgs.USER_MESSAGE_ARG
import com.apptaura.planetdiscoveries.addeditplanet.AddEditPlanetScreen
import com.apptaura.planetdiscoveries.planets.PlanetsScreen
import kotlinx.coroutines.CoroutineScope

@Composable
fun PlanetsNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    startDestination: String = PlanetsDestinations.PLANETS_ROUTE,
    navActions: PlanetsNavigationActions = remember(navController) {
        PlanetsNavigationActions(navController)
    }
) {
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: startDestination

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(
            PlanetsDestinations.PLANETS_ROUTE,
            arguments = listOf(
                navArgument(USER_MESSAGE_ARG) { type = NavType.IntType; defaultValue = 0 }
            )
        ) {
            PlanetsScreen(
                    onAddPlanet = { navActions.navigateToAddEditPlanet(R.string.add_planet, null) },
                    onPlanetClick = { planet -> navActions.navigateToAddEditPlanet(R.string.edit_planet, planet.id) }
                )
        }
        composable(
            PlanetsDestinations.ADD_EDIT_PLANET_ROUTE,
            arguments = listOf(
                navArgument(TITLE_ARG) { type = NavType.IntType },
                navArgument(PLANET_ID_ARG) { type = NavType.StringType; nullable = true },
            )
        ) { entry ->
            val planetId = entry.arguments?.getString(PLANET_ID_ARG)
            AddEditPlanetScreen(
                planetId = planetId,
                onPlanetUpdate = {
                    navActions.navigateToPlanets(
                        if (planetId == null) ADD_EDIT_RESULT_OK else EDIT_RESULT_OK
                    )
                },
            )
        }
    }
}

// Keys for navigation
const val ADD_EDIT_RESULT_OK = Activity.RESULT_FIRST_USER + 1
const val DELETE_RESULT_OK = Activity.RESULT_FIRST_USER + 2
const val EDIT_RESULT_OK = Activity.RESULT_FIRST_USER + 3
