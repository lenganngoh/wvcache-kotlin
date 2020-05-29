package gohleng.apps.wvcache.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * The Database class extending Room Database with applicable entities
 * @property invoke
 * @property buildDatabase
 */
@Database(entities = [Cache::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cacheAccess(): CacheAccess

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "database.db")
            .build()
    }
}