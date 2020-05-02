package com.umesh.discoverindia.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.umesh.discoverindia.App
import com.umesh.discoverindia.modals.Post
import com.umesh.discoverindia.modals.PostsGallery
import com.umesh.discoverindia.network.PostApi
import com.umesh.discoverindia.network.RetrofitApi
import io.reactivex.Observable


object postRepository {

    val pageSize = 60

    fun fetchRequest(page:Int): Observable<PostsGallery> {
        return RetrofitApi.getClient().create(PostApi::class.java).
        getPosts("top", "viral", page, RetrofitApi.accessToken)
    }

    @WorkerThread
    fun fromDb(page:Int): MutableList<Post> {
        print("Load $page")
        return App.getDb().getPostsDao().getImmediately()
    }

    fun persist(post:Post){
        App.getDb().getPostsDao().save(post)
    }

    fun update(post:Post){
        App.getDb().getPostsDao().update(post)
    }

    fun persist(posts:List<Post>){
        App.getDb().getPostsDao().save(posts)
    }

    fun delete(post:Post){
        App.getDb().getPostsDao().delete(post)
    }

    fun downloaded(): List<Post> {
        return App.getDb().getPostsDao().getDownloaded()
    }


}