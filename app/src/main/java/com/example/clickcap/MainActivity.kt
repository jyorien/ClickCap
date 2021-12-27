package com.example.clickcap

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clickcap.constants.ScreenNames
import com.example.clickcap.screens.ReadingScreen
import com.example.clickcap.screens.ScanDevicesScreen
import com.example.clickcap.ui.theme.ClickCapTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClickCapTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = ScreenNames.ScanScreen) {
                        composable(ScreenNames.ScanScreen) { ScanDevicesScreen(navController) }
                        composable(ScreenNames.ReadingScreen) { ReadingScreen(navController) }
                    }
                }
            }
        }
    }
}
