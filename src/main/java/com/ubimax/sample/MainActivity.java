package com.ubimax.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobads.tools.ToolsActivity;
import com.ubimax.sample.express.BannerActivity;
import com.ubimax.sample.express.InterstitialActivity;
import com.ubimax.sample.express.RewardActivity;
import com.ubimax.sample.express.SplashActivity;
import com.ubimax.sample.nativead.NativeHome;
import com.ubimax.sample.utils.ComplianceDialog;
import com.ubimax.sample.utils.ScreenUtils;

/**
 * @author zhanao.feng
 * @date 2024/7/9 16:14
 * @description
 */
public class MainActivity extends Activity {

    ComplianceDialog mComplianceDialog;
    private LinearLayout mAdTypeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window window = this.getWindow();
        // 更改状态栏颜色为黑色
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        showComplianceDialog();
    }

    private void showComplianceDialog() {
        dismissComplianceDialog();
        mComplianceDialog = new ComplianceDialog(this, new ComplianceDialog.OnDialogListener() {
            @Override
            public void onAgree() {
                //UMTDebugger.showDebugEntranceUI(MainActivity.this);
                UMTApplication.callInitSdkAfterPrivacyAgree();
                initView();
            }

            @Override
            public void onDisagree() {

            }
        });
        mComplianceDialog.show(this);
    }

    private void dismissComplianceDialog() {
        if (mComplianceDialog != null) {
            mComplianceDialog.dismiss();
        }
    }

    private void initView() {
        TextView titleFL = findViewById(R.id.titleTv);
        int topMargin = ScreenUtils.getRealScreenHeightPx(this) - ScreenUtils.getScreenHeight(this) - ScreenUtils.getStatusHeight(this);
        if (topMargin <= 0) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) titleFL.getLayoutParams();
            titleFL.setLayoutParams(params);
            topMargin = ScreenUtils.getRealScreenHeightPx(this) - ScreenUtils.getScreenHeight(this);
        }
        titleFL.setPadding(0, topMargin, 0, 0);
        titleFL.getLayoutParams().height = ScreenUtils.dp2px(63) + topMargin;

        mAdTypeList = findViewById(R.id.item_container);
        LayoutInflater inflater = getLayoutInflater();
        bindButton(inflater, R.mipmap.ubix_splash, "开屏广告", SplashActivity.class);
        bindButton(inflater, R.mipmap.ubix_feed, "信息流", NativeHome.class);
        bindButton(inflater, R.mipmap.ubix_splash, "插屏广告", InterstitialActivity.class);
        bindButton(inflater, R.mipmap.ubix_splash, "激励视频广告", RewardActivity.class);
        bindButton(inflater, R.mipmap.ubix_splash, "横幅广告", BannerActivity.class);
        bindButton(inflater, R.mipmap.ubix_splash, "百度测试工具", ToolsActivity.class);

    }

    // 广告助手
    private void bindButton(LayoutInflater inflater, int iconId, String name,
                            final Class<? extends Activity> clz) {
        boolean showDivider = true;
        View btn = inflater.inflate(R.layout.ad_type_list_item, mAdTypeList, false);
        TextView textView = btn.findViewById(R.id.item_name);
        textView.setText(name);
        ImageView icon = btn.findViewById(R.id.left_icon);
        icon.setImageResource(iconId);
        View divider = btn.findViewById(R.id.divider);
        divider.setVisibility(showDivider ? View.VISIBLE : View.GONE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, clz);
                startActivity(intent);
            }
        });
        mAdTypeList.addView(btn);
    }


    @Override
    public void onBackPressed() {
        // 弹窗打开期间：先回退 WebView 历史，不能退才走默认行为
        if (mComplianceDialog != null && mComplianceDialog.canGoBack()) {
            mComplianceDialog.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissComplianceDialog();
    }
}
