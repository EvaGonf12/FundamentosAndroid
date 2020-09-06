package io.keepcoding.eh_ho.posts

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.PostsRepo
import io.keepcoding.eh_ho.inflate
import kotlinx.android.synthetic.main.fragment_posts.*

class PostsFragment(topicId: String) : Fragment() {

    var postsInteractionListener: PostsInteractionListener? = null
    var topicId: String = topicId

    private val postsAdapter: PostsAdapter by lazy {
        val adapter = PostsAdapter {
            //this.postsInteractionListener?.onShowPostDetails(it)
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
        return container?.inflate(R.layout.fragment_posts)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

/*        buttonCreate.setOnClickListener {
            this.postsInteractionListener?.onCreatePost()
        }*/

        postsAdapter.setPosts(PostsRepo.posts)

        listPosts.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        listPosts.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        listPosts.adapter = postsAdapter
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
            PostsRepo.getPosts(this.topicId, it.applicationContext,
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
        when(item.itemId) {
            R.id.action_logout -> this.postsInteractionListener?.onLogout()
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