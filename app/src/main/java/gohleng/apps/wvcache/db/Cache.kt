package gohleng.apps.wvcache.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Cache(
    @PrimaryKey(autoGenerate = true) var cid: Long? = null,
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "image") var image: String?,
    @ColumnInfo(name = "datetime") var datetime: String?
)