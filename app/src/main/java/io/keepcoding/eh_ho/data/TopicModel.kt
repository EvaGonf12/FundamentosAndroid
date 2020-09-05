package io.keepcoding.eh_ho.data

import android.net.Uri
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import io.keepcoding.eh_ho.BuildConfig
import org.json.JSONArray


data class User(
    val id: String,
    val username: String,
    val name: String,
    val img: String
){
    companion object {
        fun parseUsersList(response: JSONObject): List<User> {
            val objectList = response.getJSONObject("users")
                .getJSONArray("users")

            val users = mutableListOf<User>()

            for (i in 0 until objectList.length()) {
                val parsedTopic = parseUser(objectList.getJSONObject(i))
                users.add(parsedTopic)
            }

            return users
        }

        fun parseUser(jsonObject: JSONObject): User {
            return User(
                id = jsonObject.getInt("id").toString(),
                username = jsonObject.getString("username").toString(),
                name = jsonObject.getString("name").toString(),
                img = jsonObject.getString("avatar_template").toString()
            )
        }
    }
}

data class Topic(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val username: String = "",
    var imageURL: String = "",
    val user: String = "",
    val date: Date = Date(),
    val posts: Int = 0,
    val views: Int = 0
) {

    companion object {
        fun parseTopicsList(response: JSONObject): List<Topic> {
            val topicsObjectList = response.getJSONObject("topic_list")
                .getJSONArray("topics")

            val topics = mutableListOf<Topic>()

            val usersObjectList = response.getJSONArray("users")

            val users = mutableListOf<User>()

            for (i in 0 until usersObjectList.length()) {
                val parsedUser = User.parseUser(usersObjectList.getJSONObject(i))
                users.add(parsedUser)
            }

            for (i in 0 until topicsObjectList.length()) {
                val parsedTopic = parseTopic(topicsObjectList.getJSONObject(i))
                //val img = users.filter { (key, value) -> key == "username" && value == parsedTopic.username }
                users.forEach {
                    if (it.username == parsedTopic.user) {
                        val imgUriString = BuildConfig.DiscourseUrl + it.img.replace("{size}", "200", false)
                        parsedTopic.imageURL = imgUriString
                    }
                }
                topics.add(parsedTopic)
            }

            return topics
        }

        fun parseTopic(jsonObject: JSONObject): Topic {
            val date = jsonObject.getString("created_at")
                .replace("Z", "+0000")

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
            val dateFormatted = dateFormat.parse(date) ?: Date()

            return Topic(
                id = jsonObject.getInt("id").toString(),
                username = jsonObject.getString("last_poster_username").toString(),
                imageURL = "",
                title = jsonObject.getString("title").toString(),
                user = jsonObject.getString("last_poster_username").toString(),
                date = dateFormatted,
                posts = jsonObject.getInt("posts_count"),
                views = jsonObject.getInt("views")
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