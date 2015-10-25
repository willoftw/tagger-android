package com.bas.tagger;


import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.bas.tagger.util.Settings;

/**
 * Created by willo on 25/10/2015.
 */
public class WebViewActivity extends Activity {

    private WebView webView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(Settings.SERVERURL + "login");

    }

}