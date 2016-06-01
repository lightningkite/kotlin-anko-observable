package com.ivieleague.kotlin.anko.observable.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import java.util.*

/**
 * Created by josep on 1/24/2016.
 */
class FilterableLightningAdapter<T>(
        val fullList: List<T>,
        val matches: T.(CharSequence) -> Boolean,
        val mutableList: MutableList<T> = mutableListOf(),
        makeView: (ItemObservable<T>) -> View
) : LightningAdapter<T>(mutableList, makeView), Filterable {

    override fun getFilter(): Filter? = object : Filter() {
        private val suggestions: ArrayList<T> = ArrayList()
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            if (constraint == null) return FilterResults()
            suggestions.clear()
            val constraintString = constraint.toString()
            for (item in fullList) {
                if (item.matches(constraintString)) {
                    suggestions.add(item)
                }
            }
            val results = FilterResults()
            results.count = suggestions.size
            results.values = suggestions
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            if (results.count > 0) {
                //list = results.values as ArrayList<T>
                mutableList.clear()
                mutableList.addAll(results.values as ArrayList<T>)
                notifyDataSetChanged()
            }
        }

    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View? = getView(position, convertView, parent)
}