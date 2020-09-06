package io.keepcoding.eh_ho.data

import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import io.keepcoding.eh_ho.BuildConfig

data class Post(
    val id: String = UUID.randomUUID().toString(),
    val user: String = "",
    val title: String = "",
    val topicId: String = "",
    val topicTitle: String = "",
    val date: Date = Date(),
    val imageURL: String = "",
    val reads: String = ""
) {

    companion object {
        fun parsePostsList(response: JSONObject): List<Post> {
            val postObjectList = response.getJSONArray("latest_posts")

            val posts = mutableListOf<Post>()

            for (i in 0 until postObjectList.length()) {
                val parsedPosts = parsePost(postObjectList.getJSONObject(i))
                // Saber si el topic_id es el del topic seleccionado
                posts.add(parsedPosts)
            }

            return posts
        }

        fun parsePost(jsonObject: JSONObject): Post {
            val date = jsonObject.getString("created_at")
                .replace("Z", "+0000")

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
            val dateFormatted = dateFormat.parse(date) ?: Date()

            var imgURL = BuildConfig.DiscourseUrl + jsonObject.getString("avatar_template").replace("{size}", "200", false)

            return Post(
                id = jsonObject.getInt("id").toString(),
                user = jsonObject.getString("name"),
                title = jsonObject.getString("raw"),
                topicId = jsonObject.getString("topic_id").toString(),
                topicTitle = jsonObject.getString("topic_title"),
                date = dateFormatted,
                imageURL = imgURL,
                reads = jsonObject.getString("reads").toString()
            )
        }
    }
    val MINUTE_MILLIS = 1000L * 60
    val HOUR_MILLIS = MINUTE_MILLIS * 60
    val DAY_MILLIS = HOUR_MILLIS * 24
    val MONTH_MILLIS = DAY_MILLIS * 30
    val YEAR_MILLIS = MONTH_MILLIS * 12

    data class TimeOffset(val amount: Int, val unit: Int)

    /**
     * Fecha de creación de topico '01/01/2020 10:00:00'
     * @param Date Fecha de consulta '01/01/2020 11:00:00'
     * @return { unit: "Hora", amount: 1 }
     **/
    fun getTimeOffset(dateToCompare: Date = Date()): TimeOffset {
        val current = dateToCompare.time
        val diff = current - this.date.time

        val years = diff / YEAR_MILLIS
        if (years > 0) return TimeOffset(
            years.toInt(),
            Calendar.YEAR
        )

        val months = diff / MONTH_MILLIS
        if (months > 0) return TimeOffset(
            months.toInt(),
            Calendar.MONTH
        )

        val days = diff / DAY_MILLIS
        if (days > 0) return TimeOffset(
            days.toInt(),
            Calendar.DAY_OF_MONTH
        )

        val hours = diff / HOUR_MILLIS
        if (hours > 0) return TimeOffset(
            hours.toInt(),
            Calendar.HOUR
        )

        val minutes = diff / MINUTE_MILLIS
        if (minutes > 0) return TimeOffset(
            minutes.toInt(),
            Calendar.MINUTE
        )

        return TimeOffset(0, Calendar.MINUTE)
    }

}