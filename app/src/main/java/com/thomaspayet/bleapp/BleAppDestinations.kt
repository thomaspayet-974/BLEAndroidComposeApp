package com.thomaspayet.bleapp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SsidChart
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.navArgument

/**
 * Contract for information needed on every navigation destination
 */
interface BleAppDestination {
    val icon: ImageVector
    val route: String
    val screenTitle: String
    val screenSubTitle: String?
}

/**
 * BleApp navigation Home destination
 */
object Home : BleAppDestination {
    override val icon = Icons.Filled.Home
    override val route = "home"
    override val screenTitle = "Home"
    override val screenSubTitle = null
}

/**
 * BleApp navigation Bluetooth destination
 */
object Bluetooth : BleAppDestination {
    override val icon = Icons.Filled.Bluetooth
    override val route = "bluetooth"
    override val screenTitle = "Scan Bluetooth Device"
    override val screenSubTitle = null
}

/**
 * BleApp navigation Bluetooth Low Energy Device Detail destination
 */
object BluetoothLEDeviceDetail : BleAppDestination {
    override val icon = Icons.Filled.Bluetooth
    override val route = "ble_device_detail"
    
    // Using keys for arguments
    const val DEVICE_NAME_ARG = "device_name"
    const val DEVICE_ADDR_ARG = "device_addr"
    
    override val screenTitle = DEVICE_NAME_ARG
    override val screenSubTitle = DEVICE_ADDR_ARG
    
    val routeWithArgs = "${route}/{$DEVICE_ADDR_ARG}/{$DEVICE_NAME_ARG}"
    
    val arguments = listOf(
        navArgument(DEVICE_NAME_ARG) { type = NavType.StringType },
        navArgument(DEVICE_ADDR_ARG) { type = NavType.StringType }
    )
}

/**
 * BleApp navigation Charts destination
 */
object Charts : BleAppDestination {
    override val icon = Icons.Filled.SsidChart
    override val route = "charts"
    override val screenTitle = "Charts"
    override val screenSubTitle = null
}

/* Exemple with deepLink and arguments of navigation
object SingleAccount : BleAppDestination {
    // Added for simplicity, this icon will not in fact be used, as SingleAccount isn't
    // part of the RallyTabRow selection
    override val icon = Icons.Filled.Money
    override val route = "single_account"
    val routeWithArgs = "${route}/{${accountTypeArg}}"
    const val accountTypeArg = "account_type"
    val arguments = listOf(
        navArgument(accountTypeArg) { type = NavType.StringType }
    )
    val deepLinks = listOf(
        navDeepLink { uriPattern = "rally://$route/{$accountTypeArg}" }
    )
}
*/

/**
 * List of BleApp navigation destinations for navigation bar
 */
val BleAppTabRowScreens = listOf(Home, Bluetooth, Charts)
