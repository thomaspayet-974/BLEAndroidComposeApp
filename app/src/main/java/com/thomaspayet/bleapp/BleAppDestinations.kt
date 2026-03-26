package com.thomaspayet.bleapp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SsidChart
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import org.jetbrains.annotations.Contract

/**
 * Contract for information needed on every navigation destination
 */
interface BleAppDestination {
    val icon: ImageVector
    val route: String
}

/**
 * BleApp navigation Home destination
 */
object Home : BleAppDestination {
    override val icon = Icons.Filled.Home
    override val route = "home"
}

/**
 * BleApp navigation Bluetooth destination
 */
object Bluetooth : BleAppDestination {
    override val icon = Icons.Filled.Bluetooth
    override val route = "bluetooth"
}

/**
 * BleApp navigation Bluetooth Low Energy Device Detail destination
 */
object BluetoothLEDeviceDetail : BleAppDestination {
    override val icon = Icons.Filled.Bluetooth
    override val route = "ble_device_detail"
}

/**
 * BleApp navigation Charts destination
 */
object Charts : BleAppDestination {
    override val icon = Icons.Filled.SsidChart
    override val route = "charts"
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
