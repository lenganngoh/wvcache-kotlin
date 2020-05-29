package gohleng.apps.wvcache.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

/**
 * The view model class which gives access to the view class into the internal database classes
 */
class CacheViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: CacheRepository = CacheRepository()
    private var allCache: LiveData<List<Cache>> = repository.getAll()

    // Inserts a new item to the cache entity
    fun insert(cache: Cache) {
        return repository.insert(cache)
    }

    // Deletes an item from the cache entity
    fun delete(cache: Cache) {
        return repository.delete(cache)
    }

    // Gets an item from the cache entity through its id
    fun getItemById(cid: Long) : Cache {
        return repository.getItemById(cid)
    }

    // Gets all the items in the cache entity
    fun getAll() : LiveData<List<Cache>> {
        return allCache
    }

    // Gets all the items with a keyword based on the title
    fun getAllWithKeyword(key: String) : List<Cache> {
        return repository.getAllWithKeyword(key)
    }
}