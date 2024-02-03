package eu.kanade.tachiyomi.extension.vi.blogtruyen

import eu.kanade.tachiyomi.multisrc.blogtruyen.BlogTruyen
import eu.kanade.tachiyomi.network.interceptor.rateLimit

class BlogTruyenMoi : BlogTruyen("BlogTruyen", "https://blogtruyenmoi.com", "vi") {
    override val client = super.client.newBuilder()
        .rateLimit(2)
        .build()

    override fun getGenreList() = listOf(
        Genre("Action", "1"),
        Genre("Adventure", "3"),
        Genre("Comedy", "5"),
        Genre("Comic", "6"),
        Genre("Doujinshi", "7"),
        Genre("Drama", "49"),
        Genre("Ecchi", "48"),
        Genre("Event BT", "60"),
        Genre("Fantasy", "50"),
        Genre("Full màu", "64"),
        Genre("Game", "61"),
        Genre("Gender Bender", "51"),
        Genre("Harem", "12"),
        Genre("Historical", "13"),
        Genre("Horror", "14"),
        Genre("Isekai/Dị giới/Trọng sinh", "63"),
        Genre("Josei", "15"),
        Genre("Live action", "16"),
        Genre("Magic", "46"),
        Genre("manga", "55"),
        Genre("Manhua", "17"),
        Genre("Manhwa", "18"),
        Genre("Martial Arts", "19"),
        Genre("Mecha", "21"),
        Genre("Mystery", "22"),
        Genre("Nấu Ăn", "56"),
        Genre("Ngôn Tình", "65"),
        Genre("NTR", "62"),
        Genre("One shot", "23"),
        Genre("Psychological", "24"),
        Genre("Romance", "25"),
        Genre("School Life", "26"),
        Genre("Sci-fi", "27"),
        Genre("Seinen", "28"),
        Genre("Shoujo", "29"),
        Genre("Shoujo Ai", "30"),
        Genre("Shounen", "31"),
        Genre("Shounen Ai", "32"),
        Genre("Slice of life", "33"),
        Genre("Smut", "34"),
        Genre("Soft Yaoi", "35"),
        Genre("Soft Yuri", "36"),
        Genre("Sports", "37"),
        Genre("Supernatural", "38"),
        Genre("Tạp chí truyện tranh", "39"),
        Genre("Tragedy", "40"),
        Genre("Trap (Crossdressing)", "58"),
        Genre("Trinh Thám", "57"),
        Genre("Truyện scan", "41"),
        Genre("Tu chân - tu tiên", "66"),
        Genre("Video Clip", "53"),
        Genre("VnComic", "42"),
        Genre("Webtoon", "52"),
        Genre("Yuri", "59"),
    )
}
