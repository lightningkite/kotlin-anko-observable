package com.lightningkite.kotlin.anko.observable

import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import com.lightningkite.kotlin.observable.property.MutableObservableProperty
import com.lightningkite.kotlin.observable.property.bind
import org.jetbrains.anko.onItemSelectedListener

/**
 * Binds this [Spinner] two way to the bond.
 * When the user picks a new value from the spinner, the value of the bond will change to the index of the new value.
 * When the value of the bond changes, the item will shown will be updated.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun Spinner.bindIndex(bond: MutableObservableProperty<Int>) {
    lifecycle.bind(bond) {
        if (selectedItemPosition != it) {
            setSelection(it)
        }
    }
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (position != bond.value) {
                bond.value = (position)
            }
        }

    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T> Spinner.bindList(bond: MutableObservableProperty<T>, list: List<T>) {
    this.onItemSelectedListener {
        onItemSelected { adapterView, view, index, id ->
            bond.value = (list[index])
        }
    }
    lifecycle.bind(bond) {
        if (it == null) return@bind
        val index = list.indexOf(it)
        if (index == -1) return@bind
        setSelection(index)
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T, E> Spinner.bindList(bond: MutableObservableProperty<T>, list: List<E>, crossinline conversion: (E) -> T) {
    this.onItemSelectedListener {
        onItemSelected { adapterView, view, index, id ->
            bond.value = (conversion(list[index]))
        }
    }
    lifecycle.bind(bond) {
        if (it == null) return@bind
        val index = list.indexOfFirst { item -> it == conversion(item) }
        if (index == -1) return@bind
        setSelection(index)
    }
}

