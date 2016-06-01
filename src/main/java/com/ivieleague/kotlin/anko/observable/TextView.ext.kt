package com.ivieleague.kotlin.anko.observable

import android.widget.TextView
import com.ivieleague.kotlin.observable.property.MutableObservableProperty
import com.ivieleague.kotlin.observable.property.bind

/**
 * Makes this [TextView] display the value of the bond.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun TextView.bindString(bond: MutableObservableProperty<String>) {
    lifecycle.bind(bond) {
        this.text = bond.value
    }
}

/**
 * Makes this [TextView] display the value of the bond.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <T : Any> TextView.bindAny(bond: MutableObservableProperty<T>) {
    lifecycle.bind(bond) {
        this.text = bond.value.toString()
    }
}