package gohleng.apps.wvcache.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import gohleng.apps.wvcache.db.Cache
import gohleng.apps.wvcache.history.listener.RecyclerViewListener

/**
 * The recycler view adapter class for the history list
 */
class HistoryListAdapter(private val recyclerViewListener: RecyclerViewListener) : RecyclerView.Adapter<HistoryListViewHolder>() {

    private var cache: List<Cache> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return HistoryListViewHolder(inflater, parent)
    }

    override fun getItemCount(): Int {
        return cache.size
    }

    override fun getItemId(position: Int): Long {
        return cache[position].cid!!
    }

    override fun onBindViewHolder(holder: HistoryListViewHolder, position: Int) {
        val post: Cache = cache[position]
        holder.itemView.setOnClickListener{
            // Sets the interface that will correctly pass the on click attempt to the calling activity
            recyclerViewListener.onClick(position)
        }
        holder.bind(post)
    }

    fun setCacheList(cache: List<Cache>) {
        this.cache = cache
        notifyDataSetChanged()
    }
}