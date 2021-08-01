package com.afur.flutterhtmltopdf

import android.app.Activity
import android.os.Build
import android.print.PdfPrinter
import android.print.PrintAttributes
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebSettings
import java.io.*


class HtmlToPdfConverter {

    interface Callback {
        fun onSuccess(filePath: String)
        fun onFailure()
    }

    fun convert(filePath: String, activity: Activity, callback: Callback) {
        val webView = WebView(activity.applicationContext)
        val htmlContent = File(filePath).readText(Charsets.UTF_8)

        

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        webView.getSettings().setDomStorageEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); 


        File cacheDir = activity.getCacheDir();

         if (cacheDir != null) {
            String appCachePath = cacheDir.getAbsolutePath();
            webView.getSettings().setDatabaseEnabled(true);
            webView.getSettings().setAppCacheEnabled(true);
            webView.getSettings().setDatabasePath(appCachePath);
            webView.getSettings().setAppCachePath(appCachePath);
        }

    
        webView.getSettings().setAllowFileAccess(true);
        webView.loadDataWithBaseURL(null, htmlContent, "text/HTML", "UTF-8", null)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                createPdfFromWebView(webView, activity, callback)
            }
        }
    }

    fun createPdfFromWebView(webView: WebView, activity: Activity, callback: Callback) {
        val path = activity.applicationContext.filesDir
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            val attributes = PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setResolution(PrintAttributes.Resolution("pdf", "pdf", 600, 600))
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS).build()

            val printer = PdfPrinter(attributes)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val adapter = webView.createPrintDocumentAdapter(temporaryDocumentName)

                printer.print(adapter, path, temporaryFileName, object : PdfPrinter.Callback {
                    override fun onSuccess(filePath: String) {
                        callback.onSuccess(filePath)
                    }

                    override fun onFailure() {
                        callback.onFailure()
                    }
                })
            }
        }
    }

    companion object {
        const val temporaryDocumentName = "TemporaryDocumentName"
        const val temporaryFileName = "TemporaryDocumentFile.pdf"
    }
}