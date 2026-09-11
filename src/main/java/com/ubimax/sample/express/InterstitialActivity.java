package com.ubimax.sample.express;


import static com.ubimax.sample.utils.ScreenUtils.getScreenWidth;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.ubimax.api.bean.UMTAdSession;
import com.ubimax.api.bean.UMTAdnDetailInfo;
import com.ubimax.api.bean.UMTAdnInfo;
import com.ubimax.api.bean.UMTAdnListener;
import com.ubimax.api.bean.UMTInterstitialAd;
import com.ubimax.api.bean.UMTInterstitialAdListener;
import com.ubimax.sample.AdConstants;
import com.ubimax.UMTSDK;
import com.ubimax.api.UMTAdConfig;
import com.ubimax.api.UMTAdLoader;
import com.ubimax.api.bean.AdnReadyStatus;
import com.ubimax.api.bean.UMTErrorInfo;
import com.ubimax.sample.R;
import com.ubimax.sample.base.BaseActivity;
import com.ubimax.sample.base.SwitchButton;
import com.ubimax.sample.utils.ScreenUtils;

/**
 * 插屏广告
 * 京媒 插屏 宽高比为3：2 或者2：3
 **/
public class InterstitialActivity extends BaseActivity {

    private final String TAG = "_ubidding_pub_interstitial_template";
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
    private UMTAdLoader adLoader;
    private UMTInterstitialAd interstitialAd;
    private SwitchButton showOrientationSwitch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);
        setTitleView("插屏广告");
        showOrientationSwitch = findViewById(R.id.showOrientationSwitch);
        showOrientationSwitch.setSwitchButtonState(false);
        showOrientationSwitch.setOnSwitchButtonChangeListener(new SwitchButton.SwitchButtonChangeListener() {
            @Override
            public void onChanger(boolean isOn) {
                if (isOn) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制横屏
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制竖屏
                }
            }
        });
    }

    @Override
    protected void initView() {
        super.initView();
        initClickListener();
        slotIdEdt.setText(AdConstants.interstitialId);
        adLoader = UMTSDK.createAdLoader();
    }

    public void loadAd() {
        int width = ScreenUtils.px2dip(InterstitialActivity.this, getScreenWidth(this)) - 50;
        int height = width * 3 / 2; //   京东插屏 需要特定宽高比 2：3或者3：2，负责加载会报尺寸错误
        UMTAdConfig adConfig = new UMTAdConfig.Builder()
                .setSlotId(slotIdEdt.getText().toString())
                .fetchAdTimeOut(5000)
                .setWidth(width)
                .setHeight(height)
                .build();
        adLoader.loadInterstitialAd(this, adConfig, new UMTInterstitialAdListener() {
            @Override
            public void onAdStart(UMTAdSession<UMTInterstitialAd> umtAdSession) {
                Log.e(TAG, "onAdStart requestId:" + umtAdSession.getRequestId());
                setAdnListener(umtAdSession);
            }

            @Override
            public void onAdLoaded(UMTInterstitialAd interstitialAd) {
                InterstitialActivity.this.interstitialAd = interstitialAd;
                isLoadSuc = true;
                UMTAdnInfo adnInfo = interstitialAd.getAdnInfo();
                Log.e(TAG, "onAdLoaded " + adnInfo.toString());
                Log.e(TAG, "is ready:" + interstitialAd.isValid());
                Log.e(TAG, "request id:" + interstitialAd.getRequestId());
                setLog(logSuc);
                if (isLoadAndShow) {
                    InterstitialActivity.this.interstitialAd.show(InterstitialActivity.this);
                }
            }

            @Override
            public void onAdShow(UMTInterstitialAd interstitialAd) {
                Log.e(TAG, "onAdExposure");
                setLog(logExposure);
            }

            @Override
            public void onAdClick(UMTInterstitialAd interstitialAd) {
                setLog(logClick);
                Log.e(TAG, "onAdClicked");
            }

            @Override
            public void onAdDismiss(UMTInterstitialAd interstitialAd) {
                Log.e(TAG, "onAdDismiss");
                setLog(logDismiss + "\n");
                isLoadSuc = false;
            }

            @Override
            public void onShowError(UMTErrorInfo errorInfo) {
                setLog(logError + " code:" + errorInfo.getCode() + " msg:" + errorInfo.getMsg() + "adn error list:" + errorInfo.getAdnErrorList().size());
                Log.e(TAG, errorInfo.toString());
            }

            @Override
            public void onError(UMTErrorInfo errorInfo) {
                setLog(logError + " code:" + errorInfo.getCode() + " msg:" + errorInfo.getMsg() + "adn error list:" + errorInfo.getAdnErrorList().size());
                Log.e(TAG, errorInfo.toString());
            }
        });
    }

    private void setAdnListener(UMTAdSession mUMTAdSession) {
        mUMTAdSession.setADNListener(mAdnListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
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
                    if (interstitialAd != null && interstitialAd.getAdnInfo() != null && isLoadSuc) {
                        setPrice(interstitialAd.getAdnInfo().getEcpm() + "");
                    } else {
                        setLog(logNoAd);
                    }
                } else if (v.getId() == R.id.showBtn) {
                    // 设置页面全屏显示
                    if (isLoadSuc) {
                        interstitialAd.show(InterstitialActivity.this);
                    } else {
                        setLog(logNoAd);
                    }
                } else if (v.getId() == R.id.loadAndShow) {
                    setLog(logStart);
                    isLoadAndShow = true;
                    loadAd();
                } else if (v.getId() == R.id.isValid) {
                    Log.e(TAG, " isValid: " + (interstitialAd != null));
                    if (interstitialAd != null && isLoadSuc) {
                        boolean isValid = !interstitialAd.isValid().equals(AdnReadyStatus.ADN_NOT_READY);
                        setLog(isValid ? logAdValid : logAdUnvalid);
                    } else {
                        setLog(logNoAd);
                    }
                }
            }
        };
        setClickListener();
    }

}
