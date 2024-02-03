package eu.kanade.tachiyomi.extension.id.shinigami

import android.util.Base64
import eu.kanade.tachiyomi.lib.cryptoaes.CryptoAES
import eu.kanade.tachiyomi.lib.synchrony.Deobfuscator
import eu.kanade.tachiyomi.multisrc.madara.Madara
import eu.kanade.tachiyomi.network.interceptor.rateLimit
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SChapter
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import okhttp3.OkHttpClient
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.util.concurrent.TimeUnit

class Shinigami : Madara("Shinigami", "https://shinigamitoon.com", "id") {
    // moved from Reaper Scans (id) to Shinigami (id)
    override val id = 3411809758861089969

    override val useNewChapterEndpoint = false

    override fun searchPage(page: Int): String = if (page == 1) "" else "page/$page/"

    override fun headersBuilder() = super.headersBuilder().apply {
        add("Sec-Fetch-Dest", "document")
        add("Sec-Fetch-Mode", "navigate")
        add("Sec-Fetch-Site", "same-origin")
        add("Upgrade-Insecure-Requests", "1")
        add("X-Requested-With", randomString((1..20).random())) // added for webview, and removed in interceptor for normal use
    }

    override val client: OkHttpClient = network.cloudflareClient.newBuilder()
        .addInterceptor { chain ->
            val request = chain.request()
            val headers = request.headers.newBuilder().apply {
                removeAll("X-Requested-With")
            }.build()

            chain.proceed(request.newBuilder().headers(headers).build())
        }
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .rateLimit(3)
        .build()

    override val mangaSubString = "semua-series"

    // Tags are useless as they are just SEO keywords.
    override val mangaDetailsSelectorTag = ""

    override val chapterUrlSelector = "div.chapter-link:not([style~=display:\\snone]) a"

    override fun chapterFromElement(element: Element): SChapter = SChapter.create().apply {
        val urlElement = element.selectFirst(chapterUrlSelector)!!

        name = urlElement.selectFirst("p.chapter-manhwa-title")?.text()
            ?: urlElement.ownText()
        date_upload = urlElement.selectFirst("span.chapter-release-date > i")?.text()
            .let { parseChapterDate(it) }

        val fixedUrl = urlElement.attr("abs:href")

        setUrlWithoutDomain(fixedUrl)
    }

    // Page list
    @Serializable
    data class CDT(val ct: String, val s: String)

    override fun pageListParse(document: Document): List<Page> {
        val script = document.selectFirst("script:containsData(chapter_data)")?.data()
            ?: throw Exception("chapter_data script not found")

        val deobfuscated = Deobfuscator.deobfuscateScript(script)
            ?: throw Exception("Unable to deobfuscate chapter_data script")

        val keyMatch = KEY_REGEX.find(deobfuscated)?.groupValues
            ?: throw Exception("Unable to find key")

        val chapterData = json.decodeFromString<CDT>(
            CHAPTER_DATA_REGEX.find(script)?.groupValues?.get(1) ?: throw Exception("Unable to get chapter data"),
        )
        val postId = POST_ID_REGEX.find(script)?.groupValues?.get(1) ?: throw Exception("Unable to get post_id")
        val otherId = OTHER_ID_REGEX.findAll(script).firstOrNull { it.groupValues[1] != "post" }?.groupValues?.get(2) ?: throw Exception("Unable to get other id")
        val key = otherId + keyMatch[1] + postId + keyMatch[2] + postId
        val salt = chapterData.s.decodeHex()

        val unsaltedCiphertext = Base64.decode(chapterData.ct, Base64.DEFAULT)
        val ciphertext = SALTED + salt + unsaltedCiphertext

        val decrypted = CryptoAES.decrypt(Base64.encodeToString(ciphertext, Base64.DEFAULT), key)
        val data = json.decodeFromString<List<String>>(decrypted)
        return data.mapIndexed { idx, it ->
            Page(idx, document.location(), it)
        }
    }

    // https://stackoverflow.com/a/66614516
    private fun String.decodeHex(): ByteArray {
        check(length % 2 == 0) { "Must have an even length" }

        return chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray()
    }

    private fun randomString(length: Int): String {
        val charPool = ('a'..'z') + ('A'..'Z')
        return List(length) { charPool.random() }.joinToString("")
    }

    companion object {
        private val KEY_REGEX by lazy { Regex("""_id\s+\+\s+'(.*?)'\s+\+\s+post_id\s+\+\s+'(.*?)'\s+\+\s+post_id""") }
        private val CHAPTER_DATA_REGEX by lazy { Regex("""var chapter_data\s*=\s*'(.*?)'""") }
        private val POST_ID_REGEX by lazy { Regex("""var post_id\s*=\s*'(.*?)'""") }
        private val OTHER_ID_REGEX by lazy { Regex("""var (\w+)_id\s*=\s*'(.*?)'""") }
    }
}
