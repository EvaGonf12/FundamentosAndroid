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
const val EXTRA_TOPIC_NAME = "TOPIC_NAME"

class PostsActivity : AppCompatActivity(),
    PostsFragment.PostsInteractionListener,
    CreatePostFragment.CreatePostInteractionListener {

    var topicId: String = ""
    var topicName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)

        this.topicId = intent.getStringExtra(EXTRA_TOPIC_ID)
        this.topicName = intent.getStringExtra(EXTRA_TOPIC_NAME)

        if (isFirsTimeCreated(savedInstanceState)) {
            var bundle = Bundle()
            // Se le pasa el id del topic
            bundle.putString(EXTRA_TOPIC_ID, this.topicId)
            bundle.putString(EXTRA_TOPIC_NAME, this.topicName)
            var postsFragment = PostsFragment()
            postsFragment.arguments = bundle
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, postsFragment)
                .commit()
        }
    }

    override fun onCreatePost() {
        var bundle = Bundle()
        // Se le pasa el id del topic y el título
        bundle.putString(EXTRA_TOPIC_ID, this.topicId)
        bundle.putString(EXTRA_TOPIC_NAME, this.topicName)
        var createPostFragment = CreatePostFragment()
        createPostFragment.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, createPostFragment)
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