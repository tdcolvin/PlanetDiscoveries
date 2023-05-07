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

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.apptaura.planetdiscoveries.PlanetsDestinationsArgs.PLANET_ID_ARG
import com.apptaura.planetdiscoveries.PlanetsDestinationsArgs.TITLE_ARG
import com.apptaura.planetdiscoveries.PlanetsDestinationsArgs.USER_MESSAGE_ARG
import com.apptaura.planetdiscoveries.PlanetsScreens.ADD_EDIT_PLANET_SCREEN
import com.apptaura.planetdiscoveries.PlanetsScreens.PLANETS_SCREEN

/**
 * Screens used in [PlanetsDestinations]
 */
private object PlanetsScreens {
    const val PLANETS_SCREEN = "planets"
    const val ADD_EDIT_PLANET_SCREEN = "addEditPlanet"
}

/**
 * Arguments used in [PlanetsDestinations] routes
 */
object PlanetsDestinationsArgs {
    const val USER_MESSAGE_ARG = "userMessage"
    const val PLANET_ID_ARG = "planetId"
    const val TITLE_ARG = "title"
}

/**
 * Destinations used in the [MainActivity]
 */
object PlanetsDestinations {
    const val PLANETS_ROUTE = "$PLANETS_SCREEN?$USER_MESSAGE_ARG={$USER_MESSAGE_ARG}"
    const val ADD_EDIT_PLANET_ROUTE = "$ADD_EDIT_PLANET_SCREEN/{$TITLE_ARG}?$PLANET_ID_ARG={$PLANET_ID_ARG}"
}

/**
 * Models the navigation actions in the app.
 */
class PlanetsNavigationActions(private val navController: NavHostController) {

    fun navigateToPlanets(userMessage: Int = 0) {
        val navigatesFromDrawer = userMessage == 0
        navController.navigate(
            PLANETS_SCREEN.let {
                if (userMessage != 0) "$it?$USER_MESSAGE_ARG=$userMessage" else it
            }
        ) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = !navigatesFromDrawer
                saveState = navigatesFromDrawer
            }
            launchSingleTop = true
            restoreState = navigatesFromDrawer
        }
    }

    fun navigateToAddEditPlanet(title: Int, planetId: String?) {
        navController.navigate(
            "$ADD_EDIT_PLANET_SCREEN/$title".let {
                if (planetId != null) "$it?$PLANET_ID_ARG=$planetId" else it
            }
        )
    }
}
