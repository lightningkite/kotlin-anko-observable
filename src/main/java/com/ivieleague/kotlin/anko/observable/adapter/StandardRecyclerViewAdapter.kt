package com.ivieleague.kotlin.anko.observable.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.ivieleague.kotlin.anko.observable.lifecycle
import com.ivieleague.kotlin.lifecycle.listen
import com.ivieleague.kotlin.observable.list.ObservableList
import com.ivieleague.kotlin.observable.list.ObservableListListenerSet
import com.ivieleague.kotlin.observable.list.addListenerSet
import com.ivieleague.kotlin.observable.list.removeListenerSet
import com.ivieleague.kotlin.observable.property.ObservableProperty
import com.ivieleague.kotlin.observable.property.ObservablePropertyBase
import org.jetbrains.anko.AnkoContextImpl
import java.util.*

/**
 * An adapter for RecyclerViews intended to be used in all cases.
 *
 * Created by jivie on 5/4/16.
 */
class StandardRecyclerViewAdapter<T>(
        val context: Context,
        initialList: List<T>,
        val makeView: SRVAContext<T>.(ItemObservable<T>) -> Unit
) : RecyclerView.Adapter<StandardRecyclerViewAdapter.ViewHolder<T>>() {

    var list: List<T> = initialList
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var previousListenerSet: ObservableListListenerSet<T>? = null

    fun attachAnimations(list: ObservableList<T>) {
        detatchAnimations()
        previousListenerSet = ObservableListListenerSet(
                onAddListener = { item: T, position: Int ->
                    notifyItemInserted(position)
                },
                onRemoveListener = { item: T, position: Int ->
                    notifyItemRemoved(position)
                },
                onChangeListener = { item: T, position: Int ->
                    //adapter.notifyItemChanged(position)
                    update(position)
                },
                onReplaceListener = { list: ObservableList<T> ->
                    notifyDataSetChanged()
                }
        )
        list.addListenerSet(previousListenerSet!!)
    }

    fun detatchAnimations() {
        if (previousListenerSet != null) {
            (list as? ObservableList<T>)?.removeListenerSet(previousListenerSet!!)
        }
    }

    var onScrollToBottom: (() -> Unit)? = null

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T>? {
        val observable = ItemObservable(this)
        itemObservables.add(observable)
        val newView = SRVAContext(this, context).apply { makeView(observable) }.view
        val holder = ViewHolder(newView, observable)
        observable.viewHolder = holder
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
        if (itemCount > 0 && position + 1 == itemCount) {
            onScrollToBottom?.invoke()
        }
        holder.observable.update()
    }


    val itemObservables = ArrayList<ItemObservable<T>>()

    class ItemObservable<T>(val parent: StandardRecyclerViewAdapter<T>) : ObservablePropertyBase<T>() {
        var viewHolder: ViewHolder<T>? = null
        val position: Int get() = viewHolder?.adapterPosition ?: 0

        override var value: T
            get() {
                if (position >= 0 && position < parent.list.size) {
                    return parent.list[position]
                } else return parent.list.first()
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

    class ViewHolder<T>(val itemView: View, val observable: ItemObservable<T>) : RecyclerView.ViewHolder(itemView)

    fun update(position: Int) {
        itemObservables.find { it.position == position }?.update()
    }

    class SRVAContext<T>(adapter: StandardRecyclerViewAdapter<T>, context: Context) : AnkoContextImpl<StandardRecyclerViewAdapter<T>>(context, adapter, false) {
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

@Deprecated("Use standardAdapter() instead.", ReplaceWith("standardAdapter(list, makeView)", "com.lightningkite.kotlincomponents.adapter.standardAdapter"))
inline fun <T> RecyclerView.adapter(
        list: List<T>,
        noinline makeView: StandardRecyclerViewAdapter.SRVAContext<T>.(StandardRecyclerViewAdapter.ItemObservable<T>) -> Unit
): StandardRecyclerViewAdapter<T> = standardAdapter(list, makeView)

inline fun <T> RecyclerView.standardAdapter(
        list: List<T>,
        noinline makeView: StandardRecyclerViewAdapter.SRVAContext<T>.(StandardRecyclerViewAdapter.ItemObservable<T>) -> Unit
): StandardRecyclerViewAdapter<T> {
    val newAdapter = StandardRecyclerViewAdapter(context, list, makeView)
    adapter = newAdapter
    return newAdapter
}

@Deprecated("Use standardAdapter() instead.", ReplaceWith("standardAdapter(list, makeView)", "com.lightningkite.kotlincomponents.adapter.standardAdapter"))
inline fun <T> RecyclerView.adapter(
        list: ObservableList<T>,
        noinline makeView: StandardRecyclerViewAdapter.SRVAContext<T>.(StandardRecyclerViewAdapter.ItemObservable<T>) -> Unit
): StandardRecyclerViewAdapter<T> = standardAdapter(list, makeView)

inline fun <T> RecyclerView.standardAdapter(
        list: ObservableList<T>,
        noinline makeView: StandardRecyclerViewAdapter.SRVAContext<T>.(StandardRecyclerViewAdapter.ItemObservable<T>) -> Unit
): StandardRecyclerViewAdapter<T> {
    val newAdapter = StandardRecyclerViewAdapter(context, list, makeView)
    adapter = newAdapter
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View?) {
            newAdapter.attachAnimations(list)
        }

        override fun onViewDetachedFromWindow(v: View?) {
            newAdapter.detatchAnimations()
        }

    })
    return newAdapter
}

inline fun <T> RecyclerView.standardAdapterObservable(
        listObs: ObservableProperty<List<T>>,
        noinline makeView: StandardRecyclerViewAdapter.SRVAContext<T>.(StandardRecyclerViewAdapter.ItemObservable<T>) -> Unit
): StandardRecyclerViewAdapter<T> {
    val newAdapter = StandardRecyclerViewAdapter(context, listObs.value, makeView)
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
            newAdapter.detatchAnimations()
        }

    })
    adapter = newAdapter
    return newAdapter
}
//
//fun RecyclerView.handlePaging(pagedEndpoint: PagedEndpoint<*>, kAdapter: StandardRecyclerViewAdapter<*>, pullingUpdate: (Boolean) -> Unit = {}) {
//    var morePages = false
//    bind(pagedEndpoint.isMoreObservable) { hasMore ->
//        morePages = hasMore
//    }
//    bind(pagedEndpoint.pullingObservable) {
//        pullingUpdate.invoke(it)
//    }
//
//    kAdapter.onScrollToBottom = {
//        if (!pagedEndpoint.pulling && morePages) {
//            pagedEndpoint.pull()
//        }
//    }
//}
//
//fun <T : Any> RecyclerView.handlePagingObservable(pagedEndpointObs: ObservableInterface<PagedEndpoint<T>>, kAdapter: StandardRecyclerViewAdapter<*>, pullingUpdate: (Boolean) -> Unit = {}) {
//    var morePages = false
//    bindSub(pagedEndpointObs, { it.isMoreObservable }) { hasMore ->
//        morePages = hasMore
//    }
//    bindSub(pagedEndpointObs, { it.pullingObservable }) {
//        pullingUpdate.invoke(it)
//    }
//
//    kAdapter.onScrollToBottom = {
//        if (!pagedEndpointObs.get().pulling && morePages) {
//            pagedEndpointObs.get().pull()
//        }
//    }
//}
//
//fun <T : Any> RecyclerView.handlePagingOptionalObservable(pagedEndpointObs: ObservableInterface<PagedEndpoint<T>?>, kAdapter: StandardRecyclerViewAdapter<*>, pullingUpdate: (Boolean) -> Unit = {}) {
//    var morePages = false
//    bindSub(pagedEndpointObs, { it?.isMoreObservable ?: Observable(true) }) { hasMore ->
//        morePages = hasMore
//    }
//    bindSub(pagedEndpointObs, { it?.pullingObservable ?: Observable(true) }) {
//        pullingUpdate.invoke(it)
//    }
//
//    kAdapter.onScrollToBottom = {
//        if (!(pagedEndpointObs.get()?.pulling ?: true) && morePages) {
//            pagedEndpointObs.get()?.pull()
//        }
//    }
//}