package ro.utcn.uid.hearway.common

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object HapticManager {
    private var vibrator: Vibrator? = null

    fun initialize(context: Context) {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    fun pulse() {
        vibrator?.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    fun distinct() {
        vibrator?.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    fun success() {
        val timings = longArrayOf(0, 150, 100, 150)
        val amplitudes = intArrayOf(0, VibrationEffect.DEFAULT_AMPLITUDE, 0, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
    }

    fun prepareToExit() {
        // Soft double pulse pattern - "get ready"
        val timings = longArrayOf(0, 200, 100, 200)
        val amplitudes = intArrayOf(0, VibrationEffect.DEFAULT_AMPLITUDE / 2, 0, VibrationEffect.DEFAULT_AMPLITUDE / 2)
        vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
    }

    fun getOffNow() {
        // Strong continuous buzz - "exit now!"
        vibrator?.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    fun warning() {
        // Three quick pulses - "warning/error"
        val timings = longArrayOf(0, 100, 50, 100, 50, 100)
        val amplitudes = intArrayOf(0, VibrationEffect.DEFAULT_AMPLITUDE, 0, VibrationEffect.DEFAULT_AMPLITUDE, 0, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
    }
}