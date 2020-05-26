package gohleng.apps.wvcache.db

import android.content.Context

class DatabaseManager {
    companion object {
        private var db: AppDatabase? = null

        fun initDatabase(context: Context): AppDatabase? {
            val tempUserDb = db
            if (tempUserDb == null) {
                db = AppDatabase.invoke(context)
            }
            return db
        }

        fun getDatabase(): AppDatabase? {
            return db
        }
    }
}