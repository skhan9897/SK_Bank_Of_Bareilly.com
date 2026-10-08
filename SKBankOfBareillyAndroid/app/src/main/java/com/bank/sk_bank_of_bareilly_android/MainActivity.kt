package com.bank.sk_bank_of_bareilly_android

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var loadingOverlay: RelativeLayout
    private lateinit var splashOverlay: RelativeLayout
    private lateinit var tvLoadingMessage: TextView
    private lateinit var offlineLayout: LinearLayout
    private lateinit var tvErrorDetails: TextView
    private lateinit var btnRetry: MaterialButton
    private lateinit var btnSwitchServer: MaterialButton
    private lateinit var toolbar: MaterialToolbar
    private var drawerToggle: ActionBarDrawerToggle? = null

    private var fileUploadCallback: ValueCallback<Array<Uri>>? = null
    private var currentServerUrl: String = "https://sk-bank-of-bareilly-com.onrender.com/"
    private var backPressedTime: Long = 0

    private val fileUploadLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (fileUploadCallback == null) return@registerForActivityResult
        val uris = if (result.resultCode == RESULT_OK) {
            val data = result.data
            if (data?.data != null) {
                arrayOf(data.data!!)
            } else {
                null
            }
        } else {
            null
        }
        fileUploadCallback?.onReceiveValue(uris)
        fileUploadCallback = null
    }

    inner class WebAppInterface {
        @JavascriptInterface
        fun showProcessing(message: String?) {
            runOnUiThread {
                try {
                    if (message != null && message.trim().isNotEmpty()) {
                        tvLoadingMessage.text = message
                    } else {
                        tvLoadingMessage.text = "Processing Secure Banking Request..."
                    }
                    loadingOverlay.visibility = View.VISIBLE
                } catch (ignored: Exception) {}
            }
        }

        @JavascriptInterface
        fun hideProcessing() {
            runOnUiThread {
                try {
                    loadingOverlay.visibility = View.GONE
                } catch (ignored: Exception) {}
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_main)

            // Load saved server URL preference
            val prefs = getSharedPreferences("SK_BANK_PREFS", Context.MODE_PRIVATE)
            currentServerUrl = prefs.getString("SERVER_URL", "https://sk-bank-of-bareilly-com.onrender.com/") ?: "https://sk-bank-of-bareilly-com.onrender.com/"

            drawerLayout = findViewById(R.id.drawerLayout)
            navigationView = findViewById(R.id.navigationView)
            bottomNavigation = findViewById(R.id.bottomNavigation)
            toolbar = findViewById(R.id.toolbar)

            try {
                setSupportActionBar(toolbar)
            } catch (ignored: Exception) {}

            try {
                val dt = ActionBarDrawerToggle(
                    this,
                    drawerLayout,
                    toolbar,
                    R.string.app_name,
                    R.string.app_name
                )
                drawerToggle = dt
                drawerLayout.addDrawerListener(dt)
                dt.syncState()
            } catch (ignored: Exception) {}

            webView = findViewById(R.id.webView)
            progressBar = findViewById(R.id.progressBar)
            loadingOverlay = findViewById(R.id.loadingOverlay)
            splashOverlay = findViewById(R.id.splashOverlay)
            tvLoadingMessage = findViewById(R.id.tvLoadingMessage)
            offlineLayout = findViewById(R.id.offlineLayout)
            tvErrorDetails = findViewById(R.id.tvErrorDetails)
            btnRetry = findViewById(R.id.btnRetry)
            btnSwitchServer = findViewById(R.id.btnSwitchServer)

            btnRetry.setOnClickListener {
                try {
                    offlineLayout.visibility = View.GONE
                    webView.visibility = View.VISIBLE
                    tvLoadingMessage.text = "Connecting to SK Bank Server..."
                    loadingOverlay.visibility = View.VISIBLE
                    webView.loadUrl(currentServerUrl)
                } catch (ignored: Exception) {}
            }

            btnSwitchServer.setOnClickListener {
                showServerSwitcherDialog()
            }

            setupWebView()
            setupBottomNavigation()
            setupDrawerNavigation()

            tvLoadingMessage.text = "Connecting to SK Bank Server..."
            loadingOverlay.visibility = View.VISIBLE
            webView.loadUrl(currentServerUrl)

            // Smooth 1.8-second splash screen transition
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    splashOverlay.animate()
                        .alpha(0f)
                        .setDuration(400)
                        .withEndAction {
                            splashOverlay.visibility = View.GONE
                            splashOverlay.alpha = 1f
                        }
                } catch (e: Exception) {
                    splashOverlay.visibility = View.GONE
                }
            }, 1800)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupWebView() {
        try {
            val settings = webView.settings
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            try { settings.databaseEnabled = true } catch (ignored: Exception) {}
            settings.allowFileAccess = true
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            settings.setSupportZoom(true)
            settings.builtInZoomControls = true
            settings.displayZoomControls = false

            val defaultUa = try { settings.userAgentString } catch (e: Exception) { "" } ?: ""
            settings.userAgentString = "$defaultUa SKBankAndroidApp/1.0"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
                } catch (ignored: Exception) {}
            }

            webView.addJavascriptInterface(WebAppInterface(), "AndroidBridge")

            webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    try {
                        val reqUrl = request?.url?.toString()
                        if (reqUrl != null && reqUrl.contains("/admin/")) {
                            // Prevent loading admin UI inside customer mobile app
                            return true
                        }
                    } catch (ignored: Exception) {}
                    return super.shouldOverrideUrlLoading(view, request)
                }

                override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                    try {
                        handler?.proceed()
                    } catch (ignored: Exception) {}
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                    try {
                        super.onPageStarted(view, url, favicon)
                        progressBar.visibility = View.VISIBLE
                        if (splashOverlay.visibility != View.VISIBLE) {
                            loadingOverlay.visibility = View.VISIBLE
                        }
                        offlineLayout.visibility = View.GONE
                        webView.visibility = View.VISIBLE

                        if (url != null) {
                            when {
                                url.contains("/send-money") || url.contains("/transfer") || url.contains("/payment") -> {
                                    tvLoadingMessage.text = "Processing Secure Payment..."
                                }
                                url.contains("/upi") -> {
                                    tvLoadingMessage.text = "Processing UPI Request..."
                                }
                                url.contains("/withdraw") || url.contains("/deposits") -> {
                                    tvLoadingMessage.text = "Processing Cash Transaction..."
                                }
                                url.contains("/login") || url.contains("/register") -> {
                                    tvLoadingMessage.text = "Authenticating Credentials..."
                                }
                                url.contains("/bills") || url.contains("/bill-payments") -> {
                                    tvLoadingMessage.text = "Fetching Bill Details..."
                                }
                                else -> {
                                    tvLoadingMessage.text = "Processing Secure Banking Request..."
                                }
                            }
                        }
                    } catch (ignored: Exception) {}
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    try {
                        super.onPageFinished(view, url)
                        progressBar.visibility = View.GONE
                        loadingOverlay.visibility = View.GONE
                        try { CookieManager.getInstance().flush() } catch (ignored: Exception) {}

                        val jsHideAdminAndForms = """
                            (function() {
                                try {
                                    var style = document.createElement('style');
                                    style.innerHTML = 'a[href*="/admin"], button[onclick*="/admin"], .admin-login-card, .admin-link, .admin-btn, #adminLoginBtn, .nav-item:has(a[href*="/admin"]) { display: none !important; visibility: hidden !important; }';
                                    document.head.appendChild(style);

                                    var forms = document.getElementsByTagName('form');
                                    for (var i = 0; i < forms.length; i++) {
                                        forms[i].addEventListener('submit', function() {
                                            if (window.AndroidBridge) {
                                                window.AndroidBridge.showProcessing('Processing Banking Request...');
                                            }
                                        });
                                    }
                                } catch(e) {}
                            })();
                        """.trimIndent()
                        view?.evaluateJavascript(jsHideAdminAndForms, null)

                        if (url != null) {
                            when {
                                url.contains("/customer/dashboard") -> bottomNavigation.selectedItemId = R.id.nav_home
                                url.contains("/customer/accounts") -> bottomNavigation.selectedItemId = R.id.nav_accounts
                                url.contains("/customer/send-money") || url.contains("/customer/upi") -> bottomNavigation.selectedItemId = R.id.nav_payments
                                url.contains("/customer/transactions") -> bottomNavigation.selectedItemId = R.id.nav_transactions
                                url.contains("/customer/profile") -> bottomNavigation.selectedItemId = R.id.nav_profile
                            }
                        }
                    } catch (ignored: Exception) {}
                }

                override fun onReceivedError(
                    view: WebView?,
                    errorCode: Int,
                    description: String?,
                    failingUrl: String?
                ) {
                    try {
                        super.onReceivedError(view, errorCode, description, failingUrl)
                        if (failingUrl == currentServerUrl || failingUrl == "$currentServerUrl/") {
                            showOfflineError(description)
                        }
                    } catch (ignored: Exception) {}
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    try {
                        super.onReceivedError(view, request, error)
                        if (request?.isForMainFrame == true) {
                            val desc = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                error?.description?.toString()
                            } else {
                                "Network Unavailable"
                            }
                            showOfflineError(desc)
                        }
                    } catch (ignored: Exception) {}
                }
            }

            webView.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    try {
                        progressBar.progress = newProgress
                        if (newProgress > 50) {
                            loadingOverlay.visibility = View.GONE
                        }
                        if (newProgress == 100) {
                            progressBar.visibility = View.GONE
                        } else {
                            progressBar.visibility = View.VISIBLE
                        }
                    } catch (ignored: Exception) {}
                }

                override fun onRenderProcessGone(view: WebView?, detail: RenderProcessGoneDetail?): Boolean {
                    return true
                }

                override fun onShowFileChooser(
                    webView: WebView?,
                    filePathCallback: ValueCallback<Array<Uri>>?,
                    fileChooserParams: FileChooserParams?
                ): Boolean {
                    try {
                        fileUploadCallback?.onReceiveValue(null)
                        fileUploadCallback = filePathCallback

                        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "image/*"
                        }
                        fileUploadLauncher.launch(Intent.createChooser(intent, "Select Profile Image"))
                        return true
                    } catch (e: Exception) {
                        return false
                    }
                }
            }

            webView.setDownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
                try {
                    val request = DownloadManager.Request(Uri.parse(url)).apply {
                        setMimeType(mimeType)
                        addRequestHeader("User-Agent", userAgent)
                        addRequestHeader("Cookie", CookieManager.getInstance().getCookie(url))
                        setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType))
                        setDescription("Downloading SK Bank Document...")
                        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_DOWNLOADS,
                            URLUtil.guessFileName(url, contentDisposition, mimeType)
                        )
                    }
                    val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    dm.enqueue(request)
                    Toast.makeText(applicationContext, "Downloading document to Downloads folder...", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(applicationContext, "Download failed: " + e.message, Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showOfflineError("WebView system service is initializing or disabled on this device")
        }
    }

    private fun showOfflineError(description: String?) {
        try {
            progressBar.visibility = View.GONE
            loadingOverlay.visibility = View.GONE
            webView.visibility = View.GONE
            offlineLayout.visibility = View.VISIBLE
            tvErrorDetails.text = "Unable to connect to server. Please check your internet connection and try again."
        } catch (ignored: Exception) {}
    }

    private fun setupBottomNavigation() {
        try {
            bottomNavigation.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_home -> navigateToPath("customer/dashboard")
                    R.id.nav_accounts -> navigateToPath("customer/accounts")
                    R.id.nav_payments -> navigateToPath("customer/send-money")
                    R.id.nav_transactions -> navigateToPath("customer/transactions")
                    R.id.nav_profile -> navigateToPath("customer/profile")
                }
                true
            }
        } catch (ignored: Exception) {}
    }

    private fun setupDrawerNavigation() {
        try {
            navigationView.setNavigationItemSelectedListener { item ->
                try {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } catch (ignored: Exception) {}
                when (item.itemId) {
                    R.id.menu_mode_netbanking -> navigateToPath("customer/dashboard")
                    R.id.menu_mode_paymentbank -> navigateToPath("customer/upi")

                    R.id.menu_dashboard -> navigateToPath("customer/dashboard")
                    R.id.menu_accounts -> navigateToPath("customer/accounts")
                    R.id.menu_send_money -> navigateToPath("customer/send-money")
                    R.id.menu_withdraw -> navigateToPath("customer/withdraw")
                    R.id.menu_statements -> navigateToPath("customer/statements")
                    R.id.menu_loans -> navigateToPath("customer/loans")
                    R.id.menu_fds -> navigateToPath("customer/fixed-deposits")
                    R.id.menu_cards -> navigateToPath("customer/cards")

                    R.id.menu_upi -> navigateToPath("customer/upi")
                    R.id.menu_bills -> navigateToPath("customer/bill-payments")
                    R.id.menu_beneficiaries -> navigateToPath("customer/beneficiaries")

                    R.id.menu_kyc -> navigateToPath("customer/kyc")
                    R.id.menu_profile -> navigateToPath("customer/profile")
                    R.id.menu_complaints -> navigateToPath("customer/complaints")
                    R.id.menu_server -> showServerSwitcherDialog()
                    R.id.menu_logout -> navigateToPath("auth/logout")
                }
                true
            }
        } catch (ignored: Exception) {}
    }

    private fun navigateToPath(path: String) {
        try {
            val targetUrl = if (currentServerUrl.endsWith("/")) {
                currentServerUrl + path
            } else {
                "$currentServerUrl/$path"
            }
            webView.loadUrl(targetUrl)
        } catch (ignored: Exception) {}
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        try {
            menuInflater.inflate(R.menu.main_menu, menu)
            return true
        } catch (e: Exception) {
            return super.onCreateOptionsMenu(menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        try {
            val dt = drawerToggle
            if (dt != null && dt.onOptionsItemSelected(item)) {
                return true
            }
        } catch (ignored: Exception) {}

        return when (item.itemId) {
            R.id.action_refresh -> {
                try { webView.reload() } catch (ignored: Exception) {}
                true
            }
            R.id.action_home -> {
                try { webView.loadUrl(currentServerUrl) } catch (ignored: Exception) {}
                true
            }
            R.id.action_server -> {
                showServerSwitcherDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showServerSwitcherDialog() {
        try {
            val options = arrayOf(
                "Live Cloud Server (sk-bank-of-bareilly-com.onrender.com)",
                "Local Emulator Server (10.0.2.2:8081)",
                "Custom Server URL"
            )

            AlertDialog.Builder(this)
                .setTitle("SK Bank Server Config")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> updateServerUrl("https://sk-bank-of-bareilly-com.onrender.com/")
                        1 -> updateServerUrl("http://10.0.2.2:8081/sk-bank-of-bareilly/")
                        2 -> promptCustomServerUrl()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        } catch (ignored: Exception) {}
    }

    private fun promptCustomServerUrl() {
        try {
            val input = EditText(this).apply {
                setText(currentServerUrl)
                hint = "http://your-server-ip:8080/"
            }
            AlertDialog.Builder(this)
                .setTitle("Enter Custom Server URL")
                .setView(input)
                .setPositiveButton("Connect") { _, _ ->
                    val url = input.text.toString().trim()
                    if (url.isNotEmpty()) {
                        updateServerUrl(if (url.endsWith("/")) url else "$url/")
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        } catch (ignored: Exception) {}
    }

    private fun updateServerUrl(newUrl: String) {
        try {
            currentServerUrl = newUrl
            getSharedPreferences("SK_BANK_PREFS", Context.MODE_PRIVATE)
                .edit()
                .putString("SERVER_URL", newUrl)
                .apply()

            offlineLayout.visibility = View.GONE
            webView.visibility = View.VISIBLE
            webView.loadUrl(currentServerUrl)
            Toast.makeText(this, "Connecting to $newUrl", Toast.LENGTH_SHORT).show()
        } catch (ignored: Exception) {}
    }

    override fun onBackPressed() {
        try {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
                return
            }
            if (webView.canGoBack()) {
                val currentUrl = webView.url
                if (currentUrl != null && (currentUrl.endsWith("/customer/dashboard") || currentUrl.endsWith("/login") || currentUrl == currentServerUrl || currentUrl == "$currentServerUrl/")) {
                    confirmAppExit()
                } else {
                    webView.goBack()
                }
            } else {
                confirmAppExit()
            }
        } catch (e: Exception) {
            confirmAppExit()
        }
    }

    private fun confirmAppExit() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
        } else {
            Toast.makeText(this, "Press back again to exit SK Bank", Toast.LENGTH_SHORT).show()
            backPressedTime = System.currentTimeMillis()
        }
    }
}
