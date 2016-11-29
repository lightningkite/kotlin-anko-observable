package com.lightningkite.kotlin.anko.observable.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.lightningkite.kotlin.anko.lifecycle
import com.lightningkite.kotlin.observable.list.ObservableList
import com.lightningkite.kotlin.observable.property.ObservablePropertyBase
import org.jetbrains.anko.AnkoContextImpl
import java.util.*

/**
 * An adapter for RecyclerViews intended to be used in all cases.
 *
 * Created by jivie on 5/4/16.
 */
open class ListEmptyableRecyclerViewAdapter<T>(
        val context: Context,
        initialList: List<T>,
        val makeEmptyView: SRVAContext<T>.() -> Unit,
        val makeView: SRVAContext<T>.(ItemObservable<T>) -> Unit
) : RecyclerView.Adapter<ListEmptyableRecyclerViewAdapter.ViewHolder<T>>() {


    var list: List<T> = initialList
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onScrollToBottom: (() -> Unit)? = null

    override fun getItemCount(): Int = list.size.coerceAtLeast(1)

    override fun getItemViewType(position: Int): Int {
        if (list.isEmpty()) return 1
        else return 0
    }

    var default: T? = null
    var shouldSetDefault = true
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T>? {
        return if (viewType == 0) {
            if (shouldSetDefault) {
                default = list.first()
                shouldSetDefault = false
            }
            val observable = ItemObservable(this)
            itemObservables.add(observable)
            val newView = SRVAContext(this, context).apply { makeView(observable) }.view
            val holder = ViewHolder(newView, observable)
            observable.viewHolder = holder
            itemHolders.add(holder)
            holder
        } else {
            val newView = SRVAContext(this, context).apply { makeEmptyView() }.view
            val holder = ViewHolder(newView, ItemObservable(this))
            holder
        }
    }

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
        if (list.isEmpty()) return
        if (itemCount > 0 && position + 1 == itemCount) {
            onScrollToBottom?.invoke()
        }
        if (list.isNotEmpty()) {
            holder.observable.apply {
                this.position = position
                update()
            }
        }
    }


    val itemHolders = ArrayList<ViewHolder<T>>()
    val itemObservables = ArrayList<ItemObservable<T>>()

    class ItemObservable<T>(val parent: ListEmptyableRecyclerViewAdapter<T>) : ObservablePropertyBase<T>() {
        var viewHolder: ViewHolder<T>? = null
        var position: Int = 0

        override var value: T
            get() {
                if (position >= 0 && position < parent.list.size) {
                    return parent.list[position]
                } else return parent.default as T
            }
            set(value) {
                if (position < 0 || position >= parent.list.size) return
                val list = parent.list as? MutableList<T> ?: throw IllegalAccessException()
                list[position] = value
                update()
            }

        override fun update() {
            if (position >= 0 && position < parent.list.size) {
                super.update()
            }
        }

    }

    class ViewHolder<T>(val view: View, val observable: ItemObservable<T>) : RecyclerView.ViewHolder(view)

    fun update(position: Int) {
        itemObservables.find { it.position == position }?.update()
    }

    class SRVAContext<T>(adapter: ListEmptyableRecyclerViewAdapter<T>, context: Context) : AnkoContextImpl<ListEmptyableRecyclerViewAdapter<T>>(context, adapter, false) {
        fun <V : View> V.lparams(
                width: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
                height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
                init: RecyclerView.LayoutParams.() -> Unit = {}
        ): V {
            val layoutParams = RecyclerView.LayoutParams(width, height)
            layoutParams.init()
            this@lparams.layoutParams = layoutParams

            return this
        }
    }
}

inline fun <T> RecyclerView.listAdapter(
        list: List<T>,
        noinline makeEmptyView: ListEmptyableRecyclerViewAdapter.SRVAContext<T>.() -> Unit,
        noinline makeView: ListEmptyableRecyclerViewAdapter.SRVAContext<T>.(ListEmptyableRecyclerViewAdapter.ItemObservable<T>) -> Unit
): ListEmptyableRecyclerViewAdapter<T> {
    val newAdapter = ListEmptyableRecyclerViewAdapter(context, list, makeEmptyView, makeView)
    return newAdapter
}

inline fun <T> RecyclerView.listAdapter(
        list: ObservableList<T>,
        noinline makeEmptyView: ListEmptyableRecyclerViewAdapter.SRVAContext<T>.() -> Unit,
        noinline makeView: ListEmptyableRecyclerViewAdapter.SRVAContext<T>.(ListEmptyableRecyclerViewAdapter.ItemObservable<T>) -> Unit
): ListEmptyableRecyclerViewAdapter<T> {
    val newAdapter = ListEmptyableRecyclerViewAdapter(context, list, makeEmptyView, makeView)
    newAdapter.attachAnimationsMin1(lifecycle, list)
    return newAdapter
}