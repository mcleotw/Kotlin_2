package com.example.leo.kotlin_1

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import java.net.HttpURLConnection
import java.net.URL

class WebActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        val textviewrespone = findViewById<TextView>(R.id.textview_respone)
        val buttonRequest = findViewById<Button>(R.id.button_Request)
        buttonRequest.setOnClickListener() {
            Thread() {
                val url = URL("https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invapp/InvApp?version=0.2&action=QryWinningList&invTerm=10612&appID=EINV4201801255581&UUID=TGZ5UUJoZzRNeWVVZE1iZw==")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                val inputStream = connection.inputStream
                val reader =inputStream.bufferedReader()
                var response =StringBuilder()
                while (true) {
                    val line = reader.readLine()
                    if(line == null)
                        break ;
                    response.append(line)
                }
                reader.close()
                connection.disconnect()
                runOnUiThread { textviewrespone.text = response }
            }.start()
        }
    }
    //
    fun onClick_Read(view: View) {
        val webview = findViewById<WebView>(R.id.webview)
        webview.settings.javaScriptEnabled = true
        webview.webViewClient = WebViewClient()
        webview.loadUrl("http://www.mcleo.idv.tw/doc/mcfont.htm")
    }
    fun onClick_CloseWeb(view: View) {
        finish()
    }
}
