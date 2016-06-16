package com.lightningkite.kotlin.anko.observable

import android.text.InputType
import android.widget.EditText
import com.lightningkite.kotlin.observable.property.MutableObservableProperty
import com.lightningkite.kotlin.observable.property.bind
import org.jetbrains.anko.opaque
import org.jetbrains.anko.textChangedListener
import org.jetbrains.anko.textColor
import java.text.NumberFormat
import java.text.ParseException

/**
 * Binds this [EditText] two way to the bond.
 * When the user edits this, the value of the bond will change.
 * When the value of the bond changes, the text here will be updated.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun EditText.bindString(bond: MutableObservableProperty<String>) {
    setText(bond.value)
    textChangedListener {
        onTextChanged { charSequence, start, before, count ->
            if (bond.value != charSequence) {
                bond.value = (charSequence.toString())
            }
        }
    }
    lifecycle.bind(bond) {
        if (bond.value != text.toString()) {
            this.setText(bond.value)
        }
    }
}

/**
 * Binds this [EditText] two way to the bond.
 * When the user edits this, the value of the bond will change.
 * When the value of the bond changes, the text here will be updated.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun EditText.bindNullableString(bond: MutableObservableProperty<String?>) {
    setText(bond.value)
    textChangedListener {
        onTextChanged { charSequence, start, before, count ->
            if (bond.value != charSequence) {
                bond.value = (charSequence.toString())
            }
        }
    }
    lifecycle.bind(bond) {
        if (bond.value != text.toString()) {
            this.setText(bond.value)
        }
    }
}

/**
 * Binds this [EditText] two way to the bond.
 * When the user edits this, the value of the bond will change.
 * When the value of the bond changes, the integer here will be updated.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun EditText.bindInt(bond: MutableObservableProperty<Int>, format: NumberFormat = NumberFormat.getNumberInstance()) {
    inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    val originalTextColor = this.textColors.defaultColor
    var value: Int? = null
    textChangedListener {
        onTextChanged { charSequence, start, before, count ->

            value = null

            try {
                value = format.parse(charSequence.toString()).toInt()
            } catch(e: ParseException) {
                //do nothing.
            }

            try {
                value = charSequence.toString().toInt()
            } catch(e: NumberFormatException) {
                //do nothing.
            }

            if (value == null) {
                textColor = 0xFF0000.opaque
            } else {
                textColor = originalTextColor
                if (bond.value != value) {
                    bond.value = (value!!)
                }
            }
        }
    }
    lifecycle.bind(bond) {
        if (bond.value != value) {
            this.setText(format.format(bond.value))
        }
    }
}

/**
 * Binds this [EditText] two way to the bond.
 * When the user edits this, the value of the bond will change.
 * When the value of the bond changes, the integer here will be updated.
 */
inline fun EditText.bindNullableInt(bond: MutableObservableProperty<Int?>, format: NumberFormat = NumberFormat.getNumberInstance()) {
    inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    val originalTextColor = this.textColors.defaultColor
    var value: Int? = null
    textChangedListener {
        onTextChanged { charSequence, start, before, count ->

            value = null

            try {
                value = format.parse(charSequence.toString()).toInt()
            } catch(e: ParseException) {
                //do nothing.
            }

            try {
                value = charSequence.toString().toInt()
            } catch(e: NumberFormatException) {
                //do nothing.
            }

            textColor = originalTextColor
            if (bond.value != value) {
                bond.value = (value!!)
            }
        }
    }
    lifecycle.bind(bond) {
        if (bond.value != value) {
            if (bond.value == null) this.setText("")
            else this.setText(format.format(bond.value))
        }
    }
}

