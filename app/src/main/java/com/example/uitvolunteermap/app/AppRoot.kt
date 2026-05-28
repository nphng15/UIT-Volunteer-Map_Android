package com.example.uitvolunteermap.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.uitvolunteermap.app.navigation.AppDestination
import com.example.uitvolunteermap.app.navigation.AppNavHost
import com.example.uitvolunteermap.core.session.SessionManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SessionEntryPoint {
    fun sessionManager(): SessionManager
}

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = remember {
        EntryPointAccessors.fromApplication(context, SessionEntryPoint::class.java).sessionManager()
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            sessionManager.sessionExpiredEvent.collect {
                navController.navigate(AppDestination.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    Surface(color = MaterialTheme.colorScheme.background) {
        AppNavHost(navController = navController)
    }
}
