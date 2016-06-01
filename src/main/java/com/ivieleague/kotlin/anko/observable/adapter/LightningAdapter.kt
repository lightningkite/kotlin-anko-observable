package com.ivieleague.kotlin.anko.observable.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.Spinner
import com.ivieleague.kotlin.anko.observable.lifecycle
import com.ivieleague.kotlin.observable.property.ObservableProperty
import com.ivieleague.kotlin.observable.property.StandardObservableProperty
import com.ivieleague.kotlin.observable.property.bind
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent

/**
 * Created by josep on 1/24/2016.
 */
open class LightningAdapter<T>(list: List<T>, val makeView: (ItemObservable<T>) -> View) : BaseAdapter() {

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
            val newView = makeView(newObs)
            newView.tag = newObs
            newObs.index = position
            newView.layoutParams = ViewGroup.LayoutParams(matchParent, wrapContent)
            return newView
        } else {
            val obs = convertView.tag as ItemObservable<T>
            obs.index = position
            obs.value = (list[position])
        }
        return convertView
    }

}

fun <T, C : List<T>> View.lightningAdapter(listObs: ObservableProperty<C>, makeView: (LightningAdapter.ItemObservable<T>) -> View): LightningAdapter<T> {
    val result = LightningAdapter(listObs.value, makeView)
    lifecycle.bind(listObs) {
        result.list = it
    }
    return result
}

fun <T> ListView.makeAdapter(list: List<T>, makeView: (LightningAdapter.ItemObservable<T>) -> View): LightningAdapter<T> {
    val result = LightningAdapter(list, makeView)
    adapter = result
    return result
}

fun <T> Spinner.makeAdapter(list: List<T>, makeView: (LightningAdapter.ItemObservable<T>) -> View): LightningAdapter<T> {
    val result = LightningAdapter(list, makeView)
    adapter = result
    return result
}