package com.lightningkite.kotlin.anko.observable

import android.view.View
import com.lightningkite.kotlin.anko.ViewGenerator
import com.lightningkite.kotlin.anko.activity.ActivityAccess

object CentralRenderMappings : (Any) -> (ActivityAccess) -> View {


    private val inner = HashMap<Class<*>, ViewGenerator<*>>()

    override fun invoke(value: Any) = get(value) ?: throw IllegalArgumentException("Type ${value.javaClass.name} has no render function associated with it.")
    operator fun get(value: Any): ((ActivityAccess) -> View)? = inner[value.javaClass]?.let {
        { access ->
            @Suppress("UNCHECKED_CAST")
            (it as ViewGenerator<Any>).invoke(access, value)
        }
    }

    operator fun <T> set(type: Class<T>, generator: ViewGenerator<T>) {
        inner[type] = generator
    }
}