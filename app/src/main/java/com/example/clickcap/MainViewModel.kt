package com.example.clickcap

import android.bluetooth.BluetoothSocket
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    var bluetoothSocket: BluetoothSocket? = null
}