package gohleng.apps.wvcache

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import gohleng.apps.wvcache.db.Cache
import gohleng.apps.wvcache.db.CacheViewModel
import gohleng.apps.wvcache.db.DatabaseManager
import gohleng.apps.wvcache.history.HistoryActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class MainActivity : AppCompatActivity() {

    private val historyPickCode: Int = 1001 // Request code for secondary activity

    private lateinit var cacheViewModel: CacheViewModel // Late initialization of the view model
    private var cachedUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialization of the database
        DatabaseManager.initDatabase(this)

        setContentView(R.layout.activity_main)

        initUI()
        initViewModel()
        initWebView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Handles the result of the secondary activity
        if (resultCode == Activity.RESULT_OK && requestCode == historyPickCode) {
            prepResultForDisplay(data!!.getLongExtra("cacheId", -1));
        }
    }

    /**
     * Launches a coroutine scope when a correct cacheId is returned by the secondary activity
     * @param cacheId
     */
    private fun prepResultForDisplay(cacheId: Long) {
        // Checking to make sure cacheIdd is existing and not null
        if (cacheId > 0) {
            // Launch coroutine scope when cacheId value is correct
            GlobalScope.launch {
                val cache = cacheViewModel.getItemById(cacheId)
                cachedUrl = cache.title

                runOnUiThread {
                    // Checking for cache if found in the database with a correct value
                    if (cachedUrl != null) {
                        // Display image view that will show the cached image saved in the database
                        imgCache.visibility = View.VISIBLE
                        imgCache?.setImageURI(Uri.parse(cache.image))
                        // Start loading the url
                        loadWebView(cachedUrl!!)
                    }
                }
            }
        }
    }

    /**
     * Initialize the UI for its initial state
     */
    private fun initUI() {
        // Add on click listener to buttons
        btnGo.setOnClickListener {
            loadWebView()
        }

        btnCapture.setOnClickListener {
            if (cachedUrl == null) {
                return@setOnClickListener
            }

            val bitmap = captureScreen(wvHome)
            if (bitmap != null) {
                val uri = saveImageToExternalStorage(bitmap)

                GlobalScope.launch {
                    val cache = Cache(
                        null,
                        cachedUrl,
                        uri.toString(),
                        Calendar.getInstance().time.toString()
                    )
                    cacheViewModel.insert(cache)
                }
            }
        }

        btnHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivityForResult(intent, historyPickCode)
        }
    }

    /**
     * Initialize the view model for the CacheViewModel
     */
    private fun initViewModel() {
        cacheViewModel = ViewModelProviders.of(this).get(CacheViewModel::class.java)
    }

    private fun initWebView() {
        pbLoader.hide()
        wvHome.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                pbLoader.show()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                pbLoader.hide()
                cachedUrl = url
                imgCache.visibility = View.GONE
            }
        }
    }

    /**
     * Loads the web view
     * @param url
     */
    private fun loadWebView(url: String) {
        wvHome.loadUrl(url)
    }

    /**
     * Loads the web view
     */
    private fun loadWebView() {
        wvHome.loadUrl(getUrl())
    }

    /**
     * Concatenates and formats the string from etURL to a correct website url
     * @return url
     */
    private fun getUrl(): String? {
        return if (etURL.text.toString().contains("https://") ||
            etURL.text.toString().contains("http://")
        ) {
            etURL.text.toString()
        } else {
            "https://" + etURL.text.toString()
        }
    }

    /**
     * Captures the screen with correct width and height
     * @return Bitmap of the screenshot
     */
    private fun captureScreen(webView: WebView): Bitmap? {
        try {
            val bitmap = Bitmap.createBitmap(
                webView.width, webView.measuredHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            canvas.drawColor(-0x1)
            webView.draw(canvas)
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Saves bitmap to the external storage
     * @param bitmap
     * @return URI of the saved bitmap
     */
    private fun saveImageToExternalStorage(bitmap: Bitmap): Uri {
        val path = baseContext.getExternalFilesDir(null)?.absolutePath
        val file = File(path, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
            toast("Image saved successfully to the database")
        } catch (e: IOException) {
            e.printStackTrace()
            toast("Error capturing this screen")
        }
        return Uri.parse(file.absolutePath)
    }

    private fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}