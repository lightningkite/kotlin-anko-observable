package com.ivieleague.kotlin.anko.observable

import android.widget.CompoundButton
import android.widget.Switch
import com.ivieleague.kotlin.observable.property.MutableObservableProperty
import com.ivieleague.kotlin.observable.property.bind
import org.jetbrains.anko.onCheckedChange

/**
 * Binds this [Switch] two way to the bond.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun Switch.bind(bond: MutableObservableProperty<Boolean>) {
    this.onCheckedChange {
        buttonView: CompoundButton?, isChecked: Boolean ->
        Unit
        if (isChecked != bond.value) {
            bond.value = (isChecked);
        }
    }
    lifecycle.bind(bond) {
        if (isChecked != bond.value) {
            isChecked = bond.value;
        }
    }
}