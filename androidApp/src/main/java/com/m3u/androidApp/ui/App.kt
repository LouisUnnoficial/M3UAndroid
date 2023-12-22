package com.m3u.androidApp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.m3u.androidApp.navigation.M3UNavHost
import com.m3u.core.architecture.pref.Pref
import com.m3u.material.model.LocalNavController
import com.m3u.ui.EmptyHelper
import com.m3u.ui.Helper
import com.m3u.ui.rememberActionHolder

@Composable
fun App(
    pref: Pref,
    appState: AppState = rememberAppState(),
    viewModel: AppViewModel = hiltViewModel(),
    helper: Helper = EmptyHelper,
) {
    val snacker by viewModel.snacker.collectAsStateWithLifecycle()
    val actions by viewModel.actions.collectAsStateWithLifecycle()
    val fob by viewModel.fob.collectAsStateWithLifecycle()

    val navDestination = appState.navDestination
    val rootDestination = appState.rootDestination

    val isBackPressedVisible = AppDefaults.isBackPressedVisible(navDestination)
    val isSystemBarScrollable = AppDefaults.isSystemBarScrollable(navDestination)

    val title: String by AppDefaults.title(
        rootDestination = rootDestination,
        defState = viewModel.title.collectAsStateWithLifecycle()
    )

    val actionHolder = rememberActionHolder(actions)

    AppScaffold(
        title = title,
        snacker = snacker.value,
        actionHolder = actionHolder,
        rootDestination = rootDestination,
        fob = fob,
        isSystemBarScrollable = isSystemBarScrollable,
        helper = helper,
        pref = pref,
        onBackPressed = appState::onBackClick.takeIf { isBackPressedVisible },
        navigate = appState::navigate,
        modifier = Modifier.fillMaxSize(),
    ) { contentPadding ->
        CompositionLocalProvider(
            LocalNavController provides appState.navController,
        ) {
            M3UNavHost(
                pagerState = appState.pagerState,
                navigate = appState::navigate,
                contentPadding = contentPadding,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
