package gohleng.apps.wvcache.db

import android.content.Context

/**
 * The database manager which exposes the database creation to the app
 */
class DatabaseManager {
    companion object {
        private var db: AppDatabase? = null

        // Calls the Room Database class that will invoke creation
        fun initDatabase(context: Context): AppDatabase? {
            val tempUserDb = db
            if (tempUserDb == null) {
                db = AppDatabase.invoke(context)
            }
            return db
        }

        // Returns the current existing database
        fun getDatabase(): AppDatabase? {
            return db
        }
    }
}