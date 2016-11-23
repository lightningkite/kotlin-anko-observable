package com.lightningkite.kotlin.anko.observable

import android.content.Context
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.lightningkite.kotlin.anko.*
import com.lightningkite.kotlin.observable.property.MutableObservableProperty
import com.lightningkite.kotlin.observable.property.StandardObservableProperty
import com.lightningkite.kotlin.observable.property.bind
import com.lightningkite.kotlin.text.isEmail
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.design.textInputLayout
import java.text.NumberFormat
import java.util.*

/**
 * Created by jivie on 3/15/16.
 */
@Deprecated("Instead of using a FormLayout, use standard layouts instead and then use a collection of Validations to validate.")
class FormLayout(ctx: Context) : _LinearLayout(ctx) {

    init {
        orientation = VERTICAL
    }

    var defaultMinimumHeight = dip(50)
    var defaultHorizontalPadding = dip(16)
    var defaultVerticalPadding = dip(8)

    var makeField: ViewGroup.(hint: Int, innerViewMaker: ViewGroup.() -> View) -> View = { hint, builder ->
        verticalLayout {
            gravity = Gravity.CENTER_VERTICAL
            textView(hint).lparams(matchParent, wrapContent)
            builder().lparams(matchParent, wrapContent)
        }
    }

    var makeTextField: ViewGroup.(hint: Int, editTextSetup: EditText.() -> Unit) -> View = { hint, editTextSetup ->
        textInputLayout {
            hintResource = hint
            textInputEditText {
                editTextSetup()
            }
        }
    }

    var showFailure: View.(failure: String) -> Unit = {
        snackbar(it)
    }

    inline fun defaultSpecialFieldStyle(crossinline styleHintText: TextView.() -> Unit) {
        makeField = { hint, builder ->
            verticalLayout {
                gravity = Gravity.CENTER_VERTICAL
                textView(hint) { styleHintText() }.lparams(matchParent, wrapContent)
                builder().lparams(matchParent, wrapContent)
            }
        }
    }

    inline fun defaultStyle(crossinline styleTextField: EditText.() -> Unit) {
        makeTextField = { hint, editTextSetup ->
            editText() {
                hintResource = hint
                styleTextField()
                editTextSetup()
            }
        }
    }

    inline fun materialStyle(crossinline editTextStyle: TextInputEditText.() -> Unit, crossinline inputLayoutStyle: TextInputLayout.() -> Unit) {
        makeTextField = { hint, editTextSetup ->
            textInputLayout {
                hintResource = hint
                val et = textInputEditText {
                    editTextStyle()
                    editTextSetup()
                }
                inputLayoutStyle()
            }
        }
    }

    var buttonStyle: Button.() -> Unit = {}

    val isPassingObs = StandardObservableProperty(true)
    val errors = HashMap<View, CharSequence?>()

    inline fun View.formPadding() {
        leftPadding = defaultHorizontalPadding
        rightPadding = defaultHorizontalPadding
        topPadding = defaultVerticalPadding
        bottomPadding = defaultVerticalPadding
    }

    inline fun MarginLayoutParams.formMargins() {
        leftMargin = defaultHorizontalPadding
        rightMargin = defaultHorizontalPadding
        topMargin = defaultVerticalPadding
        bottomMargin = defaultVerticalPadding
    }

    override fun generateDefaultLayoutParams(): LayoutParams? {
        return super.generateDefaultLayoutParams().apply {
            width = matchParent
            formMargins()
        }
    }

    var View.formError: CharSequence?
        get() = errors[this]
        set(value) {
            errors[this] = value
            if (this is TextInputLayout) {
                this.error = value
            }
            val parent = this.parent
            if (parent is TextInputLayout) {
                parent.error = value
            }
            isPassingObs.value = (!errors.values.any { it != null })
        }
    var View.formErrorResource: Int?
        get() = throw IllegalAccessException()
        set(resource) {
            val value = if (resource != null) resources.getString(resource) else null
            errors[this] = value
            if (this is TextInputLayout) {
                this.error = value
            }
            val parent = this.parent
            if (parent is TextInputLayout) {
                parent.error = value
            }
            isPassingObs.value = (!errors.values.any { it != null })
        }

    inline fun ViewGroup.fieldDouble(obs: MutableObservableProperty<Double>, format: NumberFormat, hint: Int, crossinline setup: EditText.() -> Unit): View {
        return makeTextField(hint) {
            bindDouble(obs, format)
            setup()
        }
    }

