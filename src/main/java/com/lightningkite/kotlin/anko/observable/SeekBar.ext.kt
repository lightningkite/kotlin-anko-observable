package com.lightningkite.kotlin.anko.observable

import android.support.annotation.FloatRange
import android.widget.SeekBar
import com.lightningkite.kotlin.anko.lifecycle
import com.lightningkite.kotlin.observable.property.MutableObservableProperty
import com.lightningkite.kotlin.observable.property.bind
import org.jetbrains.anko.onSeekBarChangeListener

/**
 * Binds this [SeekBar] two way to the observable property.
 * When the user picks a new value from the seek bar, the value of the observable property will change to the new value.
 * When the value of the observable property changes, the seek bar will be adjusted accordingly.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun SeekBar.bindInt(range: IntRange, obs: MutableObservableProperty<Int>) {
    max = range.endInclusive - range.start + 1
    lifecycle.bind(obs) {
        val newProg = it - range.start
        if (this.progress != newProg) {
            this.progress = newProg
        }
    }
    onSeekBarChangeListener {
        onProgressChanged { seekBar, value, fromUser ->
            if (fromUser) {
                val newValue = value + range.start
                if (obs.value != newValue) {
                    obs.value = newValue
                }
            }
        }
    }
}

/**
 * Binds this [SeekBar] two way to the observable property.
 * When the user picks a new value from the seek bar, the value of the observable property will change to the new value.
 * When the value of the observable property changes, the seek bar will be adjusted accordingly.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun SeekBar.bindFloat(range: FloatRange, steps: Int = 1000, obs: MutableObservableProperty<Float>) {
    max = steps
    lifecycle.bind(obs) {
        val newProg = ((it - range.from) / (range.to - range.from) * steps).toInt()
        if (this.progress != newProg) {
            this.progress = newProg
        }
    }
    onSeekBarChangeListener {
        onProgressChanged { seekBar, value, fromUser ->
            if (fromUser) {
                val newValue = ((value.toDouble() / steps) * (range.to - range.from) + range.from).toFloat()
                if (obs.value != newValue) {
                    obs.value = newValue
                }
            }
        }
    }
}

/**
 * Binds this [SeekBar] two way to the observable property.
 * When the user picks a new value from the seek bar, the value of the observable property will change to the new value.
 * When the value of the observable property changes, the seek bar will be adjusted accordingly.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun SeekBar.bindDouble(range: FloatRange, steps: Int = 1000, obs: MutableObservableProperty<Double>) {
    max = steps
    lifecycle.bind(obs) {
        val newProg = ((it - range.from) / (range.to - range.from) * steps).toInt()
        if (this.progress != newProg) {
            this.progress = newProg
        }
    }
    onSeekBarChangeListener {
        onProgressChanged { seekBar, value, fromUser ->
            if (fromUser) {
                val newValue = ((value.toDouble() / steps) * (range.to - range.from) + range.from)
                if (obs.value != newValue) {
                    obs.value = newValue
                }
            }
        }
    }
}