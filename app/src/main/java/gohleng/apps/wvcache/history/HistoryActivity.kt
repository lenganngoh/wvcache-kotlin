package gohleng.apps.wvcache.history

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import gohleng.apps.wvcache.R
import gohleng.apps.wvcache.db.CacheViewModel
import gohleng.apps.wvcache.history.adapter.HistoryListAdapter
import gohleng.apps.wvcache.history.listener.RecyclerViewListener
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity(), RecyclerViewListener {

    private val adapter = HistoryListAdapter(this)
    private lateinit var cacheViewModel: CacheViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        initUI()
        initViewModel()
        initCacheRecyclerView()

        searchHistory()
    }

    /**
     * Initialization the UI for its initial state
     */
    private fun initUI() {
        // Added a text watcher to the search edit text field
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                searchHistory()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
    }

    /**
     * Initialize the view model for the CacheViewModel
     */
    private fun initViewModel() {
        cacheViewModel = ViewModelProviders.of(this).get(CacheViewModel::class.java)
    }

    /**
     * Initialize the recycler view for its initial state
     */
    private fun initCacheRecyclerView() {
        // Applied LinearLayoutManager as the recycler view's layout manager and assigned history list adapter
        rvHistory.layoutManager = LinearLayoutManager(this)
        rvHistory.adapter = adapter
    }

    /**
     * Launches a coroutine scope to search the database based on the string in the edit text field
     */
    private fun searchHistory() {
        GlobalScope.launch {
            // Start search query based on the text in the search edit text field
            val cacheList = cacheViewModel.getAllWithKeyword(etSearch.text.toString())

            runOnUiThread {
                // Update the adapter based on the cache list returned by the search query
                adapter.setCacheList(cacheList)
            }
        }
    }

    /**
     * Implementation of the abstract class for the recycler view on click listener
     */
    override fun onClick(position: Int) {
        // Creation of intent to be sent back to the main activity
        val intent = Intent().apply {
            // Add extra with cacheId value based on selected adapter item
            putExtra("cacheId", adapter.getItemId(position))
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}