    inline fun ViewGroup.fieldFloat(obs: MutableObservableProperty<Float>, format: NumberFormat, hint: Int, crossinline setup: EditText.() -> Unit): View {
        return makeTextField(hint) {
            bindFloat(obs, format)
            setup()
        }
    }

    inline fun ViewGroup.fieldInt(obs: MutableObservableProperty<Int>, format: NumberFormat, hint: Int, crossinline setup: EditText.() -> Unit): View {
        return makeTextField(hint) {
            bindInt(obs, format)
            setup()
        }
    }

    inline fun ViewGroup.fieldNullableInt(obs: MutableObservableProperty<Int?>, format: NumberFormat, hint: Int, crossinline setup: EditText.() -> Unit): View {
        return makeTextField(hint) {
            bindNullableInt(obs, format)
            setup()
        }
    }

    inline fun ViewGroup.fieldNullableFloat(obs: MutableObservableProperty<Float?>, format: NumberFormat, hint: Int, crossinline setup: EditText.() -> Unit): View {
        return makeTextField(hint) {
            bindNullableFloat(obs, format)
            setup()
        }
    }

    inline fun ViewGroup.fieldNullableDouble(obs: MutableObservableProperty<Double?>, format: NumberFormat, hint: Int, crossinline setup: EditText.() -> Unit): View {
        return makeTextField(hint) {
            bindNullableDouble(obs, format)
            setup()
        }
    }

    inline fun ViewGroup.fieldString(obs: MutableObservableProperty<String>, hint: Int, type: Int, crossinline setup: EditText.() -> Unit): View {
        return makeTextField(hint) {
            bindString(obs)
            inputType = type
            setup()
        }
    }

    inline fun ViewGroup.fieldString(
            obs: MutableObservableProperty<String>,
            hint: Int,
            type: Int,
            validations: List<Pair<Int, (String) -> Boolean>> = listOf(),
            crossinline setup: EditText.() -> Unit
    ): View {
        return makeTextField(hint) {
            bindString(obs)
            inputType = type
            setup()
            lifecycle.bind(obs) {
                for ((res, validation) in validations) {
                    if (validation(it)) {
                        formErrorResource = res
                        return@bind
                    }
                }
                formErrorResource = null
            }
        }
    }

    inline fun ViewGroup.fieldString(
            obs: MutableObservableProperty<String>,
            hint: Int,
            type: Int,
            validations: List<Pair<Int, (String) -> Boolean>> = listOf()
    ): View {
        return makeTextField(hint) {
            bindString(obs)
            inputType = type
            lifecycle.bind(obs) {
                for ((res, validation) in validations) {
                    if (validation(it)) {
                        formErrorResource = res
                        return@bind
                    }
                }
                formErrorResource = null
            }
        }
    }

    inline fun ViewGroup.fieldString(
            obs: MutableObservableProperty<String>,
            hint: Int,
            type: Int
    ): View {
        return makeTextField(hint) {
            bindString(obs)
            inputType = type
        }
    }

    inline fun ViewGroup.fieldNonEmptyString(obs: MutableObservableProperty<String>, hint: Int, type: Int, blankError: Int) = fieldString(
            obs,
            hint,
            type,
            listOf(blankError to { it: String -> it.isBlank() })
    ) {}

    inline fun ViewGroup.email(obs: MutableObservableProperty<String>, hint: Int, blankError: Int, notEmailError: Int) = fieldString(
            obs,
            hint,
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
            {
                lifecycle.bind(obs) {
                    formErrorResource =
                            if (it.isEmpty()) blankError
                            else if (!it.isEmail()) notEmailError
                            else null
                }
            }
    )

    inline fun ViewGroup.password(obs: MutableObservableProperty<String>, hint: Int, blankError: Int) = fieldString(
            obs,
            hint,
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD,
            {
                lifecycle.bind(obs) {
                    formErrorResource =
                            if (it.isEmpty()) blankError
                            else null
                }
            }
    )

    inline fun ViewGroup.password(obs: MutableObservableProperty<String>, hint: Int, blankError: Int, minLength: Int, tooShortError: Int) = fieldString(
            obs,
            hint,
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD,
            {
                lifecycle.bind(obs) {
                    formErrorResource =
                            if (it.isEmpty()) blankError
                            else if (it.length < minLength) tooShortError
                            else null
                }
            }
    )

