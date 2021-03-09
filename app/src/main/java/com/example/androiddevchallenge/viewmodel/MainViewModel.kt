package com.example.androiddevchallenge.viewmodel

import android.os.CountDownTimer
import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

/**
 * @author LvXuan
 * Created by LvXuan on 2021/3/6 09:08.
 */
class MainViewModel : ViewModel() {
    private val _countdown = MutableLiveData<Long>(0) // Unit: ms
    private val _countdownText = MediatorLiveData<String>()
    private val _inputText = MutableLiveData<String>("")
    private val _progress = MediatorLiveData<Float>()

    val countdownText: LiveData<String> = _countdownText
    val inputText: LiveData<String> = _inputText
    val playing = MutableLiveData<Boolean>(false)
    val showTimePicker = MutableLiveData(false)

    var maxValue = 0L
        private set


    private var countDownTimer: CountDownTimer? = null


    init {
        _countdownText.addSource(_countdown) {
            val hour = TimeUnit.MILLISECONDS.toHours(it)
            val min = TimeUnit.MILLISECONDS.toMinutes(it) - hour * 60
            val sec = TimeUnit.MILLISECONDS.toSeconds(it) - hour * 60 * 60 - min * 60

            _countdownText.value = StringBuilder()
                .append(String.format("%02d", hour))
                .append(":")
                .append(String.format("%02d", min))
                .append(":")
                .append(String.format("%02d", sec))
                .toString()

        }

        _progress.addSource(_countdown) {
            _progress.value = 1f * it / maxValue
        }

    }

    fun onInputTextChange(s: String) {
        _inputText.value = s
    }

    fun startCountdown() {
        playing.value = false
        countDownTimer?.cancel()

        countDownTimer = object : CountDownTimer(maxValue + 1000, 1000) {
            override fun onTick(p0: Long) {
                _countdown.value = p0
            }

            override fun onFinish() {
                playing.value = false
            }


        }
        countDownTimer!!.start()
        playing.value = true

    }

    fun showDismissTimePicker(show: Boolean) {
        showTimePicker.value = show
    }

    fun onTimePicker(hour: Int, min: Int, sec: Int) {
        maxValue = TimeUnit.HOURS.toMillis(hour.toLong()) +
                TimeUnit.MINUTES.toMillis(min.toLong()) +
                TimeUnit.SECONDS.toMillis(sec.toLong())
    }

    fun onClickReset() {
        maxValue = 0
        playing.value = false
        countDownTimer?.cancel()
        _countdown.value = 0

    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
        playing.value = false
        countDownTimer = null
    }
}