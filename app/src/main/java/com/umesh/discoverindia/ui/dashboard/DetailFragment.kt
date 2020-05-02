package com.umesh.discoverindia.ui.dashboard

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.umesh.discoverindia.MainActivity
import com.umesh.discoverindia.R
import com.umesh.discoverindia.databinding.FragmentDashboardBinding
import com.umesh.discoverindia.modals.Post
import com.umesh.discoverindia.repository.postRepository
import com.umesh.discoverindia.ui.commons.PostClickListener
import com.umesh.discoverindia.ui.commons.PostsViewModel
import com.umesh.discoverindia.ui.home.PostAdapter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class DetailFragment : Fragment() {

    private lateinit var viewModel: PostsViewModel
    private  var downloaded: LiveData<List<Post>> = MutableLiveData()

    private lateinit var binding: FragmentDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Timber.d("Created")
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_dashboard,container, false)
        viewModel = (requireActivity() as MainActivity).viewModel

        updateView()

        if (viewModel.selectedPosition.value == null) {
            binding.targetPost.visibility = View.GONE
        }

        GlobalScope.launch {
            (downloaded as MutableLiveData).value = postRepository.downloaded()
            Timber.d("Downloaded posts: ${downloaded.value?.size}")
        }

        return binding.root
    }

    private fun updateView() {
        viewModel.selectedPosition.observe(this.viewLifecycleOwner, Observer { itposition ->
            viewModel.posts.value?.get(itposition)?.let {
                binding.targetPost.visibility = View.VISIBLE
                binding.like.text = requireContext().getString(R.string.likes_display, it.points)
                binding.title.text = it.title
                setDownloadState(it)
                Glide.with(binding.image).load(it.img).dontAnimate()
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
                    .into(binding.image)
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

    private fun setDownloadState(it:Post){
        if (it.downloaded){
            binding.downloadButton.setColorFilter(Color.BLUE)
        }else{
            binding.downloadButton.setColorFilter(Color.GRAY)
        }
    }

}