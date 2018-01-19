package com.lightningkite.kotlin.anko.observable

import android.view.View
import com.lightningkite.kotlin.anko.activity.ActivityAccess
import com.lightningkite.kotlin.anko.animation.AnimationSet
import com.lightningkite.kotlin.anko.animation.SwapView
import com.lightningkite.kotlin.anko.lifecycle
import com.lightningkite.kotlin.observable.property.ObservableProperty
import com.lightningkite.kotlin.observable.property.StackObservableProperty
import com.lightningkite.kotlin.observable.property.bind

fun <T> SwapView.bindRenderMap(
        access: ActivityAccess,
        observable: ObservableProperty<T>,
        getView: (T) -> (ActivityAccess) -> View,
        getAnimation: (T) -> AnimationSet = { AnimationSet.fade }
) {
    lifecycle.bind(observable) {
        swap(getView(it).invoke(access), getAnimation(it))
    }
}

fun <T> SwapView.bindRenderMapStack(
        access: ActivityAccess,
        observable: StackObservableProperty<T>,
        getView: (T) -> (ActivityAccess) -> View,
        pushAnimationSet: AnimationSet = AnimationSet.slidePush,
        neutralAnimationSet: AnimationSet = AnimationSet.fade,
        popAnimationSet: AnimationSet = AnimationSet.slidePop
) {
    var previousSize = observable.stack.size
    bindRenderMap(
            access = access,
            observable = observable,
            getView = getView,
            getAnimation = {
                val diff = observable.stack.size - previousSize
                previousSize = observable.stack.size
                if (diff > 0) pushAnimationSet
                else if (diff < 0) popAnimationSet
                else neutralAnimationSet
            }
    )
}