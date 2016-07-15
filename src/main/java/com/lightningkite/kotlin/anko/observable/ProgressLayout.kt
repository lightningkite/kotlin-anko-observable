package com.lightningkite.kotlin.anko.observable

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.lightningkite.kotlin.anko.animation.TransitionView
import com.lightningkite.kotlin.anko.animation.transitionView
import com.lightningkite.kotlin.observable.property.MutableObservableProperty
import com.lightningkite.kotlin.observable.property.StandardObservableProperty
import com.lightningkite.kotlin.observable.property.bind
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.progressBar
import org.jetbrains.anko.wrapContent

/**
 * Created by jivie on 7/15/16.
 */
fun ViewGroup.progressLayout(
        runningObs: MutableObservableProperty<Boolean> = StandardObservableProperty(false),
        otherViewMaker: TransitionView.(running: MutableObservableProperty<Boolean>) -> View
): TransitionView {
    return transitionView() {
        otherViewMaker(runningObs).tag("content").apply {
            (layoutParams as FrameLayout.LayoutParams).gravity = Gravity.CENTER
        }
        progressBar().lparams(wrapContent, wrapContent).tag("loading").apply {
            (layoutParams as FrameLayout.LayoutParams).gravity = Gravity.CENTER
        }

        lifecycle.bind(runningObs) { loading ->
            if (loading) animate("loading")
            else animate("content")
        }
    }
}

fun AnkoContext<*>.progressLayout(
        runningObs: MutableObservableProperty<Boolean> = StandardObservableProperty(false),
        otherViewMaker: TransitionView.(running: MutableObservableProperty<Boolean>) -> View
): TransitionView {
    return transitionView() {
        otherViewMaker(runningObs).tag("content").apply {
            (layoutParams as FrameLayout.LayoutParams).gravity = Gravity.CENTER
        }
        progressBar().lparams(wrapContent, wrapContent).tag("loading").apply {
            (layoutParams as FrameLayout.LayoutParams).gravity = Gravity.CENTER
        }

        lifecycle.bind(runningObs) { loading ->
            if (loading) animate("loading")
            else animate("content")
        }
    }
}