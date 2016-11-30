package com.lightningkite.kotlin.anko.observable

import android.text.InputType
import android.widget.EditText
import com.lightningkite.kotlin.anko.NumericalString
import com.lightningkite.kotlin.anko.lifecycle
import com.lightningkite.kotlin.anko.textChanger
import com.lightningkite.kotlin.observable.property.MutableObservableProperty
import com.lightningkite.kotlin.observable.property.bind
import com.lightningkite.kotlin.text.toDoubleMaybe
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
    var value: Int? = null
    textChangedListener {
        onTextChanged { charSequence, start, before, count ->

            value = null
            if (!charSequence.isNullOrBlank()) {
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

            }

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
    var value: Float? = null
    textChangedListener {
        onTextChanged { charSequence, start, before, count ->

            value = null
            if (!charSequence.isNullOrBlank()) {

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
            }

            println("PRE value $value obs ${bond.value}")
            if (bond.value != value) {
                bond.value = (value)
            }
            println("PST value $value obs ${bond.value}")
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

            if (!charSequence.isNullOrBlank()) {
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

/**
 * Binds this [EditText] two way to the bond.
 * When the user edits this, the value of the bond will change.
 * When the value of the bond changes, the number here will be updated.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun EditText.bindDoubleAutoComma(bond: MutableObservableProperty<Double>, format: NumberFormat = NumberFormat.getNumberInstance()) {
    inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL

    var iSet = false
    textChanger {
        val resultString = format.format(it.after.filter {
            it.isDigit()
                    || it == NumericalString.decimalChar
                    || it == NumericalString.negativeChar
        }.toDoubleMaybe(0.0))
        val insertionPoint = NumericalString.transformPosition(it.after, resultString, it.insertionPoint + it.replacement.length).coerceIn(0, resultString.length)

//        println("write")
        iSet = true
        bond.value = format.parse(resultString).toDouble()

        resultString to insertionPoint..insertionPoint
    }
    lifecycle.bind(bond) {
        if (iSet) {
//            println("ignored")
            iSet = false
        } else {
//            println("read")
            this.setText(format.format(bond.value))
        }
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun EditText.bindNullableDoubleAutoComma(bond: MutableObservableProperty<Double?>, format: NumberFormat = NumberFormat.getNumberInstance()) {
    inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL

    var iSet = false
    textChanger {
        val maybeNumber = it.after.filter {
            it.isDigit()
                    || it == NumericalString.decimalChar
                    || it == NumericalString.negativeChar
        }.toDoubleMaybe()
        if (maybeNumber != null) {
            val resultString = format.format(maybeNumber)
            val insertionPoint = NumericalString.transformPosition(it.after, resultString, it.insertionPoint + it.replacement.length).coerceIn(0, resultString.length)

//        println("write")
            iSet = true
            bond.value = format.parse(resultString).toDouble()

            resultString to insertionPoint..insertionPoint
        } else {
            iSet = true
            bond.value = null
            "" to 0..0
        }
    }
    lifecycle.bind(bond) {
        if (iSet) {
//            println("ignored")
            iSet = false
        } else {
//            println("read")
            this.setText(format.format(bond.value))
        }
    }
}