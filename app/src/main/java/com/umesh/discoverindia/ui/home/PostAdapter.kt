package com.umesh.discoverindia.ui.home

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.umesh.discoverindia.R
import com.umesh.discoverindia.databinding.PostLayoutBinding
import com.umesh.discoverindia.databinding.PostOnlyTextLayoutBinding
import com.umesh.discoverindia.modals.Post
import com.umesh.discoverindia.ui.commons.PostClickListener
import com.umesh.discoverindia.ui.commons.PostsViewModel
import kotlinx.android.synthetic.main.post_layout.view.*
import timber.log.Timber


class PostAdapter(val context:Context,
                  var posts:MutableList<Post>,
                  val clickListener: PostClickListener,
                  val viewModel: PostsViewModel)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var binding:PostLayoutBinding
    lateinit var textBinding:PostOnlyTextLayoutBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            IMAGE -> {
                binding = DataBindingUtil.inflate(LayoutInflater.from(context),
                    R.layout.post_layout, parent, false)
                ImageViewHolder(binding)
            }
            else -> {
                textBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(context), R.layout.post_only_text_layout, parent,
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
        if (posts.lastIndex == position){
            viewModel.fetchNext(posts.size)
        }
        if (holder is PostAdapter.ImageViewHolder) {
            holder.bind(position, clickListener)
        }else if (holder is PostAdapter.TextViewHolder){
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
            Timber.d("Type: ${post.type}")
            if (post.downloaded){
                itemView.downloadButton.setColorFilter(Color.BLUE)
            }else{
                itemView.downloadButton.setColorFilter(Color.GRAY)
            }
            itemView.downloadButton.setOnClickListener {
                post.downloaded = !post.downloaded
                posts[position] = post
                clickListener.update(post,position)
            }
            Glide.with(context).load(post.img).placeholder(R.drawable.loading).into(image)
            title.text = post.title
            likes.text = context.getString(R.string.likes_display,post.points)
            likes.setOnClickListener {
                clickListener.likePost(post)
            }
            image.setOnClickListener {
                clickListener.postClicked(position,it)
            }
            title.setOnClickListener {
                clickListener.postClicked(position,it)
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
            likes.text = context.getString(R.string.likes_display,post.points)
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