package com.ubimax.sample.utils;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

public class ComplianceDialog extends FrameLayout {
    private static final String TAG = "ComplianceDialog";
    /**
     * 用作 WebView client 占位，避免 destroy 时传 null 触发 SDK @NotNull 注解警告
     */
    private static final WebViewClient EMPTY_CLIENT = new WebViewClient();
    private static final String DEFAULT_URL = "https://ubixai.com/ubix_sdk_ubidding_privacy.html";
    public static boolean privacyAgree = false;

    private final String title;
    private final String url;
    private final OnDialogListener mOnDialogListener;

    private SafeWebView safeWebView;

    public ComplianceDialog(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.title = "隐私协议提示";
        this.url = DEFAULT_URL;
        this.mOnDialogListener = null;
        onCreate();
    }

    public ComplianceDialog(Context context) {
        this(context, null);
    }

    public ComplianceDialog(Context context, OnDialogListener dialogListener) {
        this(context, "隐私协议提示", DEFAULT_URL, new OnDialogListener() {
            @Override
            public void onAgree() {
                privacyAgree = true;
                if (dialogListener != null) dialogListener.onAgree();
            }

            @Override
            public void onDisagree() {
                privacyAgree = false;
                if (dialogListener != null) dialogListener.onDisagree();
                System.exit(0);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
    }

    public ComplianceDialog(Context context, String title, String url, OnDialogListener mOnDialogListener) {
        super(context);
        this.title = title;
        this.url = url;
        this.mOnDialogListener = mOnDialogListener;
        onCreate();
    }

    public GradientDrawable getShape(@ColorInt int color, int radius, @ColorInt int strokeColor, int strokeWidth) {
        GradientDrawable background = new GradientDrawable();
        background.setShape(GradientDrawable.RECTANGLE);
        if (color != -1) {
            background.setColor(color);
        }
        if (radius != -1) {
            float[] connerRadii = new float[]{radius, radius, radius, radius, radius, radius, radius, radius};
            background.setCornerRadii(connerRadii);
        }
        if (strokeColor != -1 && strokeWidth != -1) {
            background.setStroke(strokeWidth, strokeColor);
        }
        return background;
    }

    protected void onCreate() {
        LinearLayout rootLayout = new LinearLayout(getContext());
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        float density = getContext().getResources().getDisplayMetrics().density;
        int padding = (int) (12 * density);
        rootLayout.setPadding(padding, 0, padding, (int) (15 * density));
        ViewCompat.setBackground(rootLayout, getShape(Color.parseColor("#FFFFFD"), (int) (10 * density), -1, -1));

        RelativeLayout titleBar = new RelativeLayout(getContext());

        TextView titleView = new TextView(getContext());
        titleView.setText(title);
        titleView.setTextColor(Color.BLACK);
        titleView.setTypeface(null, Typeface.BOLD);
        titleView.setPadding(0, padding, 0, 0);
        titleView.setTextSize(18);
        titleView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        titleView.setGravity(android.view.Gravity.CENTER);

        TextView closeButton = new TextView(getContext());
        closeButton.setText("×");
        closeButton.setTextSize(20);
        closeButton.setAllCaps(false);
        closeButton.setPadding((int) (6 * density), (int) (9 * density), (int) (6 * density), 0);
        closeButton.setOnClickListener(v -> {
            if (mOnDialogListener != null) mOnDialogListener.onDisagree();
            dismiss();
        });
        RelativeLayout.LayoutParams closeButtonParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        closeButtonParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        closeButtonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        closeButton.setLayoutParams(closeButtonParams);
        titleBar.addView(titleView);
        titleBar.addView(closeButton, closeButtonParams);

        safeWebView = new SafeWebView(getContext());
        safeWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                return handleUrl(uri != null ? uri.toString() : null);
            }
        });

        LinearLayout buttonLayout = new LinearLayout(getContext());
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setWeightSum(2f);
        buttonLayout.setPadding((int) (10 * density), (int) (15 * density), (int) (10 * density), 0);

        TextView disagreeBtn = new TextView(getContext());
        disagreeBtn.setText("不同意并退出");
        disagreeBtn.setGravity(android.view.Gravity.CENTER);
        disagreeBtn.setTextColor(Color.parseColor("#80000000"));
        LinearLayout.LayoutParams disagreeBtnLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        disagreeBtnLayoutParams.rightMargin = (int) (6 * density);
        disagreeBtn.setLayoutParams(disagreeBtnLayoutParams);
        disagreeBtn.setPadding(0, (int) (15 * density), 0, (int) (15 * density));
        ViewCompat.setBackground(disagreeBtn, getShape(Color.parseColor("#FFFFFD"), (int) (80 * density), Color.parseColor("#80000000"), 2));

        TextView agreeBtn = new TextView(getContext());
        agreeBtn.setText("同意并继续");
        agreeBtn.setGravity(android.view.Gravity.CENTER);
        agreeBtn.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams agreeBtnLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        agreeBtnLayoutParams.leftMargin = (int) (6 * density);
        agreeBtn.setLayoutParams(agreeBtnLayoutParams);
        agreeBtn.setPadding(0, (int) (15 * density), 0, (int) (15 * density));
        ViewCompat.setBackground(agreeBtn, getShape(Color.parseColor("#64A6F9"), (int) (80 * density), -1, -1));

        agreeBtn.setOnClickListener(v -> {
            if (mOnDialogListener != null) mOnDialogListener.onAgree();
            dismiss();
        });

        disagreeBtn.setOnClickListener(v -> {
            if (mOnDialogListener != null) mOnDialogListener.onDisagree();
            dismiss();
        });
        buttonLayout.addView(disagreeBtn);
        buttonLayout.addView(agreeBtn);

        rootLayout.addView(titleBar, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LinearLayout.LayoutParams webViewlayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (ScreenUtils.getScreenHeight(getContext()) * 0.65), 1f);
        webViewlayoutParams.topMargin = (int) (10 * density);
        rootLayout.addView(safeWebView, webViewlayoutParams);
        rootLayout.addView(buttonLayout);
        FrameLayout rootFrameLayout = new FrameLayout(getContext());
        LayoutParams rootlayoutParams = new LayoutParams((int) (ScreenUtils.getScreenWidth(getContext()) * 0.9), ViewGroup.LayoutParams.WRAP_CONTENT);
        rootlayoutParams.gravity = android.view.Gravity.CENTER;
        rootFrameLayout.addView(rootLayout, rootlayoutParams);
        rootFrameLayout.setBackgroundColor(Color.parseColor("#B3000000"));
        rootFrameLayout.setOnClickListener(v -> {
        });
        addView(rootFrameLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        safeWebView.loadUrl(url);
    }

    /**
     * 协议页链接统一处理：http(s) 留 WebView，其他 scheme 交给系统。
     */
    private boolean handleUrl(@Nullable String url) {
        if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
            return false;
        }
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url != null ? url : ""));
            getContext().startActivity(intent);
        } catch (Exception e) {
            Log.w(TAG, "startActivity for url=" + url + " failed", e);
        }
        return true;
    }

    public void show(Activity activity) {
        if (privacyAgree) {
            if (mOnDialogListener != null) mOnDialogListener.onAgree();
            dismiss();
            return;
        }
        try {
            activity.addContentView(this, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } catch (Exception e) {
            Log.w(TAG, "show addContentView failed", e);
        }
    }

    /**
     * WebView 是否有上一页历史
     */
    public boolean canGoBack() {
        return safeWebView != null && safeWebView.canGoBack();
    }

    /**
     * 回退 WebView 一页
     */
    public void goBack() {
        if (safeWebView != null) {
            safeWebView.goBack();
        }
    }

    public void dismiss() {
        try {
            if (getParent() != null && getParent() instanceof ViewGroup) {
                ((ViewGroup) getParent()).removeView(this);
            }
            if (safeWebView != null) {
                safeWebView.destroy();
                safeWebView = null;
            }
        } catch (Exception e) {
            Log.w(TAG, "dismiss failed", e);
        }
    }

    public interface OnDialogListener {
        void onAgree();

        void onDisagree();
    }

    public static class SafeWebView extends FrameLayout {

        private WebView webView;

        public SafeWebView(Context context) {
            super(context);
            init(context);
        }

        public SafeWebView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context);
        }

        @SuppressLint("SetJavaScriptEnabled")
        private void init(Context context) {
            webView = new WebView(context);
            addView(webView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDefaultTextEncodingName("utf-8");
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            webSettings.setGeolocationEnabled(true);
            webSettings.setAllowContentAccess(false);
            webSettings.setSaveFormData(true);
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setAllowFileAccessFromFileURLs(false);
            webSettings.setAllowUniversalAccessFromFileURLs(false);
            webSettings.setMediaPlaybackRequiresUserGesture(false);
            webSettings.setLoadsImagesAutomatically(true);
            webSettings.setBlockNetworkImage(false);
            webSettings.setBlockNetworkLoads(false);
            webSettings.setSupportZoom(true);
            webSettings.setBuiltInZoomControls(true);
            webSettings.setDisplayZoomControls(false);
            webSettings.setDatabaseEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setSaveFormData(true);
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            try {
                webView.removeJavascriptInterface("searchBoxJavaBridge_");
                webView.removeJavascriptInterface("accessibility");
                webView.removeJavascriptInterface("accessibilityTraversal");
            } catch (Throwable e) {
                Log.w(TAG, "removeJavascriptInterface failed", e);
            }
        }

        public void loadUrl(String url) {
            if (webView != null) {
                try {
                    webView.loadUrl(url);
                } catch (Exception e) {
                    Log.w(TAG, "loadUrl " + url + " failed", e);
                }
            }
        }

        public boolean canGoBack() {
            return webView != null && webView.canGoBack();
        }

        public void goBack() {
            if (webView != null && webView.canGoBack()) {
                webView.goBack();
            }
        }

        public void destroy() {
            if (webView != null) {
                webView.stopLoading();
                // 断开 client/chrome client 引用（用空 client 避开 SDK @NotNull 注解警告），确保 WebView 可被 GC
                webView.setWebViewClient(EMPTY_CLIENT);
                webView.setWebChromeClient(null);
                webView.getSettings().setJavaScriptEnabled(false);
                ((ViewGroup) webView.getParent()).removeView(webView);
                webView.destroy();
                webView = null;
            }
        }

        public void setWebViewClient(WebViewClient client) {
            if (webView != null) {
                webView.setWebViewClient(client);
            }
        }
    }
}
