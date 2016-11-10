package com.lightningkite.kotlin.anko.observable

import android.support.design.widget.AppBarLayout
import com.lightningkite.kotlin.observable.property.ObservableProperty
import com.lightningkite.kotlin.observable.property.StandardObservableProperty
import java.util.*

private val AppBarLayout_collapsedRatioObs = WeakHashMap<AppBarLayout, StandardObservableProperty<Float>>()

/**
 * An observable that reflects the percentage of how collapsed an item is.
 */
val AppBarLayout.collapsedRatio: ObservableProperty<Float>
    get() {
        val it = AppBarLayout_collapsedRatioObs[this]
        if (it != null) return it
        val new = StandardObservableProperty(1f)
        addOnOffsetChangedListener { appBarLayout, pix ->
            new.value = -pix.toFloat() / this.totalScrollRange
        }
        AppBarLayout_collapsedRatioObs[this] = new
        return new
    }