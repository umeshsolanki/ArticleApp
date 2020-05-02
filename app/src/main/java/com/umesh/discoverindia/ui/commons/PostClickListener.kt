package com.umesh.discoverindia.ui.commons

import android.view.View
import com.umesh.discoverindia.modals.Post

interface PostClickListener {
        fun postClicked(post:Post)
        fun postClicked(position:Int)
        fun postClicked(position:Int,sharedImageView: View)
        fun update(post:Post,position:Int)
        fun update(post:Post)
        fun likePost(post: Post)
        fun delete(post: Post)
    }
