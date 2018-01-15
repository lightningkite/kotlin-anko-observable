package com.lightningkite.kotlin.anko.observable.adapter

import android.support.v7.widget.RecyclerView
import com.lightningkite.kotlin.lifecycle.LifecycleConnectable
import com.lightningkite.kotlin.lifecycle.LifecycleListener
import com.lightningkite.kotlin.observable.list.ObservableList
import com.lightningkite.kotlin.observable.list.ObservableListListenerSet
import com.lightningkite.kotlin.observable.list.addListenerSet
import com.lightningkite.kotlin.observable.list.removeListenerSet
import java.util.*

/**
 * Tools for attaching animations, used internally mostly.
 * Created by joseph on 9/20/16.
 */

val previousListenerSet: WeakHashMap<RecyclerView.Adapter<*>, Pair<ObservableList<*>, ObservableListListenerSet<*>>> = WeakHashMap()

fun <ITEM, VH : RecyclerView.ViewHolder> RecyclerView.Adapter<VH>.attachAnimations(list: ObservableList<ITEM>) {
    detatchAnimations<ITEM, VH>()
    val newSet = list to ObservableListListenerSet(
            onAddListener = { item: ITEM, position: Int ->
                notifyItemInserted(position)
            },
            onRemoveListener = { item: ITEM, position: Int ->
                notifyItemRemoved(position)
            },
            onChangeListener = { oldItem: ITEM, item: ITEM, position: Int ->
                notifyItemChanged(position)
            },
            onMoveListener = { item: ITEM, oldPosition: Int, position: Int ->
                notifyItemMoved(oldPosition, position)
            },
            onReplaceListener = { list: ObservableList<ITEM> ->
                notifyDataSetChanged()
            }
    )
    previousListenerSet[this] = newSet
    list.addListenerSet(newSet.second)
}

@Suppress("UNCHECKED_CASt")
fun <ITEM, VH : RecyclerView.ViewHolder> RecyclerView.Adapter<VH>.detatchAnimations() {
    val prev = previousListenerSet[this]
    if (prev != null) {
        (prev.first as ObservableList<ITEM>).removeListenerSet(prev.second as ObservableListListenerSet<ITEM>)
        previousListenerSet.remove(this)
    }
}

fun <ITEM, VH : RecyclerView.ViewHolder> RecyclerView.Adapter<VH>.attachAnimations(lifecycle: LifecycleConnectable, list: ObservableList<ITEM>) {
    lifecycle.connect(object : LifecycleListener {
        override fun onStart() {
            attachAnimations<ITEM, VH>(list)
        }

        override fun onStop() {
            detatchAnimations<ITEM, VH>()
        }
    })
}