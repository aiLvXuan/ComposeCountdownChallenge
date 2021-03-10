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
package com.example.androiddevchallenge.ui

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.defaultDecayAnimationSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * @author LvXuan
 * Created by LvXuan on 2021/3/8 10:55.
 */

@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, min: Int, sec: Int) -> Unit
) {

    val offset = 30.dp.value.toInt()
    val hourState = rememberLazyListState(getHalfIndex(100), offset)
    val minState = rememberLazyListState(getHalfIndex(60), offset)
    val secState = rememberLazyListState(getHalfIndex(60), offset)

    AlertDialog(
        onDismissRequest = onDismiss,
        buttons = {
            Column {
                Spacer(modifier = Modifier.padding(top = 8.dp))
                TimeView(hourState, minState, secState)
                Spacer(modifier = Modifier.padding(top = 8.dp))

                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .width(1.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .background(Color.White)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f, true)
                            .fillMaxHeight()

                    ) {
                        Text(text = "Cancel")
                    }
                    Divider(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                    )

                    TextButton(
                        onClick = {
                            val hour = (hourState.firstVisibleItemIndex + 2) % 100
                            val min = (minState.firstVisibleItemIndex + 2) % 60
                            val sec = (secState.firstVisibleItemIndex + 2) % 60
                            if (hour != 0 || min != 0 || sec != 0) {
                                onConfirm(hour, min, sec)
                            }

                            /*              Log.i(
                                              "TAG", """""TimePickerDialog:
                                              |${(hourState.firstVisibleItemIndex + 2) % 100}   ${hourState.firstVisibleItemScrollOffset}
                                              |---  ${(minState.firstVisibleItemIndex + 2) % 60}   ${minState.firstVisibleItemScrollOffset}
                                              |--  ${(secState.firstVisibleItemIndex + 2) % 60}    ${secState.firstVisibleItemScrollOffset}""".trimMargin()
                                          )*/
                        },
                        modifier = Modifier
                            .weight(1f, true)
                            .fillMaxHeight()

                    ) {
                        Text(text = "Start")
                    }
                }
            }
        }
    )
}

@Composable
private fun TimeView(
    hourState: LazyListState,
    minState: LazyListState,
    secState: LazyListState,
) {
    val liseSize = intArrayOf(100, 60, 60)
    val text = arrayOf("Hr.", "Min.", "Sec.")

    val itemHeight = 60.dp

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .background(Color.White)
            .clip(RoundedCornerShape(8.dp))
    ) {
        Box(
            modifier = Modifier
                .height(itemHeight)
                .fillMaxWidth()
                .background(Color.Gray)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {

            liseSize.forEachIndexed { index, size ->

//                val half: Int = Int.MAX_VALUE / size / 2 * size - 2

                val state = when (index) {
                    0 -> hourState
                    1 -> minState
                    2 -> secState
                    else -> throw IndexOutOfBoundsException("Undefined")
                }

//                scrollToItem(state, half, (itemHeight.value / 2).toInt())

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LazyColumn(
                        content = {
                            items(Int.MAX_VALUE) {
                                Text(
                                    text = String.format("%02d", it % size),
                                    fontSize = 30.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.height(itemHeight)
                                )
                            }
                        },
                        state = state,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f),
                        flingBehavior = flingBehavior(state)
                    )
                    Text(text[index], textAlign = TextAlign.Center)
                }
            }
        }
    }
}

private fun scrollToItem(state: LazyListState, index: Int, offset: Int = 0, anim: Boolean = false) {
    if (index < 0) return
    CoroutineScope(Dispatchers.Main).launch {
        if (anim) state.animateScrollToItem(index, offset)
        else state.scrollToItem(index, offset)
    }
}

/**
 * item 定位
 */
@Composable
private fun flingBehavior(state: LazyListState): PickerFlingBehavior {
    val flingSpec = defaultDecayAnimationSpec()
    var index = 0
    var offset = 0
    return PickerFlingBehavior(flingSpec) { isRunning ->

        if (!isRunning) {
            index = state.firstVisibleItemIndex
            offset = state.firstVisibleItemScrollOffset

            if (offset > 45.dp.value.toInt()) {
                index += 1
            } else if (offset < 15.dp.value.toInt()) {
                index -= 1
            }

            scrollToItem(state, index, 30.dp.value.toInt(), true)
        }
    }
}

private class PickerFlingBehavior(
    private val flingDecay: DecayAnimationSpec<Float>,
    private var b: (isRunning: Boolean) -> Unit
) : FlingBehavior {
    override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
        var velocityLeft = initialVelocity
        var lastValue = 0f
        AnimationState(
            initialValue = 0f,
            initialVelocity = initialVelocity,
        ).animateDecay(flingDecay) {
            val delta = value - lastValue
            val left = scrollBy(delta)
            lastValue = value
            velocityLeft = this.velocity
            // avoid rounding errors and stop if anything is unconsumed
            if (abs(left) > 0.5f) this.cancelAnimation()

            b(isRunning)
        }

        return velocityLeft
    }
}

private fun getHalfIndex(size: Int) = Int.MAX_VALUE / size / 2 * size - 2

@Preview
@Composable
fun Preview() {
    TimePickerDialog(
        onDismiss = { },
        onConfirm = { hour, min, sec ->
        }
    )
}
