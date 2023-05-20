package cryogenetics.logistics.ui.filters

import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import cryogenetics.logistics.api.Api

/**
 *  Manages a set of filters.
 */
class FilterManager {
    private var initialState: MutableMap<String, Map<String, String>> = mutableMapOf()
    private var adapters: MutableMap<View, CheckboxAdapter> = mutableMapOf()
    private var shorthands: MutableMap<View, String> = mutableMapOf()

    /**
     *  Adds a table from the database.
     *
     *  @param url - Url to get the table.
     *  @param name - What to name the table.
     *  @param view - The view to connect the table with.
     *  @param dbIncludeColumns - Which columns to pick data from. If left empty, picks from all.
     *  @param default - The default value of each field.
     */
    fun addTableFromDB(url: String, name: String, view: View, dbIncludeColumns: List<String> = emptyList(), default: String = "false") {
        // Fetch fields
        val fields: List<String> = Api.parseJsonArray(Api.fetchJsonData(url))
            .fold(emptyList()) { acc, e ->
                acc + e.filter { dbIncludeColumns.isEmpty() || dbIncludeColumns.contains(it.key) }.values.map { it.toString() }
            }

        // Add fields to filterState
        initialState[name] = fields.associateWith { default }

        // Add table
        addTable(name, view)
    }

    /**
     *  Adds a table.
     *
     *  @param name - What to name the table.
     *  @param view - The view to connect the table with.
     */
    fun addTable(name:String, view: View) {
        // Add view to list of views
        when (view) {
            is RecyclerView -> {
                val vi = view
                shorthands[view] = name
                val adapter = CheckboxAdapter(initialState[name]?.toList()?.map { it.first to (it.second=="true")} ?: emptyList())
                adapters[view] = adapter
                view.adapter = adapter
            }

            is EditText -> {
                shorthands[view] = name
                view.setText(initialState[name]?.get("text") ?: "")
            }

        }
    }

    /**
     *  Resets the filter states.
     */
    fun reset() {
        for (v in shorthands) {
            when (v.key) {
                is RecyclerView -> {
                    val adapter = adapters[v.key]
                    adapter?.updateData(adapter.dataList.map { Pair(it.first, false) })
                }
                is EditText -> {
                    (v.key as EditText).setText("")
                }
            }
        }
    }

    /**
     *  Gets the current filter state.
     *  The filter state is in the format "shorthand" : ("subElementName" : "true"/"false"/other).
     *
     *  @return The current filter state.
     */
    fun getState() : Map<String, Map<String, String>> {
        val filterStates = shorthands.map {
            it.value to ( when (it.key) {
                is RecyclerView -> adapters[it.key]?.getCheckboxStatesAsString() ?: emptyMap<String, String>()
                is EditText -> mapOf("text" to (it.key as EditText).text.toString())
                else -> emptyMap<String, String>()
            } ) as Map<String, String>
        }.toMap()
        return filterStates
    }

    /**
     *  Gets the url from a given filter state.
     *
     *  @return The url.
     */
    fun getUrl(base: String) : String {
        var url = "$base?"

        // Add states to URL
        for (column in getState()) { // column = Pair(columnName, fields)
            val fields = column.value
            for (field in fields) { // field = Pair(fieldName, value)
                val value = field.value
                url += when (value) {
                    "true" -> "${column.key}=${field.key}&"
                    "false" -> ""
                    "" -> ""
                    else -> "${column.key}=${field.value}&"
                }
            }
        }

        return url
    }
}