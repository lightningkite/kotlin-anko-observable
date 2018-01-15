package com.lightningkite.kotlin.anko.observable

import android.graphics.PointF
import android.view.MotionEvent
import android.view.View
import com.lightningkite.kotlin.observable.property.ObservableProperty
import com.lightningkite.kotlin.observable.property.StandardObservableProperty

/**
 * Allows you to add in "secret codes" to views.
 */
fun View.hiddenTouchFunctionality(requirements: List<PointF.() -> Boolean>, action: () -> Unit): ObservableProperty<Int> {
    val passwordStageObs = StandardObservableProperty(0)
    val point = PointF()
    setOnTouchListener { view, motionEvent ->
        if (motionEvent.action != MotionEvent.ACTION_DOWN) return@setOnTouchListener false
        point.x = (motionEvent.x) / view.width
        point.y = (motionEvent.y) / view.height
        val requirement = requirements[passwordStageObs.value]
        if (requirement(point))
            passwordStageObs.value++
        else
            passwordStageObs.value = 0
        if (passwordStageObs.value == requirements.size) {
            action()
            passwordStageObs.value = 0
        }
        true
    }
    return passwordStageObs
}

/**
 * Allows you to add in "secret codes" to views.
 * The user must tapped
 */
fun View.hiddenTouchFunctionality(rightSide: BooleanArray, action: () -> Unit): ObservableProperty<Int>
        = hiddenTouchFunctionality(
        rightSide.map {
            if (it) {
                { point: PointF -> point.x > .5f }
            } else {
                { point: PointF -> point.x < .5f }
            }
        },
        action
)