package io.keepcoding.eh_ho.posts

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.keepcoding.eh_ho.*
import io.keepcoding.eh_ho.data.Topic
import io.keepcoding.eh_ho.data.TopicsRepo
import io.keepcoding.eh_ho.data.UserRepo
import io.keepcoding.eh_ho.login.LoginActivity

const val TRANSACTION_CREATE_POST = "create_post"
const val EXTRA_TOPIC_ID = "TOPIC_ID"

class PostsActivity : AppCompatActivity(),
    PostsFragment.PostsInteractionListener,
    CreatePostFragment.CreatePostInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)

        val topicId: String = intent.getStringExtra(EXTRA_TOPIC_ID) ?: ""
        val topic: Topic? = TopicsRepo.getTopic(topicId)

        if (isFirsTimeCreated(savedInstanceState))
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, PostsFragment(topicId))
                .commit()
    }

    override fun onCreatePost() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, CreatePostFragment())
            .addToBackStack(TRANSACTION_CREATE_POST)
            .commit()
    }

    override fun onPostCreated() {
        supportFragmentManager.popBackStack()
    }

    override fun onLogout() {
        //Borrar datos
        UserRepo.logout(this.applicationContext)

        //Ir a actividad inicial
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}