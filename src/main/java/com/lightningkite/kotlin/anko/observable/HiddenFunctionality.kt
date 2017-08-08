package com.lightningkite.kotlin.anko.observable

import android.graphics.PointF
import android.view.MotionEvent
import android.view.View
import com.lightningkite.kotlin.observable.property.ObservableProperty
import com.lightningkite.kotlin.observable.property.StandardObservableProperty

/**
 * Allows you to add in "secret codes" to views.
 * Created by joseph on 3/16/17.
 */


inline fun View.hiddenTouchFunctionality(requirements: List<PointF.() -> Boolean>, crossinline action: () -> Unit): ObservableProperty<Int> {
    val passwordStageObs = StandardObservableProperty(0)
    val point = PointF()
    setOnTouchListener { view, motionEvent ->
        if (motionEvent.action != MotionEvent.ACTION_DOWN) return@setOnTouchListener false
        point.x = (motionEvent.x - view.left) / view.width
        point.y = (motionEvent.y - view.top) / view.height
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

inline fun View.hiddenTouchFunctionality(rightSide: BooleanArray, crossinline action: () -> Unit): ObservableProperty<Int>
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