package com.lightningkite.kotlin.anko.observable.validation

import android.support.annotation.StringRes
import android.support.design.widget.TextInputLayout
import android.widget.TextView
import com.lightningkite.kotlin.anko.lifecycle
import com.lightningkite.kotlin.lambda.invokeAll
import com.lightningkite.kotlin.lifecycle.listen
import com.lightningkite.kotlin.observable.property.ObservableProperty
import org.jetbrains.anko.childrenSequence
import java.util.*

/**
 *
 * Created by joseph on 10/4/17.
 */

class Validation<T>(
        val observableProperty: ObservableProperty<T>,
        val getError: Validation<T>.(T) -> StringFetcher?
) {
    val onCheck = ArrayList<(StringFetcher?) -> Unit>()

    fun invokeSilent(): StringFetcher? = getError.invoke(this, observableProperty.value)
    operator fun invoke(): StringFetcher? {
        val result = getError.invoke(this, observableProperty.value)
        onCheck.invokeAll(result)
        return result
    }

    fun isValid(): Boolean = invoke() == null

    fun string(data: String) = StringFetcherDirect(data)
    fun resource(@StringRes resource: Int) = StringFetcherResource(resource)
}


private val ObservableProperty_Validation = WeakHashMap<ObservableProperty<*>, Validation<*>>()
@Suppress("UNCHECKED_CAST")
var <T> ObservableProperty<T>.validation: Validation<T>?
    get() = ObservableProperty_Validation[this] as? Validation<T>
    set(value) {
        ObservableProperty_Validation[this] = value
    }


fun <T, P : ObservableProperty<T>> P.withValidation(getError: Validation<T>.(T) -> StringFetcher?): P {
    validation = Validation(this, getError)
    return this
}

fun TextView.bindError(observableProperty: ObservableProperty<*>) {
    lifecycle.listen(observableProperty.validation!!.onCheck) { errorFetcher ->
        error = errorFetcher?.invoke(resources)
        if (errorFetcher != null) {
            requestFocus()
        }
    }
}

fun TextInputLayout.bindError(observableProperty: ObservableProperty<*>) {
    lifecycle.listen(observableProperty.validation!!.onCheck) { errorFetcher ->
        error = errorFetcher?.invoke(resources)
        if (errorFetcher != null) {
            childrenSequence().find { it is TextView }?.requestFocus()
        }
    }
}