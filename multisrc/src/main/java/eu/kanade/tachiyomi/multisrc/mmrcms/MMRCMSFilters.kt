package eu.kanade.tachiyomi.multisrc.mmrcms

import eu.kanade.tachiyomi.source.model.Filter
import okhttp3.HttpUrl

interface UriFilter {
    fun addToUri(builder: HttpUrl.Builder)
}

class TextFilter(name: String, private val param: String) : Filter.Text(name), UriFilter {
    override fun addToUri(builder: HttpUrl.Builder) {
        builder.addQueryParameter(param, state)
    }
}

class UriPartFilter(
    name: String,
    private val param: String,
    private val vals: Array<Pair<String, String>>,
    private val firstIsUnspecified: Boolean = true,
    defaultValue: Int = 0,
) : Filter.Select<String>(name, vals.map { it.first }.toTypedArray(), defaultValue), UriFilter {
    override fun addToUri(builder: HttpUrl.Builder) {
        if (state == 0 && firstIsUnspecified) {
            return
        }

        builder.addQueryParameter(param, vals[state].second)
    }
}

class UriMultiSelectOption(name: String, val value: String) : Filter.CheckBox(name)

class UriMultiSelectFilter(
    name: String,
    private val param: String,
    private val vals: Array<Pair<String, String>>,
) : Filter.Group<UriMultiSelectOption>(name, vals.map { UriMultiSelectOption(it.first, it.second) }), UriFilter {
    override fun addToUri(builder: HttpUrl.Builder) {
        val checked = state.filter { it.state }

        if (checked.isEmpty()) {
            return
        }

        checked.forEach { builder.addQueryParameter(param, it.value) }
    }
}

class SortFilter(selection: Selection = Selection(0, true)) :
    Filter.Sort(
        "Sort by",
        sortables.map { it.second }.toTypedArray(),
        selection,
    ),
    UriFilter {
    override fun addToUri(builder: HttpUrl.Builder) {
        val state = state!!

        builder.apply {
            addQueryParameter("sortBy", sortables[state.index].first)
            addQueryParameter("asc", state.ascending.toString())
        }
    }

    companion object {
        private val sortables = arrayOf(
            "name" to "Name",
            "views" to "Popularity",
            "last_release" to "Last update",
        )
    }
}
