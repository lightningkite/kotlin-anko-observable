package com.lightningkite.kotlin.anko.observable

import android.support.design.widget.AppBarLayout
import com.lightningkite.kotlin.observable.property.ObservableProperty
import com.lightningkite.kotlin.observable.property.StandardObservableProperty
import java.lang.ref.WeakReference
import java.util.*

private val AppBarLayout_collapsedRatioObs = WeakHashMap<AppBarLayout, StandardObservableProperty<Float>>()

/**
 * An observable that reflects the percentage of how collapsed an item is.
 */

private class BarOffsetListener(val property: ObservableProperty<Float>, layout: AppBarLayout): AppBarLayout.OnOffsetChangedListener {
    val layoutRef: WeakReference<AppBarLayout> = WeakReference(layout)

    override fun onOffsetChanged(p0: AppBarLayout?, p1: Int) {
        val barLayout = layoutRef.get() ?: return

        (property as StandardObservableProperty).value = -p1.toFloat() / barLayout.totalScrollRange
    }
}
val AppBarLayout.collapsedRatio: ObservableProperty<Float>
    get() {
        val it = AppBarLayout_collapsedRatioObs[this]
        if (it != null) return it
        val new = StandardObservableProperty(1f)
        addOnOffsetChangedListener(BarOffsetListener(new, this))
        AppBarLayout_collapsedRatioObs[this] = new
        return new
    }