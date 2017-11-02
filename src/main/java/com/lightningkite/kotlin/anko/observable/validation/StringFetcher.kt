package com.lightningkite.kotlin.anko.observable.validation

import android.content.res.Resources
import android.support.annotation.StringRes

/**
 * Abstraction for handling string fetching, as for unit tests we don't have access to [Resources].
 * Created by joseph on 11/2/17.
 */


typealias StringFetcher = (Resources) -> String
class StringFetcherDirect(val data: String) : StringFetcher {
    override fun invoke(resources: Resources): String = data
}

class StringFetcherResource(@StringRes val resource: Int) : StringFetcher {
    override fun invoke(resources: Resources): String = resources.getString(resource)
}