/**
 * Binds this [EditText] two way to the bond.
 * When the user edits this, the value of the bond will change.
 * When the value of the bond changes, the number here will be updated.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun EditText.bindNullableFloat(bond: MutableObservableProperty<Float?>, format: NumberFormat = NumberFormat.getNumberInstance()) {
    inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    val originalTextColor = this.textColors.defaultColor
    var value: Float? = null
    textChangedListener {
        onTextChanged { charSequence, start, before, count ->

            value = null

            try {
                value = format.parse(charSequence.toString()).toFloat()
            } catch(e: ParseException) {
                //do nothing.
            }

            try {
                value = charSequence.toString().toFloat()
            } catch(e: NumberFormatException) {
                //do nothing.
            }

            textColor = originalTextColor
            if (bond.value != value) {
                bond.value = (value)
            }
        }
    }
    lifecycle.bind(bond) {
        if (bond.value != value) {
            if (bond.value == null) this.setText("")
            else this.setText(format.format(bond.value))
        }
    }
}

/**
 * Binds this [EditText] two way to the bond.
 * When the user edits this, the value of the bond will change.
 * When the value of the bond changes, the number here will be updated.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun EditText.bindNullableDouble(bond: MutableObservableProperty<Double?>, format: NumberFormat = NumberFormat.getNumberInstance()) {
    inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    val originalTextColor = this.textColors.defaultColor
    var value: Double? = null
    textChangedListener {
        onTextChanged { charSequence, start, before, count ->

            value = null

            try {
                value = format.parse(charSequence.toString()).toDouble()
            } catch(e: ParseException) {
                //do nothing.
            }

            try {
                value = charSequence.toString().toDouble()
            } catch(e: NumberFormatException) {
                //do nothing.
            }

            textColor = originalTextColor
            if (bond.value != value) {
                bond.value = (value)
            }
        }
    }
    lifecycle.bind(bond) {
        if (bond.value != value) {
            if (bond.value == null) this.setText("")
            else this.setText(format.format(bond.value!!))
        }
    }
}

/**
 * Binds this [EditText] two way to the bond.
 * When the user edits this, the value of the bond will change.
 * When the value of the bond changes, the number here will be updated.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun EditText.bindFloat(bond: MutableObservableProperty<Float>, format: NumberFormat = NumberFormat.getNumberInstance()) {
    inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    val originalTextColor = this.textColors.defaultColor
    var value = Float.NaN
    textChangedListener {
        onTextChanged { charSequence, start, before, count ->

            value = Float.NaN

            try {
                value = format.parse(charSequence.toString()).toFloat()
            } catch(e: ParseException) {
                //do nothing.
            }

            try {
                value = charSequence.toString().toFloat()
            } catch(e: NumberFormatException) {
                //do nothing.
            }

            if (value.isNaN()) {
                textColor = 0xFF0000.opaque
            } else {
                textColor = originalTextColor
                if (bond.value != value) {
                    bond.value = (value)
                }
            }
        }
    }
    lifecycle.bind(bond) {
        if (bond.value != value) {
            this.setText(format.format(bond.value))
        }
    }
}

/**
 * Binds this [EditText] two way to the bond.
 * When the user edits this, the value of the bond will change.
 * When the value of the bond changes, the number here will be updated.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun EditText.bindDouble(bond: MutableObservableProperty<Double>, format: NumberFormat = NumberFormat.getNumberInstance()) {
    inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    val originalTextColor = this.textColors.defaultColor
    var value = Double.NaN
    textChangedListener {
        onTextChanged { charSequence, start, before, count ->

            value = Double.NaN

            try {
                value = format.parse(charSequence.toString()).toDouble()
            } catch(e: ParseException) {
                //do nothing.
            }

            try {
                value = charSequence.toString().toDouble()
            } catch(e: NumberFormatException) {
                //do nothing.
            }

            if (value.isNaN()) {
                textColor = 0xFF0000.opaque
            } else {
                textColor = originalTextColor
                if (bond.value != value) {
                    bond.value = (value)
                }
            }
        }
    }
    lifecycle.bind(bond) {
        if (bond.value != value) {
            this.setText(format.format(bond.value))
        }
    }
}