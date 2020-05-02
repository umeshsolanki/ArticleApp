package com.umesh.discoverindia

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.umesh.discoverindia.db.AppDatabase
import com.umesh.discoverindia.db.dao.PostDao
import com.umesh.discoverindia.modals.Post
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

@RunWith(AndroidJUnit4::class)
class DatabaseTesting {

    private lateinit var postsDao: PostDao
    private lateinit var db:AppDatabase

    @Before
    fun initialize(){
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        postsDao = db.getPostsDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }


    @Test
    @Throws(Exception::class)
    fun writeAndReadTesting() {
        postsDao.save(Post("test1","http://imgur.com/","title",false,
            "image/png", Date(),123))
        val allPosts = postsDao.getImmediately()
        assertThat(allPosts[0].id, equalTo("test1"))
    }

}