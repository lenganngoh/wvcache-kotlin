package gohleng.apps.wvcache.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class CacheViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: CacheRepository = CacheRepository()
    private var allCache: LiveData<List<Cache>> = repository.getAll()

    fun insert(cache: Cache) {
        return repository.insert(cache)
    }

    fun delete(cache: Cache) {
        return repository.delete(cache)
    }

    fun getItemById(cid: Long) : Cache {
        return repository.getItemById(cid)
    }

    fun getAll() : LiveData<List<Cache>> {
        return allCache
    }
}