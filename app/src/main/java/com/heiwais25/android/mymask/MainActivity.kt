package com.heiwais25.android.mymask

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.MailTo
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    private final val REQUEST_FIND_LOCATION = 1;

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        val tag: String = "MyApplication";
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        permissionCheck();
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val webView: WebView = findViewById(R.id.webview);
        // Check if the key event was the Back button and if there's history
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event)
    }

    private fun initWebView() {
        val webView: WebView = findViewById(R.id.webview);

        webView.settings.javaScriptEnabled = true;
        webView.settings.javaScriptCanOpenWindowsAutomatically = true;
        webView.settings.allowFileAccessFromFileURLs = true;
        webView.settings.saveFormData = false;
        webView.settings.savePassword = false;
        webView.settings.allowFileAccess = true
        webView.settings.useWideViewPort = true;
        webView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN;
        webView.settings.domStorageEnabled = true;
        webView.settings.setGeolocationEnabled(true);

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                url: String
            ): Boolean {
                if (url.startsWith("mailto:")) {
                    val mt = MailTo.parse(url);
                    val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", mt.to, null))
                    try {
                        startActivity(intent);
                        view?.reload();

                    } catch (ex: ActivityNotFoundException) {
                        Toast.makeText(applicationContext, "이메일 어플리케이션이 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }

                    return true;
                }
                return false;
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback?
            ) {
                super.onGeolocationPermissionsShowPrompt(origin, callback)
                callback?.invoke(origin, true, false)
            }
        }
        webView.loadUrl("https://mymask.info");
    }

    fun permissionCheck() {
        if (
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            initWebView();
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FIND_LOCATION
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_FIND_LOCATION) {
            initWebView();
        }
    }

}
