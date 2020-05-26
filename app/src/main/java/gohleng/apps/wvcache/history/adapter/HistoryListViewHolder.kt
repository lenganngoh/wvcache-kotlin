package gohleng.apps.wvcache.history.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import gohleng.apps.wvcache.R
import gohleng.apps.wvcache.db.Cache

class HistoryListViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.viewholder_history_list, parent, false)) {

        private var txtURL: TextView? = null
        private var txtDate: TextView? = null
        private var imgWeb: ImageView? = null

        init {
            txtURL = itemView.findViewById(R.id.txtURL)
            txtDate = itemView.findViewById(R.id.txtDate)
            imgWeb = itemView.findViewById(R.id.imgWeb)
        }

        fun bind(cache: Cache) {
            txtURL?.text = cache.title
            txtDate?.text = cache.datetime
            if (cache.image != null) {
                imgWeb?.setImageURI(Uri.parse(cache.image))
            }
        }
}