package com.ubimax.sample.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.ubimax.sample.R;


/**
 *
 **/
public class WebViewActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        String title = getIntent().getStringExtra("title");
        setTitleView(title);
        WebView webView = findViewById(R.id.linkWeb);
        String url = getIntent().getStringExtra("url");
        logTag = "-------开发者页面--webview " + url;
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri destinationURI = Uri.parse(url);
                String scheme = destinationURI.getScheme().toLowerCase();
                if (scheme.equals("http") || scheme.equals("https")) {
                    return super.shouldOverrideUrlLoading(view, url);
                } else {
                    goActivity(WebViewActivity.this, url, null);
                    return true;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
        });
        webView.loadUrl(url);
    }

    @Override
    protected void initView() {
        super.initView();
    }

    public void goActivity(Context context, String url, String replacePgk) {
        try {
            Intent intent = getIntent(url);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1)
                if (!TextUtils.isEmpty(replacePgk))
                    intent.putExtra(Intent.EXTRA_REFERRER, Uri.parse("android-app://" + replacePgk));
            context.startActivity(intent);
        } catch (Exception e) {
        }
    }

    public static Intent getIntent(String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(url));
        return intent;
    }


}
