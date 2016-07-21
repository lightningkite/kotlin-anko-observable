package com.lightningkite.kotlin.anko.observable

import android.widget.TextView
import com.lightningkite.kotlin.anko.lifecycle
import com.lightningkite.kotlin.observable.property.ObservableProperty
import com.lightningkite.kotlin.observable.property.bind

/**
 * Makes this [TextView] display the value of the bond.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun TextView.bindString(bond: ObservableProperty<String>) {
    lifecycle.bind(bond) {
        this.text = bond.value
    }
}

/**
 * Makes this [TextView] display the value of the bond.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <T : Any> TextView.bindAny(bond: ObservableProperty<T>) {
    lifecycle.bind(bond) {
        this.text = bond.value.toString()
    }
}