@file:OptIn(ExperimentalPermissionsApi::class)

package com.thomaspayet.bleapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.thomaspayet.bleapp.data.ble.BluetoothLEManager
import com.thomaspayet.bleapp.screens.ChartsScreen
import com.thomaspayet.bleapp.screens.HomeScreen
import com.thomaspayet.bleapp.screens.ble.BluetoothLEDeviceDetailScreen
import com.thomaspayet.bleapp.screens.ble.BluetoothLEScreen
import com.thomaspayet.bleapp.screens.ble.BluetoothLEViewModel
import com.thomaspayet.bleapp.ui.theme.BLEAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val toCheckPermissions = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                listOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                listOf(
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    android.Manifest.permission.BLUETOOTH_SCAN
                )
            }
            val permissionState = rememberMultiplePermissionsState(toCheckPermissions)
            if (!permissionState.allPermissionsGranted) {
                LaunchedEffect(true) {
                    permissionState.launchMultiplePermissionRequest()
                }
            }
            BleApp()
        }
    }
}

@Composable
fun BleApp() {
    BLEAppTheme {
        val navController = rememberNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination

        val currentScreen = BleAppTabRowScreens.find {
            it.route == currentDestination?.route
        } ?: Home

        Scaffold(
            topBar = {},
            bottomBar = {
                com.thomaspayet.bleapp.ui.components.TabRow(
                    allScreens = BleAppTabRowScreens,
                    onTabSelected = { newScreen ->
                        navController.navigateSingleTopTo(newScreen.route)
                    },
                    currentScreen = currentScreen
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            BleAppNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun BleAppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val bleViewModel: BluetoothLEViewModel = viewModel()
    val isDevConnected by bleViewModel.isConnectedToDeviceFlow.collectAsStateWithLifecycle()
    NavHost(
        navController = navController,
        startDestination = Home.route,
        modifier = modifier
    ) {
        composable(route = Home.route) {
            HomeScreen()
        }
        composable(route = Bluetooth.route) {
            BluetoothLEScreen(
                bleViewModel = bleViewModel,
                onClickElementList = {
                    navController.navigateSingleTopTo(BluetoothLEDeviceDetail.route)
                }
            )
        }
        composable(route = Charts.route) {
            ChartsScreen()
        }
        composable(route = BluetoothLEDeviceDetail.route) {
            BluetoothLEDeviceDetailScreen(
                device = BluetoothLEManager.currentDevice,
                isDeviceConnected = isDevConnected,
                onClickDisconnect = {
                    bleViewModel.disconnect()
                    navController.navigateSingleTopTo(Bluetooth.route)
                }
            )
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) { launchSingleTop = true }

fun NavHostController.navigateWithSingleArgument(route: String, argument: String) =
    this.navigateSingleTopTo("${route}/$argument")

@Preview(showBackground = true)
@Composable
fun BleAppPreview() {
    BleApp()
}