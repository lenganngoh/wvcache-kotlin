package gohleng.apps.wvcache.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CacheAccess {
    @Query("SELECT * FROM cache WHERE cid = :cid")
    fun getAllByItemId(cid: Long) : Cache

    @Query("SELECT * FROM cache ORDER BY datetime DESC")
    fun getAll(): LiveData<List<Cache>>

    @Insert
    fun insert(cache: Cache): Long

    @Delete
    fun delete(cache: Cache)
}