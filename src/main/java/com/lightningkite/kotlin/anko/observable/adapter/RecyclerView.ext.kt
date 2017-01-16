package com.lightningkite.kotlin.anko.observable.adapter

import android.support.v7.widget.RecyclerView
import com.lightningkite.kotlin.anko.adapter.SingleRecyclerViewAdapter
import com.lightningkite.kotlin.anko.adapter.TransitionRecyclerViewAdapter
import com.lightningkite.kotlin.anko.adapter.singleAdapter
import com.lightningkite.kotlin.anko.adapter.transitionAdapter
import com.lightningkite.kotlin.anko.lifecycle
import com.lightningkite.kotlin.observable.list.ObservableList
import com.lightningkite.kotlin.observable.list.bind


inline fun <T> RecyclerView.listAdapter(
        list: ObservableList<T>,
        noinline makeEmptyView: SingleRecyclerViewAdapter.SRVAContext.() -> Unit,
        noinline makeView: ListRecyclerViewAdapter.SRVAContext<T>.(ListRecyclerViewAdapter.ItemObservable<T>) -> Unit
): TransitionRecyclerViewAdapter {
    val adapter = transitionAdapter {
        val listAdapter = listAdapter(list, makeView)
        val emptyAdapter = singleAdapter(makeEmptyView)
        lifecycle.bind(list) { it: ObservableList<T> ->
            if (it.isEmpty()) {
                animate(emptyAdapter)
            } else {
                animate(listAdapter)
            }
        }
    }
    return adapter
}