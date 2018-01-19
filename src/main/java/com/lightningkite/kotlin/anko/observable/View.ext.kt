package com.lightningkite.kotlin.anko.observable

import android.view.View
import com.lightningkite.kotlin.anko.animation.TypedValueAnimator
import com.lightningkite.kotlin.anko.lifecycle
import com.lightningkite.kotlin.anko.measureDesiredSize
import com.lightningkite.kotlin.anko.requestLayoutSafe
import com.lightningkite.kotlin.observable.property.ObservableProperty
import com.lightningkite.kotlin.observable.property.listen
import org.jetbrains.anko.wrapContent


fun <T : View> T.expanding(expandedObs: ObservableProperty<Boolean>): T {
    var lastSize = 0
    lifecycle.listen(expandedObs) {
        val final = if (it) {
            if (layoutParams.height == wrapContent) return@listen
            measureDesiredSize().y
        } else {
            if (layoutParams.height == 0) return@listen
            0
        }
        TypedValueAnimator.IntAnimator(lastSize, final)
                .onUpdate {
                    layoutParams.height = it
                    requestLayoutSafe()
                    lastSize = it
                    if (final != 0 && it == final) layoutParams.height = wrapContent
                }
                .setDuration(200)
                .start()
    }

    val final = if (expandedObs.value) {
        wrapContent
    } else {
        0
    }
    layoutParams.height = final
    lastSize = final
    requestLayoutSafe()

    return this
}