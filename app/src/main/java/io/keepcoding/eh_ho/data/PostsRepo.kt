package io.keepcoding.eh_ho.data

import android.content.Context
import com.android.volley.NetworkError
import com.android.volley.Request
import com.android.volley.ServerError
import com.android.volley.toolbox.JsonObjectRequest
import io.keepcoding.eh_ho.BuildConfig
import io.keepcoding.eh_ho.R
import org.json.JSONObject
import java.nio.charset.Charset


object PostsRepo {
    val posts: MutableList<Post> = mutableListOf()

    fun getPosts(
        topicId: String,
        context: Context,
        onSuccess: (List<Post>) -> Unit,
        onError: (RequestError) -> Unit
    ) {
        val request = JsonObjectRequest(
            Request.Method.GET,
            ApiRoutes.getPosts(),
            null,
            {
                val list = Post.parsePostsList(it)
                val listFiltered = mutableListOf<Post>()
                // Filtar lista por topic id
                list.forEach {
                    if (it.topicId == topicId) {
                        listFiltered.add(it)
                    }
                }
                onSuccess(listFiltered)
            },
            {
                it.printStackTrace()
                val requestError =
                    if (it is NetworkError)
                        RequestError(it, messageResId = R.string.error_no_internet)
                    else
                        RequestError(it)
                onError(requestError)
            }
        )

        ApiRequestQueue
            .getRequestQueue(context)
            .add(request)
    }

    fun addPost(
        context: Context,
        model: CreatePostModel,
        onSuccess: (CreatePostModel) -> Unit,
        onError: (RequestError) -> Unit
    ) {
        val username = UserRepo.getUsername(context)
        val request = PostRequest(
            Request.Method.POST,
            ApiRoutes.createPost(),
            model.toJson(),
            {
                onSuccess(model)
            },
            {
                it.printStackTrace()

                val requestError =
                    if (it is ServerError && it.networkResponse.statusCode == 422) {
                        val body = String(it.networkResponse.data, Charsets.UTF_8)
                        val jsonError = JSONObject(body)
                        val errors = jsonError.getJSONArray("errors")
                        var errorMessage = ""

                        for (i in 0 until errors.length()) {
                            errorMessage += "${errors[i]} "
                        }

                        RequestError(it, message = errorMessage)

                    } else if (it is NetworkError)
                        RequestError(it, messageResId = R.string.error_no_internet)
                    else
                        RequestError(it)

                onError(requestError)
            },
            username
        )

        ApiRequestQueue
            .getRequestQueue(context)
            .add(request)
    }

    fun getPost(id: String): Post? = posts.find { it.id == id }

}