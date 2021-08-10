package com.afur.flutterhtmltopdf

import android.app.Activity
import android.os.Build
import android.print.PdfPrinter
import android.print.PrintAttributes
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.graphics.Bitmap
import java.io.*
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.ConsoleMessage
import android.util.Log
import android.view.View;



class HtmlToPdfConverter {

    interface Callback {
        fun onSuccess(filePath: String)
        fun onFailure()
    }

    /**
     * Receive message from webview and pass on to native.
     */
    class JSBridge(val webView: WebView,val activity: Activity,val callback: Callback){

        @JavascriptInterface
        fun showMessageInNative(message:String){

            print("JSBridge - showMessageInNative");
            //Toast.makeText(context,message, Toast.LENGTH_LONG).show()
            //editTextInput.setText(message)
            createPdfFromWebView(webView, activity, callback)
        }


        fun createPdfFromWebView(webView: WebView, activity: Activity, callback: Callback) {

            print("createPdfFromWebView");

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


    fun convert(filePath: String, activity: Activity, callback: Callback) {
        val webView = WebView(activity.applicationContext)
        val htmlContent = File(filePath).readText(Charsets.UTF_8)

        lateinit var webView2: WebView

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //webView.setWebContentsDebuggingEnabled(true);
        }


        webView.webChromeClient = object : WebChromeClient() {

            override fun onConsoleMessage(message: ConsoleMessage): Boolean {
                Log.d("MyApplication", "${message.message()} -- From line " +
                        "${message.lineNumber()}")

                if(message.message() == "load-done" && webView2 != null){
                    //createPdfFromWebView(webView2, activity, callback)

                    val handler = Handler()
                    handler.postDelayed({
                        // do something after 1000ms
                        createPdfFromWebView(webView2, activity, callback)
                    }, 1000)

                }

                return true
            }
        }

        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);

        

        webView.getSettings().setDomStorageEnabled(true);
        //webView.getSettings().setAppCacheMaxSize(1024*1024*8);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);





        //webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);


       
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDatabasePath(activity.getFilesDir().getAbsolutePath() + "/databases");
        webView.getSettings().setAppCachePath(activity.getFilesDir().getAbsolutePath() + "/cache");

        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);

        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // chromium, enable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }


        /*webView?.run {
            settings.javaScriptEnabled = true
            addJavascriptInterface(JSBridge(webView, activity, callback),"JSBridge")
            post {
                loadUrl("file:///android_asset/index.html")
            }
        }*/

        //webView.addJavascriptInterface(JSBridge(webView, activity, callback),"JSBridge")




        //webView.loadDataWithBaseURL(null, htmlContent, "text/HTML", "UTF-8", null)




        webView.webViewClient = object : WebViewClient() {


            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                // Page loading started
                // Do something
                // textview.setText("Page Loading Started ...")
                //view.loadUrl("javascript:replace('"+htmlContent+"');");

                //var js: String = "(function() {document.body.innerHTML = '"+ htmlContent +"';})();"

                //view.loadUrl("javascript:"+js);

                print("onPageStarted");

                super.onPageStarted(view, url,favicon)
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                print("onPageFinished");

                /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    view.evaluateJavascript("javascript:replace(String.raw`" + htmlContent + "`);", null)
                }else{
                    view.loadUrl("javascript:replace(String.raw`"+htmlContent+"`);");
                }*/

                webView2 = view;


                //view.loadUrl("javascript:replace(String.raw`"+htmlContent+"`);");
                //view.loadUrl("javascript:startRender();");

                //webView.loadUrl("javascript:startRender();");


                /*webView.post(Runnable {
                        //view.loadUrl("javascript:replace(`"+htmlContent+"`);");
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            webView.evaluateJavascript("javascript:replace(`" + htmlContent + "`);", null)
                        }else{
                            webView.loadUrl("javascript:replace(`"+htmlContent+"`);");
                        }
                })*/


                /*activity.runOnUiThread(object : Runnable() {
                    @Override
                    fun run() {
                        view.loadUrl("javascript:replace(`"+htmlContent+"`);");
                    }
                })*/

               /* view.post(Runnable {
                        //view.loadUrl("javascript:replace(`"+htmlContent+"`);");
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            view.evaluateJavascript("javascript:replace(`" + htmlContent + "`);", null)
                        }else{
                            view.loadUrl("javascript:replace(`"+htmlContent+"`);");
                        }
                })*/

                //view.loadUrl("javascript:MathJax.Hub.Queue(['Typeset',MathJax.Hub]);");

                //createPdfFromWebView(view, activity, callback)

                   /* val handler = Handler()
                    handler.postDelayed({
                        // do something after 1000ms
                        createPdfFromWebView(view, activity, callback)
                    }, 2000)*/

                
            }
        }

        //webView.loadUrl("file:///android_asset/index-2.html")
        webView.loadDataWithBaseURL("file:///android_asset/", htmlContent, "text/HTML", "UTF-8", null)
    }

    fun createPdfFromWebView(webView: WebView, activity: Activity, callback: Callback) {

        print("createPdfFromWebView");

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

    private fun doubleEscapeTeX(s: String): String {
        var t: String = ""
        var end:Int = s.length
        for (i in 0 until end) {
            if (s.get(i) === '\'') t += '\\'
            if (s.get(i) !== '\n') t += s.get(i)
            if (s.get(i) === '\\') t += "\\"
        }
        return t
    }


}