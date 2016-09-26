package com.lightningkite.kotlin.anko.observable.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.ViewGroup
import com.lightningkite.kotlin.runAll
import java.util.*

/**
 * An adapter for RecyclerViews that contains other adapters.
 *
 * Created by jivie on 5/4/16.
 */
class MultiRecyclerViewAdapter(
        val context: Context,
        val adapters: List<RecyclerView.Adapter<*>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ChildObserver(val child: RecyclerView.Adapter<*>) : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            val myFromPosition = getIndexInThis(child, fromPosition)
            val myToPosition = getIndexInThis(child, toPosition)
            for (index in 0..itemCount - 1) {
                this@MultiRecyclerViewAdapter.notifyItemMoved(myFromPosition + index, myToPosition + index)
            }
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            this@MultiRecyclerViewAdapter.notifyItemRangeInserted(getIndexInThis(child, positionStart), itemCount)
        }

        override fun onChanged() {
            this@MultiRecyclerViewAdapter.notifyDataSetChanged()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            Log.d("MultiRecyclerViewAdapte", "onItemRangeRemoved - pos = $positionStart, count = $itemCount")
            val resultPos = getIndexInThis(child, positionStart)
            Log.d("MultiRecyclerViewAdapte", "onItemRangeRemoved - resultPos = $resultPos, count = $itemCount")
            this@MultiRecyclerViewAdapter.notifyItemRangeRemoved(resultPos, itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            this@MultiRecyclerViewAdapter.notifyItemRangeChanged(getIndexInThis(child, positionStart), itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            this@MultiRecyclerViewAdapter.notifyItemRangeChanged(getIndexInThis(child, positionStart), itemCount, payload)
        }
    }

    var timesRegistered = 0
    val removers = ArrayList<() -> Unit>()
    override fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        if (timesRegistered == 0) {
            for (adapter in adapters) {
                val obs = ChildObserver(adapter)
                adapter.registerAdapterDataObserver(obs)
                removers += { obs.child.unregisterAdapterDataObserver(obs) }
            }
        }
        timesRegistered++
        super.registerAdapterDataObserver(observer)
    }

    override fun unregisterAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        timesRegistered--
        if (timesRegistered == 0) {
            removers.runAll()
            removers.clear()
        }
        super.unregisterAdapterDataObserver(observer)
    }

    val types = HashMap<Pair<Int, Int>, Int>()
    val reverseTypes = HashMap<Int, Pair<Int, Int>>()

    override fun getItemCount(): Int = adapters.sumBy { it.itemCount }

    fun getIndexInChild(position: Int): Int {
        var beforePos = 0
        var pastPos = 0
        var adapterIndex = 0
        for (adapter in adapters) {
            pastPos += adapter.itemCount
            if (position < pastPos) {
                return position - beforePos
            }
            beforePos = pastPos
            adapterIndex++
        }
        throw IllegalArgumentException()
    }

    fun getChildInfo(position: Int): Pair<RecyclerView.Adapter<*>, Int> {
        var beforePos = 0
        var pastPos = 0
        var adapterIndex = 0
        for (adapter in adapters) {
            pastPos += adapter.itemCount
            if (position < pastPos) {
                return adapter to position - beforePos
            }
            beforePos = pastPos
            adapterIndex++
        }
        throw IllegalArgumentException()
    }

    fun getIndexInThis(adapter: RecyclerView.Adapter<*>, position: Int): Int {
        var startPos = 0
        for (child in adapters) {
            if (child === adapter) {
                return position + startPos
            }
            startPos += child.itemCount
        }
        throw IllegalArgumentException()
    }

    override fun getItemViewType(position: Int): Int {
        var beforePos = 0
        var afterPos = 0
        var adapterIndex = 0
        for (adapter in adapters) {
            afterPos += adapter.itemCount
            if (position < afterPos) {
                val pair = adapterIndex to adapter.getItemViewType(position - beforePos)
                return if (types.containsKey(pair)) {
                    types[pair]!!
                } else {
                    val new = types.size
                    types.put(pair, new)
                    reverseTypes.put(new, pair)
                    new
                }
            }
            beforePos = afterPos
            adapterIndex++
        }
        throw IllegalArgumentException()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val type = reverseTypes[viewType] ?: throw IllegalArgumentException("ViewType $viewType is not defined yet!")
        return adapters[type.first].onCreateViewHolder(parent, type.second)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val info = getChildInfo(position)
        (adapters[getItemViewType(position)] as RecyclerView.Adapter<RecyclerView.ViewHolder>).onBindViewHolder(holder, info.second)
    }
}

inline fun RecyclerView.multiAdapter(
        adapters: List<RecyclerView.Adapter<*>>
): MultiRecyclerViewAdapter = MultiRecyclerViewAdapter(context, adapters)

inline fun RecyclerView.multiAdapter(
        vararg adapterArgs: RecyclerView.Adapter<*>
): MultiRecyclerViewAdapter = multiAdapter(adapterArgs.toList())