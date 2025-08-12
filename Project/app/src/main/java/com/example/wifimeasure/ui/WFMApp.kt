/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.wifimeasure.ui

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wifimeasure.ui.utils.PageType
import com.example.wifimeasure.ui.utils.WFMNavigationType

@Composable
fun WFMApp(
    windowSize: WindowWidthSizeClass,
    modifier: Modifier = Modifier,
) {
    val viewModel: WFMViewModel = viewModel(factory = WFMViewModel.Factory)
    val wfmUiState = viewModel.uiState.collectAsState().value

    val navigationType: WFMNavigationType

    when (windowSize) {
        WindowWidthSizeClass.Compact -> {
            navigationType = WFMNavigationType.TOP_NAVIGATION
        }
        WindowWidthSizeClass.Medium -> {
            navigationType = WFMNavigationType.TOP_NAVIGATION
        }
        WindowWidthSizeClass.Expanded -> {
            navigationType = WFMNavigationType.SIDE_NAVIGATION
        }
        else -> {
            navigationType = WFMNavigationType.TOP_NAVIGATION
        }
    }

    WFMHomeScreen(
        viewModel = viewModel,
        navigationType = navigationType,
        wfmUiState = wfmUiState,
        modifier = modifier,
        onTabPressed = { pageType: PageType ->
            viewModel.updateCurrentPage(pageType = pageType)
        },
    )
}
