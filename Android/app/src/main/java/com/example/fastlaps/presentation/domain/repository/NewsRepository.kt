
import com.prof18.rssparser.RssParser
import com.prof18.rssparser.model.RssChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NewsRepository {
    private val rssParser = RssParser()

    suspend fun getF1News(language: String): List<NewsModel> = withContext(Dispatchers.IO) {
        try {
            val rssUrl = when (language) {
                "es" -> "https://lat.motorsport.com/rss/f1/news/"
                else -> "https://www.motorsport.com/rss/f1/news/"
            }

            val rssChannel: RssChannel = rssParser.getRssChannel(rssUrl)

            rssChannel.items.map { item ->
                NewsModel(
                    title = item.title ?: "",
                    description = item.description ?: "",
                    url = item.link ?: "",
                    date = item.pubDate ?: "",
                    imageUrl = item.image ?: ""
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}