    inline fun ViewGroup.confirmPassword(password: MutableObservableProperty<String>, confirm: MutableObservableProperty<String>, hint: Int, blankError: Int, notMatchingError: Int) = fieldString(
            confirm,
            hint,
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD,
            {
                lifecycle.bind(password, confirm) { pass, conf ->
                    formErrorResource =
                            if (conf.isEmpty()) blankError
                            else if (pass != conf) notMatchingError
                            else null
                }
            }
    )

    inline fun ViewGroup.specialField(label: Int, noinline maker: ViewGroup.() -> View): View {
        return makeField(label, maker)
    }

    inline fun ViewGroup.switchLayout(observable: MutableObservableProperty<Boolean>, label: Int): LinearLayout = linearLayout {
        formPadding()
        minimumHeight = defaultMinimumHeight
        gravity = Gravity.CENTER

        textView(label).lparams(0, wrapContent, 1f) {
            leftMargin = defaultHorizontalPadding
            rightMargin = defaultHorizontalPadding / 2
        }

        val s = switch() {
            bindBoolean(observable)
        }.lparamsMod {
            leftMargin = defaultHorizontalPadding / 2
            rightMargin = defaultHorizontalPadding
        }

        backgroundResource = selectableItemBackgroundResource
        onClick {
            s.toggle()
        }

    }.lparams(matchParent, wrapContent)

    inline fun ViewGroup.submit(text: Int, showFailReason: Boolean = true, setup: ProgressButton.() -> Unit) = formProgressButton(text) {
        lifecycle.bind(isPassingObs) {
            button.isEnabled = it
        }
        if (showFailReason) {
            onDisabledClick {
                if (!isPassingObs.value) {
                    val failure = errors.values.firstOrNull { it != null }?.toString() ?: "UNKNOWN ERROR"
                    showFailure(failure)
                }
            }
        }
        setup()
    }

    inline fun ViewGroup.formProgressButton(text: Int, setup: ProgressButton.() -> Unit) = progressButton(text) {
        button.lparams(matchParent, matchParent) { formMargins() }
        button.minimumHeight = defaultMinimumHeight
        button.buttonStyle()
        setup()
    }.lparams(matchParent, wrapContent)

    inline fun ViewGroup.formProgressButton(setup: ProgressButton.() -> Unit) = progressButton() {
        button.lparams(matchParent, matchParent) { formMargins() }
        button.minimumHeight = defaultMinimumHeight
        button.buttonStyle()
        setup()
    }.lparams(matchParent, wrapContent)

    inline fun ViewGroup.formButton(setup: Button.() -> Unit): Button = button() {
        minimumHeight = defaultMinimumHeight
        buttonStyle()
        setup()
    }

    inline fun ViewGroup.formButton(text: Int, setup: Button.() -> Unit): Button = button(text) {
        minimumHeight = defaultMinimumHeight
        buttonStyle()
        setup()
    }

    inline fun layoutCancelSave(crossinline cancelSetup: Button.() -> Unit, crossinline saveSetup: Button.() -> Unit) {
        linearLayout {
            formButton() {
                cancelSetup()
            }.lparams(0, wrapContent, 1f) {
                formMargins()
            }

            formButton() {
                lifecycle.bind(isPassingObs) {
                    isEnabled = it
                }
                saveSetup()
            }.lparams(0, wrapContent, 1f) {
                formMargins()
            }
        }.lparams(matchParent, wrapContent)
    }

    inline fun layoutCancelSaveProgress(showFailReason: Boolean = true, crossinline cancelSetup: Button.() -> Unit, crossinline saveSetup: ProgressButton.() -> Unit) {
        linearLayout {
            formButton() {
                cancelSetup()
            }.lparams(0, wrapContent, 1f)

            space().lparams(dip(16), dip(16))

            formProgressButton() {
                button.lparamsMod { margin = 0 }
                lifecycle.bind(isPassingObs) {
                    button.isEnabled = it
                }
                if (showFailReason) {
                    onDisabledClick {
                        if (!isPassingObs.value) {
                            val failure = errors.values.firstOrNull { it != null }?.toString() ?: "UNKNOWN ERROR"
                            showFailure(failure)
                        }
                    }
                }
                saveSetup()
            }.lparams(0, wrapContent, 1f)
        }
    }
}

@Deprecated("Instead of using a FormLayout, use standard layouts instead and then use a collection of Validations to validate.")
inline fun ViewManager.formLayout(init: FormLayout.() -> Unit) = ankoView({ FormLayout(it) }, init)