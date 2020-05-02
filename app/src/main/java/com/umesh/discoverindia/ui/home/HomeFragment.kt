package com.umesh.discoverindia.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.umesh.discoverindia.R
import com.umesh.discoverindia.databinding.FragmentHomeBinding
import com.umesh.discoverindia.MainActivity
import com.umesh.discoverindia.ui.commons.PostClickListener
import com.umesh.discoverindia.ui.commons.PostsViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import timber.log.Timber

class HomeFragment : Fragment() {

    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var viewModel: PostsViewModel
    private lateinit var binding: FragmentHomeBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        viewModel = (requireActivity() as MainActivity).viewModel

        mLayoutManager = LinearLayoutManager(requireContext())//StaggeredGridLayoutManager(1,LinearLayoutManager.VERTICAL)
        binding.postsView.layoutManager = mLayoutManager
        binding.progressBar.isIndeterminate = true

        viewModel.error?.observe(this.viewLifecycleOwner, Observer { onError(it) })
        viewModel.loading.observe(this.viewLifecycleOwner, Observer {
            it?.let {
                binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
            }
        })

        viewModel.posts.observe(this.viewLifecycleOwner, Observer {
            if (binding.postsView.adapter == null){
                binding.postsView.adapter = PostAdapter(requireContext(),it,
                    requireActivity() as PostClickListener,viewModel)
            }else{
                binding.postsView.adapter?.notifyDataSetChanged()
            }
        })
        viewModel.updatedPosition.observe(this.viewLifecycleOwner, Observer {
            binding.postsView.adapter?.notifyItemChanged(it)
        })
        return binding.root
    }

    private fun onError(throwable: Throwable){
        throwable.printStackTrace()
        Toast.makeText(context,"Failed to load posts",Toast.LENGTH_LONG).show()
    }

}