package com.umesh.discoverindia.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.umesh.discoverindia.modals.Post



@Dao
interface PostDao{

    @Query("select * from Post order by type desc,dateTime asc")
    fun getAll() : LiveData<MutableList<Post>>

    @Query("select * from Post  order by type desc,dateTime asc")
    fun getImmediately() : MutableList<Post>

    @Insert
    fun save(j:Post)

    @Update
    fun update(j:Post)

    @Insert
    fun save(list:List<Post>)

    @Delete
    fun delete(j:Post)

    @Query("delete from Post where id =:id")
    fun delete(id:String)

    @Query("select * from Post where title like '%'||:input||'%'")
    fun search(input:String): LiveData<List<Post>>

    @Query("select * from Post where downloaded = 1 order by type,dateTime desc")
    fun getDownloaded():List<Post>


}