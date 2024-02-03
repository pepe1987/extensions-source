package eu.kanade.tachiyomi.extension.all.komga

import eu.kanade.tachiyomi.source.Source
import eu.kanade.tachiyomi.source.SourceFactory

class KomgaFactory : SourceFactory {
    override fun createSources(): List<Source> {
        val firstKomga = Komga("")
        val komgaCount = firstKomga.preferences.getString(Komga.PREF_EXTRA_SOURCES_COUNT, Komga.PREF_EXTRA_SOURCES_DEFAULT)!!.toInt()

        // Komga(""), Komga("2"), Komga("3"), ...
        return listOf(firstKomga) + (0 until komgaCount).map { Komga("${it + 2}") }
    }
}
