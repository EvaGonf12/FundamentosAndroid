package io.keepcoding.eh_ho

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.keepcoding.eh_ho.data.Post
import io.keepcoding.eh_ho.data.PostsRepo
import kotlinx.android.synthetic.main.activity_posts.*

const val EXTRA_TOPIC_ID = "TOPIC_ID"

class PostsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)

        val postId: String = intent.getStringExtra(EXTRA_TOPIC_ID) ?: ""
        val post: Post? = PostsRepo.getPost(postId)

        post?.let {
            //labelTitle.text = it.title
        }

    }
}