@file:OptIn(ExperimentalPermissionsApi::class)

package com.thomaspayet.bleapp

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
import com.thomaspayet.bleapp.screens.ChartsScreen
import com.thomaspayet.bleapp.screens.HomeScreen
import com.thomaspayet.bleapp.screens.ble.BluetoothLEDeviceDetailScreen
import com.thomaspayet.bleapp.screens.ble.BluetoothLEScreen
import com.thomaspayet.bleapp.screens.ble.BluetoothLEViewModel
import com.thomaspayet.bleapp.ui.components.TopAppBar
import com.thomaspayet.bleapp.ui.theme.BLEAppTheme
import com.thomaspayet.bleapp.R

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
        val bleViewModel: BluetoothLEViewModel = viewModel()

        // Identify the current screen, handling the detail screen's dynamic route
        val currentScreen = BleAppTabRowScreens.find {
            it.route == currentDestination?.route
        } ?: if (
            currentDestination?.route?.startsWith(BluetoothLEDeviceDetail.route) == true
        ) {
            BluetoothLEDeviceDetail
        } else {
            Home
        }

        // Action logic to perform when going back
        val handleBackAction: () -> Unit = {
            if (currentScreen == BluetoothLEDeviceDetail) {
                bleViewModel.disconnect()
            } else if (currentScreen == Bluetooth) {
                bleViewModel.stopScan()
            }
            navController.popBackStack(Home.route, false)
        }

        // Intercept the system back button/gesture
        if (currentScreen != Home) {
            BackHandler(onBack = handleBackAction)
        }
        
        // Dynamically determine title and subtitle for the detail screen
        val title = if (currentScreen == BluetoothLEDeviceDetail) {
            currentBackStack?.arguments?.getString(
                BluetoothLEDeviceDetail.DEVICE_NAME_ARG
            ) ?: stringResource(R.string.unknown_ble_device)
        } else {
            currentScreen.screenTitle
        }
        val subtitle = if (currentScreen == BluetoothLEDeviceDetail) {
            currentBackStack?.arguments?.getString(
                BluetoothLEDeviceDetail.DEVICE_ADDR_ARG
            ) ?: stringResource(R.string.empty_string)
        } else {
            currentScreen.screenSubTitle
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    subtitle = {
                        if (!subtitle.isNullOrBlank()) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    },
                    leftIcon = {
                        if (currentScreen != Home) {
                            IconButton(
                                onClick = handleBackAction
                            ) {
                                Image(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.back_action)
                                )
                            }
                        }
                    }
                )
            },
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

@SuppressLint("MissingPermission")
@Composable
fun BleAppNavHost(
    modifier: Modifier = Modifier,
    bleViewModel: BluetoothLEViewModel = viewModel(),
    navController: NavHostController
) {
    val isDevConnected by bleViewModel.isConnectedToDeviceFlow.collectAsStateWithLifecycle()
    val unknownBleDevice = stringResource(R.string.unknown_ble_device)
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
                onClickElementList = { bleDevice ->
                    bleViewModel.connect(
                        ApplicationRoot.getContext(),
                        bleDevice
                    )
                },
                onConnectedDevice = { bleDevice ->
                    if (bleDevice != null) {
                        // Pass address first then name to match BluetoothLEDeviceDetail.routeWithArgs
                        navController.navigateWithTwoArguments(
                            BluetoothLEDeviceDetail.route,
                            bleDevice.address,
                            bleDevice.name ?: unknownBleDevice
                        )
                    }
                }
            )
        }
        composable(route = Charts.route) {
            ChartsScreen()
        }
        composable(
            route = BluetoothLEDeviceDetail.routeWithArgs,
            arguments = BluetoothLEDeviceDetail.arguments
        ) { navBackStackEntry ->
            val bleDeviceAddr =
                navBackStackEntry.arguments?.getString(
                    BluetoothLEDeviceDetail.DEVICE_ADDR_ARG
                )
            BluetoothLEDeviceDetailScreen(
                bleViewModel = bleViewModel,
                deviceAddr = bleDeviceAddr ?: stringResource(R.string.empty_string),
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
    this.navigate(route) {
        popUpTo(Home.route) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }

fun NavHostController.navigateWithSingleArgument(route: String, argument: String) =
    this.navigateSingleTopTo("${route}/${argument}")

fun NavHostController.navigateWithTwoArguments(route: String, arg1: String, arg2: String) =
    this.navigateSingleTopTo("${route}/${arg1}/${arg2}")

@Preview(showBackground = true)
@Composable
fun BleAppPreview() {
    BleApp()
}
