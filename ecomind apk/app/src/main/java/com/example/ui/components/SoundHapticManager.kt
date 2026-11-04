package com.example.ui.components

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.view.HapticFeedbackConstants
import android.view.View

object SoundHapticManager {
    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 60)
        } catch (_: Exception) {
            toneGenerator = null
        }
    }

    fun playSuccess(view: View?, soundEnabled: Boolean) {
        view?.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
        if (soundEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 120)
            } catch (_: Exception) {}
        }
    }

    fun playClick(view: View?, soundEnabled: Boolean) {
        view?.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
    }

    fun playError(view: View?, soundEnabled: Boolean) {
        view?.performHapticFeedback(HapticFeedbackConstants.REJECT)
        if (soundEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 160)
            } catch (_: Exception) {}
        }
    }
}
