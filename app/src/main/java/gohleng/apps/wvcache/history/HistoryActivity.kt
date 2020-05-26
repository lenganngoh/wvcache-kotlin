package gohleng.apps.wvcache.history

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import gohleng.apps.wvcache.R
import gohleng.apps.wvcache.db.CacheViewModel
import gohleng.apps.wvcache.history.adapter.HistoryListAdapter
import gohleng.apps.wvcache.history.listener.RecyclerViewListener
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : AppCompatActivity(), RecyclerViewListener {

    private val adapter = HistoryListAdapter(this)
    private lateinit var cacheViewModel: CacheViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        initViewModel()
        initCacheRecyclerView()
    }

    private fun initViewModel() {
        cacheViewModel = ViewModelProviders.of(this).get(CacheViewModel::class.java)
    }

    private fun initCacheRecyclerView() {
        rvHistory.layoutManager = LinearLayoutManager(this)
        rvHistory.adapter = adapter

        cacheViewModel.getAll().observe(this,
            Observer { t ->
                adapter.setCacheList(t!!)
            })
    }

    override fun onClick(position: Int) {
        val intent = Intent().apply {
            putExtra("cacheId", adapter.getItemId(position))
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}