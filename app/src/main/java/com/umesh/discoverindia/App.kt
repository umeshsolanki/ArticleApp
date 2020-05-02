package com.umesh.discoverindia

import android.app.Application
import android.content.Context
import com.umesh.discoverindia.db.AppDatabase
import timber.log.Timber

class App : Application() {

    companion object{

        private var DB: AppDatabase?=null

        fun getDb(): AppDatabase {
            return DB as AppDatabase
        }

        fun getString(ctx:Context,key:String):String?{
            val pref = ctx.applicationContext.getSharedPreferences(Constants.API_S_PREFERENCE, Context.MODE_PRIVATE)
            return pref.getString(key,null)
        }

        fun setString(ctx:Context,key:String,value:String){
            val pref = ctx.applicationContext.getSharedPreferences(Constants.API_S_PREFERENCE, Context.MODE_PRIVATE)
            pref.edit().putString(key,value).apply()
        }
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
//        else{
//            Timber.plant()
//        }
        DB = AppDatabase.getInstance(this)
    }



}