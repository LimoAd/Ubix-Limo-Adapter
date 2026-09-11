package com.ubimax.sample.express;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ubimax.UMTSDK;
import com.ubimax.api.UMTAdConfig;
import com.ubimax.api.UMTAdLoader;
import com.ubimax.api.bean.AdnReadyStatus;
import com.ubimax.api.bean.UMTAdSession;
import com.ubimax.api.bean.UMTAdnDetailInfo;
import com.ubimax.api.bean.UMTAdnInfo;
import com.ubimax.api.bean.UMTAdnListener;
import com.ubimax.api.bean.UMTErrorInfo;
import com.ubimax.api.bean.UMTRewardAd;
import com.ubimax.api.bean.UMTRewardAdListener;
import com.ubimax.api.bean.UMTSplashAd;
import com.ubimax.sample.AdConstants;
import com.ubimax.sample.R;
import com.ubimax.sample.base.BaseActivity;
import com.ubimax.sample.base.SwitchButton;


/**
 * 激励视频
 **/
public class RewardActivity extends BaseActivity {

    private final String TAG = "_ubidding_pub_reward";
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
    private UMTRewardAd umtRewardAd;
    private SwitchButton orientationSwitchButton;
    private UMTAdLoader adLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);
        setTitleView("激励视频");
        findViewById(R.id.viewline).setVisibility(View.VISIBLE);
        findViewById(R.id.orientationFl).setVisibility(View.VISIBLE);
        orientationSwitchButton = findViewById(R.id.showOrientationSwitch);
        orientationSwitchButton.setSwitchButtonState(false);
        orientationSwitchButton.setOnSwitchButtonChangeListener(new SwitchButton.SwitchButtonChangeListener() {
            @Override
            public void onChanger(boolean isOn) {
                if (isOn) {
                    Toast.makeText(RewardActivity.this, "横屏展示需要横屏ID", Toast.LENGTH_SHORT).show();
                    setLog("已切换为广告横屏展示，横屏展示需要横屏ID");
                } else {
                    setLog("已切换为广告竖屏展示");
                }
            }
        });
    }

    @Override
    protected void initView() {
        super.initView();
        slotIdEdt.setText(AdConstants.rewardId);
        initClickListener();
        adLoader = UMTSDK.createAdLoader();
    }

    public void loadAd() {
        UMTAdConfig adParams = new UMTAdConfig.Builder()
                .setSlotId(slotIdEdt.getText().toString())
                .build();
        adLoader.loadRewardAd(RewardActivity.this, adParams, new UMTRewardAdListener() {
            @Override
            public void onAdStart(UMTAdSession<UMTRewardAd> umtAdSession) {
                Log.e(TAG, "onAdStart requestId:" + umtAdSession.getRequestId());
                setAdnListener(umtAdSession);
            }

            @Override
            public void onAdLoaded(UMTRewardAd rewardAd) {
                umtRewardAd = rewardAd;
                UMTAdnInfo adnInfo = rewardAd.getAdnInfo();
                Log.e(TAG, "onAdLoaded " + adnInfo.toString());
                Log.e(TAG, "is ready:" + umtRewardAd.isValid());
                Log.e(TAG, "request id:" + umtRewardAd.getRequestId());
                setLog(logSuc);
                isLoadSuc = true;
                if (isLoadAndShow) {
                    showAd();
                }
            }

            @Override
            public void onAdShow(UMTRewardAd rewardAd) {
                Log.e(TAG, "onAdShow");
                setLog("视频打开页面");
            }

            @Override
            public void onAdClick(UMTRewardAd rewardAd) {
                setLog(logClick);
                Log.e(TAG, "onAdClicked");
                Toast.makeText(getBaseContext(), "点击", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdDismiss(UMTRewardAd rewardAd) {
                Log.e(TAG, "onAdDismiss");
                setLog(logDismiss + "\n");
                isLoadSuc = false;
            }

            @Override
            public void onVideoPlayStart() {
                Log.e(TAG, "onVideoPlayStart");
                setLog("视频开始播放");
            }

            @Override
            public void onVideoPlayComplete() {
                Log.e(TAG, "onVideoPlayComplete");
                setLog("视频播放完成");
            }

            @Override
            public void onRewardVerify() {
                Log.e(TAG, "onRewardVerify");
                setLog("激励任务已完成");
            }

            @Override
            public void onVideoSkip() {
                Log.e(TAG, "onVideoSkip");
                setLog("激励任务点击跳过");
            }

            @Override
            public void onVideoPlayError(UMTErrorInfo errorInfo) {
                Log.e(TAG, "onVideoPlayError" + errorInfo.toString());
                setLog("播放失败:" + errorInfo.getMsg());
            }

            @Override
            public void onError(UMTErrorInfo errorInfo) {
                Log.e(TAG, "onError" + errorInfo.toString());
                setLog("无填充:" + errorInfo);
            }
        });
    }

    private void setAdnListener(UMTAdSession mUMTAdSession) {
        mUMTAdSession.setADNListener(mAdnListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (umtRewardAd != null) {
            umtRewardAd.destroy();
        }
    }


    public void showAd() {
        if (orientationSwitchButton.getSwitchButtonState()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制竖屏
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    umtRewardAd.show(RewardActivity.this);
                }
            }, 100);
        } else {
            umtRewardAd.show(RewardActivity.this);
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
                    if (umtRewardAd != null && umtRewardAd.getAdnInfo() != null && isLoadSuc) {
                        setPrice(umtRewardAd.getAdnInfo().getEcpm() + "");
                    } else {
                        setLog(logNoAd);
                    }
                } else if (v.getId() == R.id.showBtn) {
                    // 设置页面全屏显示
                    if (isLoadSuc) {
                        showAd();
                    } else {
                        setLog(logNoAd);
                    }
                } else if (v.getId() == R.id.loadAndShow) {
                    setLog(logStart);
                    isLoadAndShow = true;
                    loadAd();
                } else if (v.getId() == R.id.isValid) {
                    Log.e(TAG, " isValid: " + (umtRewardAd != null));
                    if (umtRewardAd != null) {
                        boolean isValid = !umtRewardAd.isValid().equals(AdnReadyStatus.ADN_NOT_READY);
                        setLog(isValid ? logAdValid : logAdUnvalid);
                    } else {
                        setLog(logNoAd);
                    }
                }
            }
        };
        setClickListener();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            Log.e("-----openMainActivity", "----横屏转开屏");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制竖屏
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    setTitleView("激励视频"); // 为了Titile部分显示正常
                }
            }, 50);
        }
    }
}
