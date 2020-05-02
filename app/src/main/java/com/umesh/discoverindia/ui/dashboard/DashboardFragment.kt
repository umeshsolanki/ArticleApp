package com.umesh.discoverindia.ui.dashboard

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.umesh.discoverindia.MainActivity
import com.umesh.discoverindia.R
import com.umesh.discoverindia.databinding.FragmentDetailBinding
import com.umesh.discoverindia.modals.Post
import com.umesh.discoverindia.ui.commons.PostClickListener
import com.umesh.discoverindia.ui.commons.PostsViewModel
import timber.log.Timber

class DashboardFragment : Fragment() {

    private lateinit var viewModel: PostsViewModel

    private lateinit var binding: FragmentDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
        sharedElementEnterTransition = TransitionInflater.from(context)
            .inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (requireActivity() as MainActivity).apply {
            supportActionBar?.hide()
        }
        Timber.d("Created")
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_detail,container, false)
        viewModel = (requireActivity() as MainActivity).viewModel

        updateView()
        viewModel.syncDownloads()

        loadRecent()
        return binding.root
    }

    private fun updateView() {
        binding.savedPosts.layoutManager = StaggeredGridLayoutManager(2,
            StaggeredGridLayoutManager.VERTICAL)
        viewModel.selectedPosition.observe(this.viewLifecycleOwner, Observer { itposition ->
                viewModel.posts.value?.get(itposition)?.let {
                binding.recentContainer.fullScroll(View.FOCUS_UP)
                binding.like.text = requireContext().getString(R.string.likes_display, it.points)
                binding.title.text = it.title
                setDownloadState(it)
                Glide.with(binding.parallaxImage).load(it.img).dontAnimate()
                    .listener(object:RequestListener<Drawable>{
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            startPostponedEnterTransition()
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            startPostponedEnterTransition()
                            return false
                        }
                    })
            .into(binding.parallaxImage)
            }
        })

        binding.downloadButton.setOnClickListener {
            viewModel.selectedPosition.value?.apply {
                viewModel.posts.value?.get(this)?.let {
                    it.downloaded = !it.downloaded
                    setDownloadState(it)
                    (requireActivity() as PostClickListener).update(it,this)
                }
            }
        }
    }


    private fun loadRecent(){
        viewModel.downloaded.observe(this.viewLifecycleOwner, Observer { savedPosts ->
            Timber.d("Downloaded posts: ${savedPosts.size}")
            val recent3 = savedPosts.takeLast(3).toMutableList()
            binding.savedPosts.adapter = RecentAdapter(this,
                recent3,
                requireActivity() as PostClickListener
            )
            binding.savedPosts.adapter?.let {
                it.notifyDataSetChanged()
//                return@Observer
            }

         })
    }


    override fun onStop() {
        super.onStop()
        (requireActivity() as MainActivity).apply {
            supportActionBar?.show()
        }
    }

    private fun setDownloadState(it:Post){
        if (it.downloaded){
            binding.downloadButton.setColorFilter(Color.BLUE)
        }else{
            binding.downloadButton.setColorFilter(Color.GRAY)
        }
    }

}