package com.lightningkite.kotlin.anko.observable.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.lightningkite.kotlin.anko.lifecycle
import com.lightningkite.kotlin.lifecycle.listen
import com.lightningkite.kotlin.observable.list.ObservableList
import com.lightningkite.kotlin.observable.property.ObservableProperty
import com.lightningkite.kotlin.observable.property.ObservablePropertyBase
import org.jetbrains.anko.AnkoContextImpl
import java.util.*

@Deprecated("Use ListRecyclerViewAdapter<T> instead.", ReplaceWith("ListRecyclerViewAdapter<T>", "com.lightningkite.kotlincomponents.adapter.ListRecyclerViewAdapter"))
class StandardRecyclerViewAdapter<T>(
        context: Context,
        initialList: List<T>,
        makeView: SRVAContext<T>.(ItemObservable<T>) -> Unit
) : ListRecyclerViewAdapter<T>(
        context,
        initialList,
        makeView
)

/**
 * An adapter for RecyclerViews intended to be used in all cases.
 *
 * Created by jivie on 5/4/16.
 */
open class ListRecyclerViewAdapter<T>(
        val context: Context,
        initialList: List<T>,
        val makeView: SRVAContext<T>.(ItemObservable<T>) -> Unit
) : RecyclerView.Adapter<ListRecyclerViewAdapter.ViewHolder<T>>() {



    var list: List<T> = initialList
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onScrollToBottom: (() -> Unit)? = null

    override fun getItemCount(): Int = list.size

    var default: T? = null
    var shouldSetDefault = true
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T>? {
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
        holder.view.lifecycle.setAlwaysOnRecursive() //Necessary because Android is broke
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
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

    class ItemObservable<T>(val parent: ListRecyclerViewAdapter<T>) : ObservablePropertyBase<T>() {
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

    class SRVAContext<T>(adapter: ListRecyclerViewAdapter<T>, context: Context) : AnkoContextImpl<ListRecyclerViewAdapter<T>>(context, adapter, false) {
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
        noinline makeView: ListRecyclerViewAdapter.SRVAContext<T>.(ListRecyclerViewAdapter.ItemObservable<T>) -> Unit
): ListRecyclerViewAdapter<T> {
    val newAdapter = ListRecyclerViewAdapter(context, list, makeView)
    return newAdapter
}

inline fun <T> RecyclerView.listAdapter(
        list: ObservableList<T>,
        noinline makeView: ListRecyclerViewAdapter.SRVAContext<T>.(ListRecyclerViewAdapter.ItemObservable<T>) -> Unit
): ListRecyclerViewAdapter<T> {
    val newAdapter = ListRecyclerViewAdapter(context, list, makeView)
    newAdapter.attachAnimations(lifecycle, list)
    return newAdapter
}


inline fun <T> RecyclerView.listAdapterObservable(
        listObs: ObservableProperty<List<T>>,
        noinline makeView: ListRecyclerViewAdapter.SRVAContext<T>.(ListRecyclerViewAdapter.ItemObservable<T>) -> Unit
): ListRecyclerViewAdapter<T> {
    val newAdapter = ListRecyclerViewAdapter(context, listObs.value, makeView)
    lifecycle.listen(listObs) {
        newAdapter.list = it
        if (it is ObservableList<T>) {
            newAdapter.attachAnimations(it)
        }
    }
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View?) {
            val list = listObs.value
            if (list is ObservableList<T>) {
                newAdapter.attachAnimations(list)
            }
        }

        override fun onViewDetachedFromWindow(v: View?) {
            newAdapter.detatchAnimations<T, ListRecyclerViewAdapter.ViewHolder<T>>()
        }

    })
    adapter = newAdapter
    return newAdapter
}