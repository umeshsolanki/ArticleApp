package com.umesh.discoverindia.modals

import com.google.gson.annotations.SerializedName

class  PostsGallery {

    @SerializedName("data")
    var posts: MutableList<Post> = mutableListOf()

}