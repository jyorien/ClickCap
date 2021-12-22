package com.example.clickcap.screens

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.clickcap.R
import com.example.clickcap.composables.ClickAppBar
import com.example.clickcap.constants.ScreenNames
@Composable
fun ScanDevicesScreen(navController: NavController) {
    val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    var isBluetoothEnabled by rememberSaveable { mutableStateOf(false) }
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
            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceAround) {
                Text("Please enable bluetooth and allow location access")
            }
            return@Scaffold
        }

        // check & ask for location permission
        val isLocationGranted = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!isLocationGranted) {
            val requestLocationResultLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (!result) isBluetoothEnabled = false
            }
            SideEffect {
                requestLocationResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
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
                    getBluetoothBondedDevices(bluetoothAdapter)
                    val hasStartedDiscovery = bluetoothAdapter.startDiscovery()
                    Log.d("hello","start device discovery: $hasStartedDiscovery")
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
            // list of bluetooth devices
            LazyColumn(content = {})

        }
    }
}

fun getBluetoothBondedDevices(bluetoothAdapter: BluetoothAdapter?) {
    val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
    pairedDevices?.forEach { device ->
        val deviceName = device.name
        val deviceHardwareAddress = device.address // MAC address
    }
}