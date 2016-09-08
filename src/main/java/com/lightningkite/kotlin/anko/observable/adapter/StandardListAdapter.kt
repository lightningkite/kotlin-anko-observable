package com.lightningkite.kotlin.anko.observable.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.Spinner
import com.lightningkite.kotlin.anko.lifecycle
import com.lightningkite.kotlin.lifecycle.listen
import com.lightningkite.kotlin.observable.list.ObservableList
import com.lightningkite.kotlin.observable.property.MutableObservableProperty
import com.lightningkite.kotlin.observable.property.ObservableProperty
import com.lightningkite.kotlin.observable.property.StandardObservableProperty
import com.lightningkite.kotlin.observable.property.bind
import org.jetbrains.anko.AnkoContextImpl
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.onItemSelectedListener
import org.jetbrains.anko.wrapContent

/**
 * Created by josep on 1/24/2016.
 */
open class StandardListAdapter<T>(
        val context: Context,
        list: List<T>,
        val makeView: StandardListAdapter.SLVAContext<T>.(StandardListAdapter.ItemObservable<T>) -> Unit
) : BaseAdapter() {

    class ItemObservable<T>(init: T) : StandardObservableProperty<T>(init) {
        var index: Int = 0
        override fun remove(element: (T) -> Unit): Boolean {
            return super.remove(element)
        }
    }

    var list: List<T> = list
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getCount(): Int = list.size
    override fun getItem(position: Int): Any? = list[position]
    override fun getItemId(position: Int): Long = position.toLong()

    @Suppress("UNCHECKED_CAST")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val item = list[position]
        if (convertView == null) {
            val newObs = ItemObservable(list[position])
            val newView = SLVAContext(this, context).apply { makeView(newObs) }.view
            newView.tag = newObs
            newObs.index = position
            newView.layoutParams = AbsListView.LayoutParams(matchParent, wrapContent)
            return newView
        } else {
            val obs = convertView.tag as ItemObservable<T>
            obs.index = position
            obs.value = (list[position])
            return convertView
        }
    }

    class SLVAContext<T>(adapter: StandardListAdapter<T>, context: Context) : AnkoContextImpl<StandardListAdapter<T>>(context, adapter, false) {
        fun <V : View> V.lparams(
                width: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
                height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
                init: AbsListView.LayoutParams.() -> Unit = {}
        ): V {
            val layoutParams = AbsListView.LayoutParams(width, height)
            layoutParams.init()
            this@lparams.layoutParams = layoutParams

            return this
        }
    }
}

inline fun <T> ListView.standardAdapter(
        list: List<T>,
        noinline makeView: StandardListAdapter.SLVAContext<T>.(StandardListAdapter.ItemObservable<T>) -> Unit
): StandardListAdapter<T> {
    val newAdapter = StandardListAdapter(context, list, makeView)
    adapter = newAdapter
    return newAdapter
}

inline fun <T> ListView.standardAdapter(
        list: ObservableList<T>,
        noinline makeView: StandardListAdapter.SLVAContext<T>.(StandardListAdapter.ItemObservable<T>) -> Unit
): StandardListAdapter<T> {
    val newAdapter: StandardListAdapter<T> = StandardListAdapter(context, list, makeView)
    adapter = newAdapter
    lifecycle.listen(list.onUpdate) {
        newAdapter.notifyDataSetChanged()
    }
    return newAdapter
}

inline fun <T> Spinner.standardAdapter(
        list: List<T>,
        noinline makeView: StandardListAdapter.SLVAContext<T>.(StandardListAdapter.ItemObservable<T>) -> Unit
): StandardListAdapter<T> {
    val newAdapter = StandardListAdapter(context, list, makeView)
    adapter = newAdapter
    return newAdapter
}

inline fun <T> Spinner.standardAdapter(
        listObs: ObservableProperty<List<T>>,
        noinline makeView: StandardListAdapter.SLVAContext<T>.(StandardListAdapter.ItemObservable<T>) -> Unit
): StandardListAdapter<T> {
    val newAdapter: StandardListAdapter<T> = StandardListAdapter(context, listObs.value, makeView)
    adapter = newAdapter
    lifecycle.listen(listObs) {
        newAdapter.list = it
    }
    return newAdapter
}

inline fun <T> Spinner.standardAdapter(
        list: ObservableList<T>,
        noinline makeView: StandardListAdapter.SLVAContext<T>.(StandardListAdapter.ItemObservable<T>) -> Unit
): StandardListAdapter<T> {
    val newAdapter: StandardListAdapter<T> = StandardListAdapter(context, list, makeView)
    adapter = newAdapter
    lifecycle.listen(list.onUpdate) {
        newAdapter.notifyDataSetChanged()
    }
    return newAdapter
}

inline fun <T> Spinner.standardAdapter(
        list: ObservableList<T>,
        selected: MutableObservableProperty<T>,
        noinline makeView: StandardListAdapter.SLVAContext<T>.(StandardListAdapter.ItemObservable<T>) -> Unit
): StandardListAdapter<T> {
    val newAdapter: StandardListAdapter<T> = StandardListAdapter(context, list, makeView)
    adapter = newAdapter

    var indexAlreadySet = false

    lifecycle.listen(list.onUpdate) {
        newAdapter.notifyDataSetChanged()
        val index = list.indexOf(selected.value)
        println("update to $index - ${selected.value}")
        if (index == -1) {
            println("could not find ${selected.value}")
            setSelection(0)
            return@listen
        }
        setSelection(index)
    }

    lifecycle.bind(selected) { it ->
        val index = list.indexOf(it)
        println("selected to $index - $it")
        if (index == -1) {
            println("could not find $it")
            setSelection(0)
            return@bind
        }
        if (!indexAlreadySet) {
            setSelection(index)
        } else {
            indexAlreadySet = false
        }
    }

    this.onItemSelectedListener {
        onItemSelected { adapterView, view, index, id ->
            println("set to $index - ${list[index]}")
            indexAlreadySet = true
            selected.value = (list[index])
        }
    }
    return newAdapter
}

inline fun <T> Spinner.standardAdapter(
        list: List<T>,
        selected: MutableObservableProperty<T>,
        noinline makeView: StandardListAdapter.SLVAContext<T>.(StandardListAdapter.ItemObservable<T>) -> Unit
): StandardListAdapter<T> {
    val newAdapter: StandardListAdapter<T> = StandardListAdapter(context, list, makeView)
    adapter = newAdapter

    var indexAlreadySet = false

    lifecycle.bind(selected) { it ->
        val index = list.indexOf(it)
        println("selected to $index - $it")
        if (index == -1) {
            println("could not find $it")
            setSelection(0)
            return@bind
        }
        if (!indexAlreadySet) {
            setSelection(index)
        } else {
            indexAlreadySet = false
        }
    }

    this.onItemSelectedListener {
        onItemSelected { adapterView, view, index, id ->
            println("set to $index - ${list[index]}")
            indexAlreadySet = true
            selected.value = (list[index])
        }
    }
    return newAdapter
}