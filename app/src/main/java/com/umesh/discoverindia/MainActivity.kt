package com.umesh.discoverindia

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.umesh.discoverindia.modals.Post
import com.umesh.discoverindia.repository.postRepository
import com.umesh.discoverindia.ui.commons.PostClickListener
import com.umesh.discoverindia.ui.commons.PostsViewModel
import com.umesh.discoverindia.ui.home.PostAdapter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : AppCompatActivity(), PostClickListener { //,BottomNavigationView.OnNavigationItemSelectedListener {


    lateinit var viewModel: PostsViewModel
    private lateinit var navController: NavController
    private lateinit var navView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        viewModel = ViewModelProvider(this).get(PostsViewModel::class.java)

    }

    override fun postClicked(post: Post) {
        var position = -1
        viewModel.posts.value?.forEachIndexed { index, p ->
            if (p.id == post.id) {
                position = index
                return@forEachIndexed
            }
        }
        Timber.d("Index for post ${post.title} is ${position}")
        if (position >= 0){
            viewModel.selectedPosition.value = position
        }
    }

    override fun postClicked(position: Int) {
        viewModel.selectedPosition.value = position
        navController.navigate(R.id.navigation_dashboard)
    }

    override fun postClicked(position: Int, sharedImageView: View) {
        viewModel.selectedPosition.value = position
        val extras = FragmentNavigatorExtras(sharedImageView to getString(R.string.detailTransition))
        navController.navigate(R.id.navigation_dashboard,null,null,extras)
    }

    override fun delete(post: Post) {
        GlobalScope.launch {
            postRepository.delete(post)
        }
    }

    override fun likePost(post: Post) {
        Timber.d("Meme: %s is liked",post.title)
//        navController.navigate(R.id.navigation_dashboard)
    }

    override fun update(post: Post,position:Int) {
        lifecycleScope.launch {
            Timber.d("Persist into storage: ${post.id}")
            viewModel.posts.value?.set(position, post)
            viewModel.updatedPosition.postValue(position)
            postRepository.update(post)
            viewModel.syncDownloads()
        }
    }

    override fun update(post: Post) {
        lifecycleScope.launch {
            var position = -1
            viewModel.posts.value?.forEachIndexed { index, p ->
                if (p.id == post.id) {
                    position = index
                    return@forEachIndexed
                }
            }
            Timber.d("Index for post ${post.title} is $position")
            if (position<0) {
                return@launch
            }
            viewModel.posts.value?.set(position, post)
            viewModel.updatedPosition.postValue(position)
            viewModel.selectedPosition.value?.let {
                if (it == position){
                    Timber.d("Reload detail view")
                    viewModel.selectedPosition.postValue(position)
                }
            }
            postRepository.update(post)
            viewModel.syncDownloads()
        }
    }
}
