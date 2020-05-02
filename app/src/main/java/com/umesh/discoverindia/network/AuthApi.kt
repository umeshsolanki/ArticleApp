package com.umesh.discoverindia.network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthApi {

    @POST
    @FormUrlEncoded
    fun getAuthToken(@Field("client_id") cliendId:String,
                     @Field("client_secret")secret:String,
                     @Field("refresh_token")refreshToken:String,
                     @Field("grant_type")grantType:String
                    ):Call<ResponseBody>

}