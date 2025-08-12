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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.wifimeasure.R
import com.example.wifimeasure.ui.utils.PageType
import com.example.wifimeasure.ui.utils.WFMNavigationType


@Composable
fun WFMHomeScreen(
    navigationType: WFMNavigationType,
    wfmUiState: WFMUiState,
    onTabPressed: (PageType) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WFMViewModel
) {
    val navigationItemContentList = listOf(
        NavigationItemContent(
            icon = Icons.Default.Search,
            text = stringResource(id = R.string.connected_networks),
            pageType = PageType.Networks
        ),
        NavigationItemContent(
            icon = Icons.Default.AccountCircle,
            text = stringResource(id = R.string.network_params),
            pageType = PageType.Network_Parameters
        ),
        NavigationItemContent(
            icon = Icons.Default.Menu,
            text = stringResource(id = R.string.history),
            pageType = PageType.History
        ),
    )

    if (navigationType == WFMNavigationType.TOP_NAVIGATION) {
        val navigationContentDescription = "topNavigation"
        PermanentNavigationDrawer(
            modifier = Modifier.testTag(navigationContentDescription),
            drawerContent = {
                PermanentDrawerSheet(Modifier.width(240.dp)) {
                    NavigationDrawerContent(
                        selectedDestination = wfmUiState.currentPage,
                        onTabPressed = onTabPressed,
                        navigationItemContentList = navigationItemContentList,
                        modifier = Modifier
                            .wrapContentWidth()
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.inverseOnSurface)
                            .padding(12.dp)
                    )
                }
            }
        ) {
        }
    }
            WFMAppContent(
                navigationType = navigationType,
                wfmUiState = wfmUiState,
                onTabPressed = onTabPressed,
                navigationItemContentList = navigationItemContentList,
                modifier = modifier,
                contentType = wfmUiState.currentPage,
                viewModel = viewModel
            )
}

@Composable
private fun WFMAppContent(
    navigationType: WFMNavigationType,
    contentType: PageType,
    wfmUiState: WFMUiState,
    onTabPressed: (PageType) -> Unit,
    navigationItemContentList: List<NavigationItemContent>,
    modifier: Modifier = Modifier,
    viewModel: WFMViewModel
) {
    Row(modifier = modifier) {
        AnimatedVisibility(visible = navigationType == WFMNavigationType.SIDE_NAVIGATION) {
            WFMNavigationRail(
                currentTab = wfmUiState.currentPage,
                onTabPressed = onTabPressed,
                navigationItemContentList = navigationItemContentList,
                modifier = Modifier
                    .testTag("navigationRailContent")
            )
        }
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            AnimatedVisibility(visible = navigationType == WFMNavigationType.TOP_NAVIGATION) {
                WFMBottomNavigationRail(
                    currentTab = wfmUiState.currentPage,
                    onTabPressed = onTabPressed,
                    navigationItemContentList = navigationItemContentList,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                )

                    Text(
                        text = when (contentType) {
                            PageType.Networks -> "Networks"
                            PageType.Network_Parameters -> "Network Parameters"
                            else -> "History"
                        },
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier
                            .padding(top = 100.dp)
                            .padding(horizontal = 16.dp)
                            .align(Alignment.CenterHorizontally)
                    )
            }
            if (navigationType == WFMNavigationType.SIDE_NAVIGATION) {
                Row (
                    modifier = Modifier.padding(2.dp)) {
                    Text(
                        text = when (contentType) {
                            PageType.Networks -> "Networks"
                            PageType.Network_Parameters -> "Network Parameters"
                            else -> "History"
                        },
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    ControlPanel(viewModel = viewModel,
                        modifier = Modifier
                            .padding(16.dp)
                    )
                }
            }
            else
                {
                    Row {
                        ControlPanel(
                            viewModel = viewModel, modifier = Modifier
                                .padding(16.dp)
                        )
                        if (contentType == PageType.History) {
                            Spacer(modifier = Modifier.weight(1f))
                            Button(
                                onClick = { viewModel.deleteDb() },
                                modifier = Modifier
                                    .padding(16.dp)
                            ) {
                                Text("Delete DB")
                            }
                        }
                    }
                }
            when (contentType) {
                PageType.Networks -> NetworksContent(viewModel = viewModel)
                PageType.Network_Parameters -> NetworkParametersContent(viewModel = viewModel)
                else -> HistoryContent(viewModel = viewModel)
            }

        }
    }
}

@Composable
private fun WFMNavigationRail(
    currentTab: PageType,
    onTabPressed: ((PageType) -> Unit),
    navigationItemContentList: List<NavigationItemContent>,
    modifier: Modifier = Modifier
) {
    NavigationRail(modifier = modifier) {
        for (navItem in navigationItemContentList) {
            NavigationRailItem(
                selected = currentTab == navItem.pageType,
                onClick = { onTabPressed(navItem.pageType) },
                icon = {
                    Icon(
                        imageVector = navItem.icon,
                        contentDescription = navItem.text
                    )
                }
            )
        }
    }
}

@Composable
private fun WFMBottomNavigationRail(
    currentTab: PageType,
    onTabPressed: ((PageType) -> Unit),
    navigationItemContentList: List<NavigationItemContent>,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        for (navItem in navigationItemContentList) {
            NavigationBarItem(
                selected = currentTab == navItem.pageType,
                onClick = { onTabPressed(navItem.pageType) },
                icon = {
                    Icon(
                        imageVector = navItem.icon,
                        contentDescription = navItem.text
                    )
                }
            )
        }
    }
}

@Composable
private fun NavigationDrawerContent(
    selectedDestination: PageType,
    onTabPressed: ((PageType) -> Unit),
    navigationItemContentList: List<NavigationItemContent>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        for (navItem in navigationItemContentList) {
            NavigationDrawerItem(
                selected = selectedDestination == navItem.pageType,
                label = {
                    Text(
                        text = navItem.text,
                        modifier = Modifier
                    )
                },
                icon = {
                    Icon(
                        imageVector = navItem.icon,
                        contentDescription = navItem.text
                    )
                },
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = Color.Transparent
                ),
                onClick = { onTabPressed(navItem.pageType) }
            )
        }
    }
}

@Composable
fun ControlPanel(viewModel: WFMViewModel, modifier: Modifier) {
    val isPaused by viewModel.isPaused.collectAsState()

    Button(
        onClick = { viewModel.toggleUpdatesPaused() },
        modifier = modifier
    ) {
        if (isPaused) Icon(imageVector =  Icons.Default.Refresh, contentDescription = null)
        else Icon(imageVector = Icons.Default.Close, contentDescription = null)
        Text(if (isPaused) "Resume Updates" else "Pause Updates")
    }
}


data class NavigationItemContent(
    val pageType: PageType,
    val icon: ImageVector,
    val text: String
)
