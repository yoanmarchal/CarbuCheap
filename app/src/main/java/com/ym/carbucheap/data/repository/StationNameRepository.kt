package com.ym.carbucheap.data.repository

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Brand / display names of fuel stations ("TotalEnergies", "Intermarché"…), keyed by the
 * official station id. The government price feed has no name field, so the names come from
 * the Chiffrex / OpenStreetMap referential (ODbL) bundled as an asset —
 * regenerate it with `scripts/update_station_names.py`.
 */
class StationNameRepository(private val context: Context) {

    private val mutex = Mutex()
    private var names: Map<String, String>? = null

    suspend fun getNames(): Map<String, String> = mutex.withLock {
        names ?: withContext(Dispatchers.IO) { load() }.also { names = it }
    }

    private fun load(): Map<String, String> = try {
        context.assets.open(ASSET).bufferedReader(Charsets.UTF_8).useLines(::parse)
    } catch (e: Exception) {
        // Names are a nice-to-have: fall back to addresses rather than failing the list.
        Log.w("StationNames", "Could not load $ASSET", e)
        emptyMap()
    }

    companion object {
        const val ASSET = "station_names.tsv"

        /** Parses "<id>\t<name>" lines; tolerant to CRLF and malformed lines. */
        fun parse(lines: Sequence<String>): Map<String, String> = buildMap {
            for (line in lines) {
                val tab = line.indexOf('\t')
                if (tab <= 0) continue
                val id = line.substring(0, tab).trim()
                val name = line.substring(tab + 1).trim()
                if (id.isNotEmpty() && name.isNotEmpty()) put(id, name)
            }
        }
    }
}
