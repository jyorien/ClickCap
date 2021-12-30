package com.example.clickcap.screens

import ReadBluetoothService
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.clickcap.MainViewModel
import com.example.clickcap.composables.ClickAppBar

@Composable
fun ReadingScreen(navController: NavController, currentEye: String = "LEFT", readingIndex: Int = 1, viewModel: MainViewModel) {
    val currentReading by remember { mutableStateOf(0.0) }
    val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

        }
    }
    val readBluetoothService = ReadBluetoothService(mHandler)

    Scaffold(
        topBar = { ClickAppBar() }
    ) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(text = "Reading $readingIndex", fontSize = 27.sp)
            Text(text = "$currentEye EYE", fontSize = 20.sp)
            Text("$currentReading", fontSize = 117.sp)
            Text(text = "Current Reading")
            Spacer(modifier = Modifier.height(25.dp))

            // go next action
            Button(onClick = { /*TODO*/ }) {
                Text(text = "NEXT")
            }
            Spacer(modifier = Modifier.height(25.dp))

            // go back to starting point
            Button(onClick = { /*TODO*/ }) {
                Text(text = "Restart")
            }
        }
    }



}