package com.lightningkite.kotlin.anko.observable

import android.widget.CompoundButton
import com.lightningkite.kotlin.anko.lifecycle
import com.lightningkite.kotlin.observable.list.ObservableList
import com.lightningkite.kotlin.observable.list.bind
import com.lightningkite.kotlin.observable.property.MutableObservableProperty
import com.lightningkite.kotlin.observable.property.ObservableProperty
import com.lightningkite.kotlin.observable.property.bind
import org.jetbrains.anko.onCheckedChange

/**
 * Binds this [Switch] two way to the bond.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun CompoundButton.bindBoolean(bond: MutableObservableProperty<Boolean>, crossinline onChange: (Boolean) -> Unit) {
    this.onCheckedChange {
        buttonView: CompoundButton?, isChecked: Boolean ->
        Unit
        if (isChecked != bond.value) {
            bond.value = (isChecked)
            onChange(isChecked)
        }
    }
    lifecycle.bind(bond) {
        if (isChecked != bond.value) {
            isChecked = bond.value
        }
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun CompoundButton.bindArray(bond: MutableObservableProperty<Array<Boolean>>, index: Int) {
    this.onCheckedChange {
        buttonView: CompoundButton?, isChecked: Boolean ->
        if (isChecked != bond.value[index]) {
            bond.value[index] = isChecked
            bond.update()
        }
    }
    lifecycle.bind(bond) {
        val value = bond.value[index]
        if (isChecked != value) {
            isChecked = value
        }
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun CompoundButton.bindBoolean(bond: MutableObservableProperty<Boolean>) {
    this.onCheckedChange {
        buttonView: CompoundButton?, isChecked: Boolean ->
        if (isChecked != bond.value) {
            bond.value = (isChecked)
        }
    }
    lifecycle.bind(bond) {
        val value = bond.value
        if (isChecked != value) {
            isChecked = value
        }
    }
}

@Suppress("NOTHING_TO_INLINE")
@JvmName("bindBooleanOptional")
inline fun CompoundButton.bindBoolean(bond: MutableObservableProperty<Boolean?>, default: Boolean = false) {
    this.onCheckedChange {
        buttonView: CompoundButton?, isChecked: Boolean ->
        if (isChecked != bond.value) {
            bond.value = (isChecked)
        }
    }
    lifecycle.bind(bond) {
        val value = bond.value
        if (isChecked != value) {
            isChecked = value ?: default
        }
    }
}

inline fun CompoundButton.bindList(bond: MutableObservableProperty<MutableList<Boolean>>, index: Int) {
    this.onCheckedChange {
        buttonView: CompoundButton?, isChecked: Boolean ->
        if (isChecked != bond.value[index]) {
            bond.value[index] = isChecked
            bond.update()
        }
    }
    lifecycle.bind(bond) {
        val value = bond.value[index]
        if (isChecked != value) {
            isChecked = value
        }
    }
}

/**
 * Binds this [RadioButton] two way to the bond.
 * When the user picks this radio button, [bond] is set to [value]
 * When the value of the bond changes, it will be shown as checked if they are equal.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <T> CompoundButton.bindValue(bond: MutableObservableProperty<T>, value: T) {
    lifecycle.bind(bond) {
        isChecked = value == bond.value
    }
    onCheckedChange { compoundButton, checked ->
        if (checked && bond.value != value) {
            bond.value = (value)
        }
    }
}

/**
 * Binds this [RadioButton] two way to the bond.
 * When the user picks this radio button, [bond] is set to [value]
 * When the value of the bond changes, it will be shown as checked if they are equal.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <T, A : T> CompoundButton.bindValue(bond: MutableObservableProperty<T>, otherBond: MutableObservableProperty<A>) {
    lifecycle.bind(bond, otherBond) { currentValue, myValue ->
        isChecked = currentValue == myValue
    }
    onCheckedChange { compoundButton, checked ->
        if (checked && bond.value != otherBond.value) {
            bond.value = (otherBond.value)
        }
    }
}


fun <T> CompoundButton.bindList(list: ObservableList<T>, item: T) {
    this.onCheckedChange {
        buttonView: CompoundButton?, isChecked: Boolean ->

        val index = list.indexOf(item)
        if (isChecked != (index != -1)) {
            if (index != -1) {
                list.removeAt(index)
            } else {
                list.add(item)
            }
        }
    }
    lifecycle.bind(list) {
        val contained = list.contains(item)
        if (isChecked != contained) {
            isChecked = contained
        }
    }
}

inline fun <T> CompoundButton.bindList(list: ObservableList<T>, item: T, crossinline matches: (T, T) -> Boolean) {
    this.onCheckedChange {
        buttonView: CompoundButton?, isChecked: Boolean ->

        val index = list.indexOfFirst { matches(it, item) }
        if (isChecked != (index != -1)) {
            if (index != -1) {
                list.removeAt(index)
            } else {
                list.add(item)
            }
        }
    }
    lifecycle.bind(list) {
        val index = list.indexOfFirst { matches(it, item) }
        if (isChecked != (index != -1)) {
            isChecked = (index != -1)
        }
    }
}

fun <T> CompoundButton.bindList(list: ObservableList<T>, itemObs: ObservableProperty<T>) {
    this.onCheckedChange {
        buttonView: CompoundButton?, isChecked: Boolean ->

        val index = list.indexOf(itemObs.value)
        if (isChecked != (index != -1)) {
            if (index != -1) {
                list.removeAt(index)
            } else {
                list.add(itemObs.value)
            }
        }
    }
    lifecycle.bind(list.onUpdate, itemObs) { list, item ->
        val contained = list.contains(item)
        if (isChecked != contained) {
            isChecked = contained
        }
    }
}

inline fun <T> CompoundButton.bindList(list: ObservableList<T>, itemObs: ObservableProperty<T>, crossinline matches: (T, T) -> Boolean) {
    this.onCheckedChange {
        buttonView: CompoundButton?, isChecked: Boolean ->

        val index = list.indexOfFirst { matches(it, itemObs.value) }
        if (isChecked != (index != -1)) {
            if (index != -1) {
                list.removeAt(index)
            } else {
                list.add(itemObs.value)
            }
        }
    }
    lifecycle.bind(list.onUpdate, itemObs) { list, item ->
        val contained = list.contains(item)
        if (isChecked != contained) {
            isChecked = contained
        }
    }
}
