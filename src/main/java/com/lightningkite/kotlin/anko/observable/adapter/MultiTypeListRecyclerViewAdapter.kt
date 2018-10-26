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
import org.jetbrains.anko.frameLayout
import java.util.*

/**
 * An adapter for RecyclerViews intended to be used in all cases.
 *
 * Created by jivie on 5/4/16.
 */
class MultiTypeListRecyclerViewAdapter<T : Any>(
        val context: Context,
        initialList: List<T>,
        val makeViews: Map<Class<out T>, SRVAContext<T>.(ItemObservable<T>) -> Unit>
) : RecyclerView.Adapter<MultiTypeListRecyclerViewAdapter.ViewHolder<T>>() {

    val types = makeViews.keys.toList()

    var list: List<T> = initialList
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onScrollToBottom: (() -> Unit)? = null

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int {
        return types.indexOf(list[position].javaClass)
    }

    var default: T? = null
    var shouldSetDefault = true
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T> {
        if (shouldSetDefault) {
            default = list.first()
            shouldSetDefault = false
        }
        val observable = ItemObservable(this)
        itemObservables.add(observable)
        val srva = SRVAContext(this, context)
        val maker = makeViews[types[viewType]]
        if (maker != null) {
            maker.invoke(srva, observable)
        } else {
            srva.frameLayout()
        }
        val newView = srva.view
        val holder = ViewHolder(newView, observable)
        observable.viewHolder = holder
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


    val itemObservables = ArrayList<ItemObservable<T>>()

    class ItemObservable<T : Any>(val parent: MultiTypeListRecyclerViewAdapter<T>) : ObservablePropertyBase<T>() {
        var viewHolder: MultiTypeListRecyclerViewAdapter.ViewHolder<T>? = null
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

    class ViewHolder<T : Any>(val itemView: View, val observable: ItemObservable<T>) : RecyclerView.ViewHolder(itemView)

    fun update(position: Int) {
        itemObservables.find { it.position == position }?.update()
    }

    class SRVAContext<T : Any>(adapter: MultiTypeListRecyclerViewAdapter<T>, context: Context) : AnkoContextImpl<MultiTypeListRecyclerViewAdapter<T>>(context, adapter, false) {
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


inline fun <T : Any> RecyclerView.multiTypeListAdapter(
        list: List<T>,
        makeViews: Map<Class<out T>, MultiTypeListRecyclerViewAdapter.SRVAContext<T>.(MultiTypeListRecyclerViewAdapter.ItemObservable<T>) -> Unit>
): MultiTypeListRecyclerViewAdapter<T> {
    val newAdapter = MultiTypeListRecyclerViewAdapter(context, list, makeViews)
    return newAdapter
}

inline fun <T : Any> RecyclerView.multiTypeListAdapter(
        list: ObservableList<T>,
        makeViews: Map<Class<out T>, MultiTypeListRecyclerViewAdapter.SRVAContext<T>.(MultiTypeListRecyclerViewAdapter.ItemObservable<T>) -> Unit>
): MultiTypeListRecyclerViewAdapter<T> {
    val newAdapter = MultiTypeListRecyclerViewAdapter(context, list, makeViews)
    newAdapter.attachAnimations(lifecycle, list)
    return newAdapter
}

inline fun <T : Any> RecyclerView.multiTypeListAdapterObservable(
        listObs: ObservableProperty<List<T>>,
        makeViews: Map<Class<out T>, MultiTypeListRecyclerViewAdapter.SRVAContext<T>.(MultiTypeListRecyclerViewAdapter.ItemObservable<T>) -> Unit>
): MultiTypeListRecyclerViewAdapter<T> {
    val newAdapter = MultiTypeListRecyclerViewAdapter(context, listObs.value, makeViews)
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
            newAdapter.detatchAnimations<T, MultiTypeListRecyclerViewAdapter.ViewHolder<T>>()
        }

    })
    return newAdapter
}