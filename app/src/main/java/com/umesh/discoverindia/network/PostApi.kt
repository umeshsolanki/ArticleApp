package com.umesh.discoverindia.network

import com.umesh.discoverindia.modals.PostsGallery
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface PostApi {

    @GET("/3/gallery/{category}/{sort}/all/{page}?showViral=true&mature=false")
    fun getPosts(@Path("category") category:String,
                      @Path("sort") sort:String,
//                      @Path("window") win:String,
                      @Path("page") page:Int,
                      @Query("access_token") token:String
        ):Observable<PostsGallery>

}