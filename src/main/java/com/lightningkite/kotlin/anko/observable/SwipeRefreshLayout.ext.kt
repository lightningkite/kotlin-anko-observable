package com.lightningkite.kotlin.anko.observable

import android.support.v4.widget.SwipeRefreshLayout
import com.lightningkite.kotlin.anko.lifecycle
import com.lightningkite.kotlin.observable.property.MutableObservableProperty
import com.lightningkite.kotlin.observable.property.StandardObservableProperty
import com.lightningkite.kotlin.observable.property.bind

/**
 * Additional functionality for SwipeRefreshLayouts and observables.
 * Created by josep on 5/30/2017.
 */

inline fun SwipeRefreshLayout.onRefresh(
        progressObservable: MutableObservableProperty<Boolean> = StandardObservableProperty(false),
        crossinline action: (progressObservable: MutableObservableProperty<Boolean>) -> Unit
) {
    lifecycle.bind(progressObservable){
        this.isRefreshing = it
    }
    setOnRefreshListener {
        action.invoke(progressObservable)
    }
}

inline fun SwipeRefreshLayout.onRefreshAndNow(
        progressObservable: MutableObservableProperty<Boolean> = StandardObservableProperty(false),
        crossinline action: (progressObservable: MutableObservableProperty<Boolean>) -> Unit
) {
    onRefresh(progressObservable, action)
    action.invoke(progressObservable)
}