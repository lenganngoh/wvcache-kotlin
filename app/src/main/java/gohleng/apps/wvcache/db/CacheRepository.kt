package gohleng.apps.wvcache.db

import android.os.AsyncTask
import androidx.lifecycle.LiveData

/**
 * The repository class binding view model to the access class
 */
class CacheRepository {

    private var cacheAccess: CacheAccess
    private var allCache: LiveData<List<Cache>>

    init {
        val db: AppDatabase? = DatabaseManager.getDatabase()

        cacheAccess = db?.cacheAccess()!!
        allCache = cacheAccess.getAll()
    }

    fun insert(cache: Cache) {
        InsertCacheAsyncTask(cacheAccess).execute(cache)
    }

    fun delete(cache: Cache) {
        DeleteCacheAsyncTask(cacheAccess).execute(cache)
    }

    fun getItemById(cid: Long) : Cache {
        return cacheAccess.getAllByItemId(cid)
    }

    fun getAll() : LiveData<List<Cache>> {
        return allCache
    }

    fun getAllWithKeyword(key: String) : List<Cache> {
        return cacheAccess.getAllWithKeyword(key)
    }

    // AsyncTask handing the insertion of items into the database
    private class InsertCacheAsyncTask(val cacheAccess: CacheAccess) : AsyncTask<Cache, Unit, Unit>() {
        override fun doInBackground(vararg p0: Cache?) {
            cacheAccess.insert(p0[0]!!)
        }
    }

    // AsyncTask handing the deletion of items from the database
    private class DeleteCacheAsyncTask(val cacheAccess: CacheAccess) : AsyncTask<Cache, Unit, Unit>() {
        override fun doInBackground(vararg p0: Cache?) {
            cacheAccess.delete(p0[0]!!)
        }
    }
}