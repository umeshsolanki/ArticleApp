package com.umesh.discoverindia.ui.commons

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umesh.discoverindia.modals.Post
import com.umesh.discoverindia.modals.PostsGallery
import com.umesh.discoverindia.repository.postRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class PostsViewModel : ViewModel() {

    private var fetchRequest: Disposable? = null
    var downloaded: LiveData<List<Post>> = MutableLiveData()
    var selectedPosition:MutableLiveData<Int> = MutableLiveData()
    var updatedPosition:MutableLiveData<Int> = MutableLiveData()
    var fetchAfter:Int = 0
    var page:Int = 1
    var error:MutableLiveData<Throwable>? = MutableLiveData()
    var loading:MutableLiveData<Boolean?> = MutableLiveData()
    val posts = MutableLiveData<MutableList<Post>>()



    fun fetchNext(lastPosition:Int){
        fetchAfter = lastPosition
        fetch(++page)
    }

    fun syncDownloads(){
            (downloaded as MutableLiveData).postValue(postRepository.downloaded())
    }


    init {
        Timber.d("Initializing VM")
        viewModelScope.launch {
            postRepository.fromDb(page).let {
                Timber.d("Database result is loaded")
                if (it.isNotEmpty()) {
                    Timber.d("Db has posts")
                    posts.value = it
                    loading.value = false
                    page = it.size/postRepository.pageSize
                }else{
                    Timber.d("Db doesn't have post, Load from network!!")
                    fetch(page)
                }
            }
        }
    }

    fun fetch(page:Int) {
        loading.value?.let {
            if (it){
                Timber.d("Already loading page!!")
                return
            }
        }
        loading.value = true
        Timber.d("Fetching page: $page")
        fetchRequest = postRepository.fetchRequest(page).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onSuccess, this::onError)
    }

    private fun onSuccess(gallery:PostsGallery){
        postRepository.persist(gallery.posts)
        loading.value = false
        if (posts.value == null) {
            posts.value = gallery.posts
        }else{
            posts.value?.addAll(gallery.posts)
            posts.value = posts.value
        }
    }

    private fun onError(throwable: Throwable){
        loading.value = false
        throwable.printStackTrace()
        error?.value = throwable

    }

}