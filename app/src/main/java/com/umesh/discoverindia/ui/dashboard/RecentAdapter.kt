package com.umesh.discoverindia.ui.dashboard

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.umesh.discoverindia.R
import com.umesh.discoverindia.databinding.PostLayoutBinding
import com.umesh.discoverindia.databinding.PostOnlyTextLayoutBinding
import com.umesh.discoverindia.modals.Post
import com.umesh.discoverindia.ui.commons.PostClickListener
import com.umesh.discoverindia.ui.commons.PostsViewModel
import com.umesh.discoverindia.ui.home.PostAdapter
import kotlinx.android.synthetic.main.post_layout.view.*
import timber.log.Timber


class RecentAdapter(val fragment:Fragment,
                    var posts:MutableList<Post>,
                    val clickListener: PostClickListener)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var binding:PostLayoutBinding
    lateinit var textBinding:PostOnlyTextLayoutBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            IMAGE -> {
                binding = DataBindingUtil.inflate(LayoutInflater.from(fragment.requireContext()),
                    R.layout.post_layout, parent, false)
                ImageViewHolder(binding)
            }
            else -> {
                textBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(fragment.requireContext()), R.layout.post_only_text_layout, parent,
                    false)
                TextViewHolder(textBinding)
            }
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun getItemViewType(position: Int): Int {
        return if(posts[position].type.isNullOrBlank()) Companion.TEXT else Companion.IMAGE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RecentAdapter.ImageViewHolder) {
            holder.bind(position, clickListener)
        }else if (holder is RecentAdapter.TextViewHolder){
            holder.bind(position, clickListener)
        }
    }

    inner class ImageViewHolder(itemView: PostLayoutBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        var image:ImageView = itemView.image
        var likes:TextView = itemView.like
        var title:TextView = itemView.title

        fun bind(position: Int,clickListener: PostClickListener) {
            val post = posts[position]
            image.setImageDrawable(null)
            if (post.downloaded){
                itemView.downloadButton.setColorFilter(Color.BLUE)
            }else{
                itemView.downloadButton.setColorFilter(Color.GRAY)
            }
            itemView.downloadButton.setOnClickListener {
                post.downloaded = !post.downloaded
                posts[position] = post
                notifyItemChanged(position)
                clickListener.update(post)
            }
            Glide.with(fragment).load(post.img).placeholder(R.drawable.loading).into(image)
            title.text = post.title
            likes.text = fragment.getString(R.string.likes_display,post.points)
            likes.setOnClickListener {
                Timber.d("Like clicked ${post.title}")
                clickListener.likePost(post)
            }
            image.setOnClickListener {
                Timber.d("Image tapped ${post.title}")
                clickListener.postClicked(post)
            }
            title.setOnClickListener {
                Timber.d("Title clicked ${post.title}")
                clickListener.postClicked(post)
            }
        }
    }

    inner class TextViewHolder(itemView:PostOnlyTextLayoutBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        var likes:TextView = itemView.like
        var title:TextView = itemView.title

        fun bind(position: Int,clickListener: PostClickListener) {
            val post = posts[position]
            Timber.d("Type: ${post.type}")
            title.text = post.title
            likes.text = fragment.getString(R.string.likes_display,post.points)
            itemView.setOnClickListener {
                clickListener.postClicked(position)
            }
        }
    }

    companion object {
        const val TEXT = 0
        const val IMAGE = 1
    }
}