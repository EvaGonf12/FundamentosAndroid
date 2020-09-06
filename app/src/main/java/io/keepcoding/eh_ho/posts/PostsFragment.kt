package io.keepcoding.eh_ho.posts

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.PostsRepo
import io.keepcoding.eh_ho.data.Topic
import io.keepcoding.eh_ho.data.TopicsRepo
import io.keepcoding.eh_ho.inflate
import kotlinx.android.synthetic.main.fragment_posts.*
import kotlinx.android.synthetic.main.fragment_posts.swipeRefresh
import kotlinx.android.synthetic.main.fragment_topics.*

class PostsFragment() : Fragment() {

    var postsInteractionListener: PostsInteractionListener? = null
    var topicId: String = ""
    var topicName: String = ""

    private val postsAdapter: PostsAdapter by lazy {
        val adapter = PostsAdapter {
        }
        adapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is PostsInteractionListener)
            postsInteractionListener = context
        else
            throw IllegalArgumentException("Context doesn't implement ${PostsInteractionListener::class.java.canonicalName}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.arguments?.let {
            this.topicId = it.getString(EXTRA_TOPIC_ID).toString()
            this.topicName = it.getString(EXTRA_TOPIC_NAME).toString()
        }
        return container?.inflate(R.layout.fragment_posts)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Reaload on swipe
        swipeRefresh.setOnRefreshListener {
            this.loadPosts()
            swipeRefresh.isRefreshing = false
        }

        postsAdapter.setPosts(PostsRepo.posts)

        listPosts.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        listPosts.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        listPosts.adapter = postsAdapter
        titleTopic.text = "Topic: ${this.topicName}"
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_posts, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onResume() {
        super.onResume()
        loadPosts()
    }

    private fun loadPosts() {
        context?.let {
            TopicsRepo.getPosts(it.applicationContext, this.topicId,
                {
                    // SUCCESS
                    postsAdapter.setPosts(it)
                },
                {
                    // TODO: Manejo de errores
                    // Se recibe un elemento RequestError
                }
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> this.postsInteractionListener?.onLogout()
            R.id.action_new -> this.postsInteractionListener?.onCreatePost()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDetach() {
        super.onDetach()
        postsInteractionListener = null
    }

    interface PostsInteractionListener {
        fun onCreatePost()
        fun onLogout()
    }
}