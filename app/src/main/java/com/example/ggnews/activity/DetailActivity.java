package com.example.ggnews.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ggnews.Constants;
import com.example.ggnews.R;

public class DetailActivity extends AppCompatActivity {
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_activity);

      Intent intent = getIntent();
      String url = intent.getStringExtra(Constants.NEWS_DETAIL_URL_KEY);

      WebView myWebView = (WebView) findViewById(R.id.wv_webView);
      myWebView.loadUrl(url);
    }
}
//
