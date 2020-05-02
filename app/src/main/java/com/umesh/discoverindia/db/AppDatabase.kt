package com.umesh.discoverindia.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.umesh.discoverindia.db.dao.PostDao
import com.umesh.discoverindia.modals.Post

import java.io.Serializable

@Database(entities = [Post::class], version = 2,exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase(),Serializable{

    abstract fun getPostsDao() : PostDao

    companion object{

        private var INSTANCE: AppDatabase? = null

        fun getInstance(ctx:Context): AppDatabase {
            if (INSTANCE !=null)
                return INSTANCE as AppDatabase

            synchronized(AppDatabase::class){
            if (INSTANCE ==null) {
                INSTANCE = Room.databaseBuilder(ctx, AppDatabase::class.java, "app.db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries().build()
                Log.d(javaClass.simpleName, "DB initialized")
            }
            }
            return INSTANCE as AppDatabase
        }


    }

}