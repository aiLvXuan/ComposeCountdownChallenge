/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androiddevchallenge.ui.TimePickerDialog
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp(
                    viewModel,
                    btnCreateClick = {
//                        viewModel.startCountdown()
                        viewModel.showDismissTimePicker(true)
                    },
                    btnResetClick = {
                        viewModel.onClickReset()
                    }
                )
            }
        }
    }
}

// Start building your app here!
@Composable
fun MyApp(
    mainViewModel: MainViewModel = viewModel(),
    btnCreateClick: () -> Unit = {},
    btnResetClick: () -> Unit = {}
) {
//    val inputValue: String by mainViewModel.inputText.observeAsState("2333")
    val countdownText by mainViewModel.countdownText.observeAsState("00:00:00")
    val start by mainViewModel.playing.observeAsState(false)

    val progress by animateFloatAsState(
        targetValue = if (start) 0f else 1f,
        animationSpec = tween(
            durationMillis = if (start) mainViewModel.maxValue.toInt() else 100,
            easing = LinearEasing,
        )
    )



    Surface(color = MaterialTheme.colors.background) {

        Column(Modifier.padding(12.dp)) {
            Text(text = "Ready... Set... GO!")


            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(top = 20.dp)) {
                Column() {

                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = progress,
                            strokeWidth = 26.dp,
                            modifier = Modifier.size(260.dp),
                        )


                        Text(
                            text = countdownText,
                            fontSize = 48.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .fillMaxWidth(),
                        )
                    }

                    Spacer(modifier = Modifier.padding(vertical = 10.dp))
                }

                TextButton(
                    onClick = if (start) btnResetClick else btnCreateClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(74.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .clip(CircleShape)


                ) {
                    Text(
                        if (start) "Reset" else "Create",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White,
                    )
                }
            }



            Spacer(modifier = Modifier.padding(top = 20.dp))

        }
        ShowTimePicker(viewModel = mainViewModel)
    }
}

@Composable
fun ShowTimePicker(viewModel: MainViewModel) {

    val show by viewModel.showTimePicker.observeAsState(false)
    if (show)
        TimePickerDialog(
            onDismiss = {
                viewModel.showDismissTimePicker(false)
            },
            onConfirm = { hour, min, sec ->
                viewModel.onTimePicker(hour, min, sec)
                viewModel.showDismissTimePicker(false)
                viewModel.startCountdown()
            },
        )
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}
/*
@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}*/
