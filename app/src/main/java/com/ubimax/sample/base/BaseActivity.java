package com.ubimax.sample.base;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ubimax.sample.utils.ScreenUtils;
import com.ubimax.sample.R;


/**
 * 基类，公用控件，日志等声明
 */
public class BaseActivity extends AppCompatActivity {

    public final static String logStart = "广告请求开始";
    public final static String logSuc = "广告请求成功";
    public final static String logError = "广告请求失败";
    public final static String logExposure = "广告曝光成功";
    public final static String logClick = "点击广告";
    public final static String logDismiss = "广告关闭";
    public final static String logNoAd = "暂无可用广告";
    public final static String logShowError = "广告显示曝光失败";
    public final static String logRenderSucc = "广告渲染成功";
    public final static String logRenderFail = "广告渲染失败";
    public final static String logAdValid = "广告可用";
    public final static String logAdUnvalid = "广告不可用";
    public final static String logVideoReady = "视频准备完成";
    public final static String logVideoLoaded = "视频加载完成";
    public final static String logVideoStart = "视频开始播放";
    public final static String logVideoPause = "视频播放暂停";
    public final static String logVideoResume = "视频播放继续";
    public final static String logVideoFinish = "视频播放完毕";
    public final static String logVideoProgressUpdate = "视频播放中，进度：";
    public final static String logVideoError = "视频播放失败";
    protected String logTag = "";
    protected View.OnClickListener clickListener;
    protected TextView logTv;
    protected EditText slotIdEdt;
    protected EditText heightEdt;
    protected EditText widthEdt;
    protected boolean isLoadAndShow = false;
    protected boolean isLoadSuc = false;
    String beforeStr = "";
    boolean isShowSoft = false;
    private StringBuilder logSBuilder = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setTitleView(String titleStr) {
        DemoTitleView titleView = findViewById(R.id.titleView);
        titleView.setTitle(titleStr);
        int topMargin = ScreenUtils.getRealScreenHeightPx(this) - ScreenUtils.getScreenHeight(this) - ScreenUtils.getStatusHeight(this);
        if (topMargin <= 0) {
            topMargin = ScreenUtils.getRealScreenHeightPx(this) - ScreenUtils.getScreenHeight(this);
        }
        titleView.getLayoutParams().height = ScreenUtils.dp2px(63) + topMargin;
        titleView.setPadding(0, topMargin, 0, 0);
        Window window = this.getWindow();
        // 更改状态栏颜色为黑色
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.getDecorView().setBackgroundColor(getResources().getColor(R.color.main_bg));
        logTv = findViewById(R.id.logTv);
        slotIdEdt = findViewById(R.id.slotId);
        if (slotIdEdt != null) {
            initView();
            clickListener();
        }
    }

    private void setListenerToRootView() {

        final View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                boolean mKeyboardUp = isKeyboardShown(rootView);
                if (mKeyboardUp) {
                    isShowSoft = true;
                } else {
                    if (isShowSoft) {
                        hideSoft();
                        isShowSoft = false;
                    }
                }
            }
        });
    }

    private boolean isKeyboardShown(View rootView) {
        final int softKeyboardHeight = ScreenUtils.dp2px(70);
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = rootView.getBottom() - r.bottom;
        return heightDiff > softKeyboardHeight * dm.density;
    }

    private void clickListener() {
        setListenerToRootView();
        slotIdEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeStr = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    Toast.makeText(BaseActivity.this, "请输入有效广告位", Toast.LENGTH_SHORT).show();
                    slotIdEdt.setText(beforeStr);
                    slotIdEdt.setSelection(slotIdEdt.getText().length());
                }
            }
        });
        findViewById(R.id.saveImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoft();
                findViewById(R.id.saveImg).setVisibility(View.GONE);
            }
        });
        if (widthEdt != null) {
            widthEdt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    Log.e("-----widthEdt", "beforeTextChanged " + s.toString());
                    beforeStr = s.toString();
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Log.e("-----widthEdt", "onTextChanged " + s.toString());

                }

                @Override
                public void afterTextChanged(Editable s) {
                    Log.e("-----widthEdt", "afterTextChanged " + s.toString());
                    if (TextUtils.isEmpty(s.toString())) {
                        Toast.makeText(BaseActivity.this, "宽度不能为空", Toast.LENGTH_SHORT).show();
                        widthEdt.setText(beforeStr);
                        widthEdt.setSelection(widthEdt.getText().length());
                    }
                }

            });
        }

        if (heightEdt != null) {
            heightEdt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    beforeStr = s.toString();
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (TextUtils.isEmpty(s.toString())) {
                        Toast.makeText(BaseActivity.this, "高度不能为空", Toast.LENGTH_SHORT).show();
                        heightEdt.setText(beforeStr);
                        heightEdt.setSelection(heightEdt.getText().length());
                    }
                }

            });
        }
        logTv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoft();
                return false;
            }
        });
    }

    protected void initView() {

    }

    // 控件设置点击监听
    protected void setClickListener() {
        findViewById(R.id.loadAndShow).setOnClickListener(clickListener);
        findViewById(R.id.showBtn).setOnClickListener(clickListener);
        findViewById(R.id.priceBtn).setOnClickListener(clickListener);
        findViewById(R.id.loadBtn).setOnClickListener(clickListener);
        findViewById(R.id.isValid).setOnClickListener(clickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void setLog(String log) {
        logSBuilder.append(log);
        logSBuilder.append("\n");
        logTv.setText(logSBuilder.toString());
        int len = logSBuilder.toString().split("\n").length;
//        UMTLog.e("-----setLog", "size " + len);
        ScrollView scrollView = findViewById(R.id.scrollView);
        if (scrollView != null && logSBuilder.toString().split("\n").length > 3) {
            scrollView.scrollTo(0, 500 + len * 70);
        }
        hideSoft();
    }

    protected void setPrice(String price) {
        if (!TextUtils.isEmpty(price)) {
            setLog("价格 " + price);
        } else {
            setLog("价格 0");
        }
    }

    protected void hideSoft() {
        InputMethodManager inputmanger = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputmanger.hideSoftInputFromWindow(slotIdEdt.getWindowToken(), 0);
        if (slotIdEdt != null) {
            slotIdEdt.clearFocus();
        }
        if (heightEdt != null) {
            heightEdt.clearFocus();
        }
        if (widthEdt != null) {
            widthEdt.clearFocus();
        }
    }
}
