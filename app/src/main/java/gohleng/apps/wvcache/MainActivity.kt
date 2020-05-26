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

    private val historyPickCode: Int = 1001

    private lateinit var cacheViewModel: CacheViewModel
    private var cachedUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DatabaseManager.initDatabase(this)

        setContentView(R.layout.activity_main)

        initUI()
        initViewModel()
        initWebView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == historyPickCode) {
            prepResultForDisplay(data!!.getLongExtra("cacheId", -1));
        }
    }

    private fun prepResultForDisplay(cacheId: Long) {
        if (cacheId > 0) {
            GlobalScope.launch {
                val cache = cacheViewModel.getItemById(cacheId)
                cachedUrl = cache.title

                runOnUiThread {
                    if (cachedUrl != null) {
                        imgCache.visibility = View.VISIBLE
                        imgCache?.setImageURI(Uri.parse(cache.image))
                        loadWebView(cachedUrl!!)
                    }
                }
            }
        }
    }

    private fun initUI() {
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

    private fun initViewModel() {
        cacheViewModel = ViewModelProviders.of(this).get(CacheViewModel::class.java)
    }

    private fun initWebView() {
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

    private fun loadWebView(url: String) {
        wvHome.loadUrl(url)
    }

    private fun loadWebView() {
        wvHome.loadUrl(getUrl())
    }

    private fun getUrl(): String? {
        return if (etURL.text.toString().contains("https://") ||
            etURL.text.toString().contains("http://")
        ) {
            etURL.text.toString()
        } else {
            "https://" + etURL.text.toString()
        }
    }

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

    private fun saveImageToExternalStorage(bitmap: Bitmap): Uri {
        val path = baseContext.getExternalFilesDir(null)?.absolutePath
        val file = File(path, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
            toast("Image saved successful.")
        } catch (e: IOException) {
            e.printStackTrace()
            toast("Error to save image.")
        }
        return Uri.parse(file.absolutePath)
    }

    private fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}