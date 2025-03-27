package com.sanchezgrupo.supersanchezlite

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.sanchezgrupo.supersanchezlite.ui.theme.SuperSanchezLiteTheme

class MainActivity : ComponentActivity() {
    private var webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightNavigationBars = true

        setContent {
            SuperSanchezLiteTheme(darkTheme = false) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WebViewScreen(
                        url = "https://supersanchez.com",
                        modifier = Modifier.padding(innerPadding),
                        onWebViewCreated = { webView = it }
                    )
                }
            }
        }
    }

    override fun onBackPressed() {
        webView?.let {
            if (it.canGoBack()) {
                it.goBack()
            } else {
                super.onBackPressed()
            }
        } ?: super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        webView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        webView?.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        webView?.destroy()
        webView = null
    }
}

@Composable
fun WebViewScreen(url: String, modifier: Modifier = Modifier, onWebViewCreated: (WebView) -> Unit) {
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    var isFirstLoad by remember { mutableStateOf(true) }
    val context = LocalContext.current

    val prefs: SharedPreferences = context.getSharedPreferences("WebViewCache", Context.MODE_PRIVATE)
    val lastUpdateTime = prefs.getLong("lastUpdate", 0L)
    val currentTime = System.currentTimeMillis()
    val twentyFourHoursInMillis = 24 * 60 * 60 * 1000
    val shouldReloadFromNetwork = (currentTime - lastUpdateTime) >= twentyFourHoursInMillis

    fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    fun updateLastUpdateTime() {
        prefs.edit().putLong("lastUpdate", System.currentTimeMillis()).apply()
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                SwipeRefreshLayout(context).apply {
                    setOnRefreshListener {
                        isRefreshing = true
                        isLoading = true
                        hasError = false
                        findViewById<WebView>(android.R.id.content)?.apply {
                            clearCache(true)
                            loadUrl(url)
                            updateLastUpdateTime()
                        }
                    }
                }.apply {
                    addView(WebView(context).apply {
                        id = android.R.id.content
                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                                super.onPageStarted(view, url, favicon)

                            }

                            override fun onPageCommitVisible(view: WebView?, url: String?) {
                                super.onPageCommitVisible(view, url)
                                // Ocultar el indicador de carga o refresco cuando la página estática comienza a cargar
                                if (isFirstLoad) {
                                    isLoading = false
                                    isFirstLoad = false
                                }
                                if (isRefreshing) {
                                    isRefreshing = false
                                }
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                hasError = false
                                if (shouldReloadFromNetwork) {
                                    updateLastUpdateTime()
                                }
                            }

                            override fun onReceivedError(
                                view: WebView?,
                                errorCode: Int,
                                description: String?,
                                failingUrl: String?
                            ) {
                                super.onReceivedError(view, errorCode, description, failingUrl)
                                isLoading = false
                                isRefreshing = false
                                hasError = true
                            }
                        }

                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            setAllowFileAccess(true)
                            cacheMode = android.webkit.WebSettings.LOAD_CACHE_ELSE_NETWORK
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            setSupportZoom(false)
                            builtInZoomControls = false
                            mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        }

                        if (shouldReloadFromNetwork && isNetworkAvailable()) {
                            loadUrl(url)
                        } else {
                            loadUrl(url)
                        }
                    })
                }.also {
                    onWebViewCreated(it.findViewById(android.R.id.content))
                }
            },
            update = { swipeRefreshLayout ->
                swipeRefreshLayout.isRefreshing = isRefreshing
            },
            modifier = Modifier.fillMaxSize()
        )

        if (isLoading && !hasError && isFirstLoad) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_round),
                    contentDescription = "Super Sánchez Lite",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(bottom = 16.dp)
                )
                CircularProgressIndicator(
                    color = Color(0xFFE53012)
                )
            }
        }

        if (hasError) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (!isNetworkAvailable()) {
                        "Sin conexión a internet. Por favor, verifica tu red e inténtalo de nuevo."
                    } else {
                        "Error al cargar la página. Por favor, inténtalo de nuevo."
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Button(
                    onClick = {
                        if (isNetworkAvailable()) {
                            isLoading = true
                            hasError = false
                            val webView = (context as? ComponentActivity)?.findViewById<WebView>(android.R.id.content)
                            webView?.apply {
                                clearCache(true)
                                loadUrl(url)
                                updateLastUpdateTime()
                            }
                        }
                    }
                ) {
                    Text("Retry")
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            val webView = (context as? ComponentActivity)?.findViewById<WebView>(android.R.id.content)
            webView?.stopLoading()
        }
    }
}

@Composable
fun WebViewPreview() {
    SuperSanchezLiteTheme(darkTheme = false) {
        WebViewScreen("https://www.supersanchez.com") {}
    }
}