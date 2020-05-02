package com.umesh.discoverindia.modals

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity
data class  Post(@PrimaryKey val id:String,
                 @SerializedName("link")var img:String,
                 var title:String,
                 var downloaded:Boolean,
                 var type:String?,
                 @SerializedName("datetime")
                 var dateTime: Date,
                 @SerializedName("score")var points:Int)