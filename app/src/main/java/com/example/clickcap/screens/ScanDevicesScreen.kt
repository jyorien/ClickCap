package com.example.clickcap.screens

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.clickcap.R
import com.example.clickcap.composables.ClickAppBar
import com.example.clickcap.composables.DeviceCard
import com.example.clickcap.constants.ScreenNames

@Composable
fun ScanDevicesScreen(navController: NavController) {
    val bondedDevicesList by remember { mutableStateOf(mutableListOf<BluetoothDevice>().toMutableStateList())}
    val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    var isBluetoothEnabled by rememberSaveable { mutableStateOf(false) }
    val deviceList by remember { mutableStateOf(mutableListOf<BluetoothDevice>().toMutableStateList()) }
    val context = LocalContext.current
    if (bluetoothAdapter == null) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            Text("Unfortunately, you need Bluetooth on your device for the app to work!")
        }
        return
    }

    if (!bluetoothAdapter.isEnabled) {
        val activityResultLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            when (result.resultCode) {
                -1 -> isBluetoothEnabled = true
                else -> isBluetoothEnabled = false
            }

        }
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        SideEffect {
            activityResultLauncher.launch(enableBtIntent)
        }
    } else {
        isBluetoothEnabled = true
    }

    Scaffold(
        topBar = {
            ClickAppBar()
        }
    )
    {
        if (!isBluetoothEnabled) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text("Please enable bluetooth and allow location access")
            }
            return@Scaffold
        }

        // check & ask for location permission
        val isLocationGranted =
            context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!isLocationGranted) {
            val requestLocationResultLauncher =
                rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                    if (!result) isBluetoothEnabled = false
                }
            SideEffect {
                requestLocationResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
        BluetoothBroadcastReceiver(systemAction = BluetoothDevice.ACTION_FOUND) {
            if (it == null) return@BluetoothBroadcastReceiver
            if (deviceList.contains(it)) return@BluetoothBroadcastReceiver
            deviceList.add(it)
        }

        Column {
            // button row
            Row(
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    getBluetoothBondedDevices(bluetoothAdapter).forEach {
                        if (bondedDevicesList.contains(it)) return@forEach
                        bondedDevicesList.add(it)
                    }
                    val hasStartedDiscovery = bluetoothAdapter.startDiscovery()
                    Log.d("hello", "start device discovery: $hasStartedDiscovery")
                }) {
                    Row {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_bluetooth_24),
                            contentDescription = "Scan for bluetooth devices button"
                        )
                        Text("Scan for devices")
                    }
                }

                Button(onClick = { navController.navigate(ScreenNames.ReadingScreen) }) {
                    Text(
                        text = "OK",
                        modifier = Modifier.padding(vertical = 3.5.dp, horizontal = 12.dp)
                    )
                }
            }
            // list of bonded bluetooth devices
            Text("Bonded Devices")
            Divider(Modifier.fillMaxWidth())
            LazyColumn {
                Log.d("hello", "bonded lazy column $bondedDevicesList")
                items(bondedDevicesList) { device ->
                    Column {
                        DeviceCard(device = device, modifier = Modifier.padding(horizontal = 4.dp))
                    }
                }
            }
            // list of unbonded bluetooth devices
            Text("Available Devices")
            Divider(Modifier.fillMaxWidth())
            LazyColumn {
                Log.d("hello", "lazy column $deviceList")
                items(deviceList) { device ->
                    Column {
                        DeviceCard(device = device, modifier = Modifier.padding(horizontal = 4.dp).clickable {
                            val isConnected = device.createBond()
                            Log.d("hello","isConnected: $isConnected")
                        })
                    }
                }
            }

        }
    }
}

fun getBluetoothBondedDevices(bluetoothAdapter: BluetoothAdapter?) : List<BluetoothDevice> {
    val bondedList = mutableListOf<BluetoothDevice>()
    val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
    pairedDevices?.forEach { device ->
        bondedList.add(device)
    }
    return bondedList.toList()
}

fun createBroadcastReceiver(): BroadcastReceiver {
    return object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action.toString()) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? =
                        intent?.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name
                    Log.d("hello", "device $deviceName")
                }
            }
        }

    }
}

@Composable
fun BluetoothBroadcastReceiver(
    systemAction: String,
    onSystemEvent: (device: BluetoothDevice?) -> Unit
) {
    // Grab the current context in this part of the UI tree
    val context = LocalContext.current

    // Safely use the latest onSystemEvent lambda passed to the function
    val currentOnSystemEvent by rememberUpdatedState(onSystemEvent)

    // If either context or systemAction changes, unregister and register again
    DisposableEffect(context, systemAction) {
        val intentFilter = IntentFilter(systemAction)
        val broadcast = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action.toString()) {
                    BluetoothDevice.ACTION_FOUND -> {
                        val device: BluetoothDevice? =
                            intent?.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        device?.let {
                            if (it.name != null && it.address != null)
                                onSystemEvent(it)
                        }
                    }
                }
            }
        }
        context.registerReceiver(broadcast, intentFilter)
        // When the effect leaves the Composition, remove the callback
        onDispose {
            context.unregisterReceiver(broadcast)
        }
    }
}