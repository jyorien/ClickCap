package com.example.clickcap.composables

import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun DeviceCard(modifier: Modifier = Modifier, device: BluetoothDevice) {
    Column(modifier) {
        Text(text = device.name, fontSize = 24.sp)
        Text(text = device.address, fontSize = 18.sp)
        Divider(modifier.fillMaxWidth())
    }
}