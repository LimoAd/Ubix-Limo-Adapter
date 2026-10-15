package com.ubimax.sample.express;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ubimax.UMTSDK;
import com.ubimax.api.UMTAdConfig;
import com.ubimax.api.UMTAdLoader;
import com.ubimax.api.bean.AdnReadyStatus;
import com.ubimax.api.bean.BidLossReasons;
import com.ubimax.api.bean.UMTAdSession;
import com.ubimax.api.bean.UMTAdnDetailInfo;
import com.ubimax.api.bean.UMTAdnInfo;
import com.ubimax.api.bean.UMTAdnListener;
import com.ubimax.api.bean.UMTBiddingFeedBack;
import com.ubimax.api.bean.UMTConstant;
import com.ubimax.api.bean.UMTErrorInfo;
import com.ubimax.api.bean.UMTSplashAd;
import com.ubimax.api.bean.UMTSplashAdListener;
import com.ubimax.sample.AdConstants;
import com.ubimax.sample.R;
import com.ubimax.sample.base.BaseActivity;
import com.ubimax.sample.base.SwitchButton;
import com.ubimax.sample.utils.ScreenUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhanao.feng
 * @date 2024/8/1 14:20
 * @description
 */
public class SplashActivity extends BaseActivity {

    private final String TAG = "_ubidding_pub_splash";
    //开发者强引用，SDK内部弱引用
    private final UMTAdnListener mAdnListener = new UMTAdnListener() {
        @Override
        public void onAdnRequest(UMTAdnDetailInfo umtAdnDetailInfo) {
            Log.e(TAG, "onAdnRequest=" + umtAdnDetailInfo.toString());
            setLog("AdnRequest=" + umtAdnDetailInfo.getAdnName() + ", adnSlotId=" + umtAdnDetailInfo.getAdnSlotId());
        }

        @Override
        public void onAdnLoaded(UMTAdnDetailInfo umtAdnDetailInfo) {
            Log.e(TAG, "onAdnLoaded=" + umtAdnDetailInfo.toString());
            setLog("AdnLoaded=" + umtAdnDetailInfo.getAdnName() + ", adnSlotId=" + umtAdnDetailInfo.getAdnSlotId() + ", ecpm=" + umtAdnDetailInfo.getEcpm());
        }

        @Override
        public void onAdnLoadFail(UMTAdnDetailInfo umtAdnDetailInfo, UMTErrorInfo umtErrorInfo) {
            Log.e(TAG, "onAdnLoadFail=" + umtAdnDetailInfo.toString() + ", err=" + umtErrorInfo.toString());
            setLog("AdnLoadFail=" + umtAdnDetailInfo.getAdnName() + ", adnSlotId=" + umtAdnDetailInfo.getAdnSlotId() + ", errmsg=" + umtErrorInfo.getMsg());
        }
    };
    private boolean mForceGoMain = false;
    private boolean isTimerFinish = false;
    private SwitchButton logoSwitchButton, showOrientationSwitch;
    private FrameLayout container;
    private UMTAdLoader adLoader;
    private UMTSplashAd umtSplashAd;
    private UMTAdSession<UMTSplashAd> mUMTAdSession;
    private EditText loadTimeOutEdt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        heightEdt = findViewById(R.id.heightEdt);
        loadTimeOutEdt = findViewById(R.id.loadTimeOutEdt);
        setTitleView("开屏广告");
        heightEdt.setText((ScreenUtils.getRealScreenHeightDp(this) - 100) + "");
        logoSwitchButton = findViewById(R.id.logoSwitch);
        logoSwitchButton.setSwitchButtonState(true);
        logoSwitchButton.setOnSwitchButtonChangeListener(new SwitchButton.SwitchButtonChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanger(boolean isOn) {
                if (!isOn) {
                    heightEdt.setText(ScreenUtils.getRealScreenHeightDp(SplashActivity.this) + "");
                } else {
                    heightEdt.setText((ScreenUtils.getRealScreenHeightDp(SplashActivity.this) - 100) + "");
                }
            }
        });
        showOrientationSwitch = findViewById(R.id.showOrientationSwitch);
        showOrientationSwitch.setSwitchButtonState(false);
        slotIdEdt.setText(AdConstants.splashId);
        adLoader = UMTSDK.createAdLoader();
    }

    protected void initView() {
        container = findViewById(R.id.ad_container);
        initClickListener();
    }    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.e(TAG, "timeout ");
            boolean isHasAvailableAd = false;
            if (mUMTAdSession != null) {
                //onAdLoaded同步触发
                UMTSplashAd availableAd = mUMTAdSession.getAvailableAd();
                ////建议：方式1，无操作，onAdLoaded被触发直接展示，
                //
                if (availableAd != null) {
                    isHasAvailableAd = true;
                    setLog("已超时，存在可用广告");
                    Log.e(TAG, "timeout  has availableAd");
                    //////方式2，返回值也允许直接调用展示，多次展示内部会过滤保证只有一次展示
                    //if (isLoadAndShow) {
                    //    showAd(availableAd);
                    //}
                }
            }
            //无可用广告，直接结束广告流程
            if (!isHasAvailableAd) {
                setLog("已超时，不存在可用广告");
                Log.e(TAG, "timeout  has not availableAd");
                openMainActivity();
            }
        }
    };

    public void loadAd() {
        //应用层倒计时
        String loadTimeoutMillis = loadTimeOutEdt.getText().toString();
        if (TextUtils.isEmpty(loadTimeoutMillis)) {
            loadTimeoutMillis = "3000";
            loadTimeOutEdt.setText(loadTimeoutMillis);
        }
        mHandler.removeMessages(1);
        mHandler.sendEmptyMessageDelayed(1, Long.parseLong(loadTimeoutMillis));
        //
        int widthDp = ScreenUtils.getScreenWidthDip(this);
        //100dp demo 布局文件中 底部 logo 图片高度,开发者根据自身实际情况设置
        int heightDp = ScreenUtils.getRealScreenHeightDp(this) - 100;
        UMTAdConfig adConfig = new UMTAdConfig.Builder()
                .setSlotId(slotIdEdt.getText().toString())
                .fetchAdTimeOut(5000)
                .setWidth(widthDp)
                .setHeight(heightDp)
                .build();


        adLoader.loadSplashAd(this, adConfig, new UMTSplashAdListener() {
            @Override
            public void onAdStart(UMTAdSession<UMTSplashAd> umtAdSession) {
                Log.e(TAG, "onAdStart requestId:" + umtAdSession.getRequestId());
                mUMTAdSession = umtAdSession;
                setAdnListener(mUMTAdSession);
            }

            @Override
            public void onAdLoaded(UMTSplashAd splashAd) {
                mHandler.removeMessages(1);
                umtSplashAd = splashAd;
                isLoadSuc = true;
                UMTAdnInfo adnInfo = splashAd.getAdnInfo();
                Log.e(TAG, "onAdLoaded " + adnInfo.toString());
                Log.e(TAG, "is ready:" + splashAd.isValid());
                Log.e(TAG, "request id:" + splashAd.getRequestId());
                if (checkInAppBidding()) {
                    Map<String, String> extraData = new HashMap<>();
                    extraData.put(UMTBiddingFeedBack.KEY_SECOND_PRICE_CENT, "300");//次高价,单位：分
                    splashAd.notifyBiddingWin(extraData);
                    setLog(logSuc);
                    if (isLoadAndShow) {
                        showAd(splashAd);
                    }
                } else {
                    Map<String, String> extraData = new HashMap<>();
                    extraData.put(UMTBiddingFeedBack.KEY_WINNER_PRICE_CENT, "500");//竞胜方出价,单位：分
                    extraData.put(UMTBiddingFeedBack.KEY_WINNER_ADN_ID, "3");//竞胜方平台adnid ，枚举值见广告网络ADN_ID枚举值
                    /**
                     *  int FLOOR_PRICE_FILTER = 1; // 底价过滤
                     *     int LOW_PRICE          = 2; // 竞价失败
                     *     int TIMEOUT            = 3; // 超时
                     *     int MATERIAL_BLOCKED   = 4; // 素材屏蔽
                     *     int AD_EXPIRED         = 5; // 广告失效
                     *     int OTHER              = 6; // 其他
                     */
                    extraData.put(UMTBiddingFeedBack.KEY_LOSS_REASON, "" + BidLossReasons.LOW_PRICE);//竞败原因，见 BidLossReason
                    extraData.put(UMTBiddingFeedBack.KEY_WINNER_AD_SOURCE, "xx广告");//竞胜方广告主名称/来源
                    extraData.put(UMTBiddingFeedBack.KEY_WINNER_AD_TITLE, "xxx广告标题");//竞胜方广告标题
                    splashAd.notifyBiddingLoss(extraData);
                    setLog("模拟竞败");
                }
            }

            @Override
            public void onAdShow(UMTSplashAd splashAd) {
                setLog(logExposure);
                Log.e(TAG, "onAdShow");
            }

            @Override
            public void onAdClick(UMTSplashAd splashAd) {
                setLog(logClick);
                Log.e(TAG, "onAdClicked");
            }

            @Override
            public void onAdDismiss(UMTSplashAd splashAd) {
                setLog(logDismiss);
                Log.e(TAG, "onAdDismiss is skip?" + (splashAd.getDismissType() == UMTConstant.DismissType.SKIP));
                isTimerFinish = true;
                if (!mForceGoMain) {
                    openMainActivity();
                }
            }

            @Override
            public void onShowError(UMTErrorInfo errorInfo) {
                setLog(logShowError);
                Log.e(TAG, "showError:" + errorInfo.toString() + errorInfo.getAdnErrorList());
                openMainActivity();
            }

            @Override
            public void onError(UMTErrorInfo errorInfo) {
                Log.e(TAG, "onError" + errorInfo.toString());
                setLog(logError + " code:" + errorInfo.getCode() + " msg:" + errorInfo.getMsg() + " adn error list:" + errorInfo.getAdnErrorList().size());
                openMainActivity();
            }
        });
    }

    /**
     * 模拟应用内竞价
     *
     * @return
     */
    private boolean checkInAppBidding() {
        //    some app bidding
        // return new Random().nextInt(100) > 50;
        return true;
    }

    private void setAdnListener(UMTAdSession<UMTSplashAd> mUMTAdSession) {
        mUMTAdSession.setADNListener(mAdnListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //根据mForceGoMain 的值确定是否将跳转主页面
        if (mForceGoMain && isTimerFinish) {
            openMainActivity();
        }
        mForceGoMain = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stop 后 将 mForceGoMain 设置为true 标识再次 onResume 根据mForceGoMain 的值确定是否将跳转主页面
        mForceGoMain = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (umtSplashAd != null) {
            umtSplashAd.destroy();
        }
    }

    /**
     * 跳转到主页面、根据业务逻辑跳转到指定页面
     */
    private void openMainActivity() {
        setTitleView("开屏");
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            Log.e("-----openMainActivity", "----横屏转开屏");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制竖屏
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    setTitleView("开屏"); // 为了Titile部分显示正常
                }
            }, 200);
        }
        mHandler.removeMessages(1);
        container.setVisibility(View.GONE);
        container.removeAllViews();
        findViewById(R.id.appLogo).setVisibility(View.GONE);
        setLog("");
        isLoadSuc = false;
        isTimerFinish = false;
        mForceGoMain = false;
        mUMTAdSession = null;
        mHandler.removeCallbacksAndMessages(null);
        if (umtSplashAd != null) {
            umtSplashAd.destroy();
            umtSplashAd = null;
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

    }

    private void initClickListener() {
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.loadBtn) {
                    setLog(logStart);
                    isLoadAndShow = false;
                    loadAd();
                } else if (v.getId() == R.id.priceBtn) {
                    if (umtSplashAd != null) {
                        setPrice(umtSplashAd.getAdnInfo().getEcpm() + "");
                    } else {
                        setLog(logNoAd);
                    }
                } else if (v.getId() == R.id.showBtn) {
                    // 设置页面全屏显示
                    if (isLoadSuc) {
                        showAd(umtSplashAd);
                    } else {
                        setLog(logNoAd);
                    }
                } else if (v.getId() == R.id.loadAndShow) {
                    setLog(logStart);
                    isLoadAndShow = true;
                    loadAd();
                } else if (v.getId() == R.id.isValid) {
                    if (umtSplashAd != null) {
                        boolean isValid = !umtSplashAd.isValid().equals(AdnReadyStatus.ADN_NOT_READY);
                        setLog(isValid ? logAdValid : logAdUnvalid);
                    } else {
                        setLog(logNoAd);
                    }
                }
            }
        };
        setClickListener();
    }

    private void showAd(UMTSplashAd ad) {
        Log.e("----SplashUI", "showAd");
        container.setPadding(0, 0, 0, ScreenUtils.getRealScreenHeightPx(this) - ScreenUtils.dp2px(Integer.parseInt(heightEdt.getText().toString())) - (logoSwitchButton.getSwitchButtonState() ? ScreenUtils.dp2px(100) : 0));
        container.setVisibility(View.VISIBLE);
        if (showOrientationSwitch.getSwitchButtonState()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制横屏
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        findViewById(R.id.appLogo).setVisibility(logoSwitchButton.getSwitchButtonState() ? View.VISIBLE : View.GONE);
        if (ad != null) {
            ad.show(container);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!logoSwitchButton.getSwitchButtonState()) {
            heightEdt.setText(ScreenUtils.getRealScreenHeightDp(SplashActivity.this) + "");
        } else {
            heightEdt.setText((ScreenUtils.getRealScreenHeightDp(SplashActivity.this) - 100) + "");
        }
    }




}
