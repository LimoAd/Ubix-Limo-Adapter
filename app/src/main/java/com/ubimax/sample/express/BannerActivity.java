package com.ubimax.sample.express;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.ubimax.UMTSDK;
import com.ubimax.api.UMTAdConfig;
import com.ubimax.api.UMTAdLoader;
import com.ubimax.api.bean.AdnReadyStatus;
import com.ubimax.api.bean.UMTAdSession;
import com.ubimax.api.bean.UMTAdnDetailInfo;
import com.ubimax.api.bean.UMTAdnInfo;
import com.ubimax.api.bean.UMTAdnListener;
import com.ubimax.api.bean.UMTBannerAdListener;
import com.ubimax.api.bean.UMTBannerView;
import com.ubimax.api.bean.UMTErrorInfo;
import com.ubimax.sample.AdConstants;
import com.ubimax.sample.R;
import com.ubimax.sample.base.BaseActivity;
import com.ubimax.sample.utils.ScreenUtils;

/**
 * @author zhanao.feng
 * @date 2024/8/1 14:20
 * @description
 */
public class BannerActivity extends BaseActivity {

    private final String TAG = "_ubidding_pub_banner";
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
    private FrameLayout container;
    private UMTAdLoader adLoader;
    private UMTBannerView mUMTBannerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        heightEdt = findViewById(R.id.heightEdt);
        heightEdt.setText("0");
        setTitleView("横幅广告");
        slotIdEdt.setText(AdConstants.bannerId);
        widthEdt = findViewById(R.id.widthEdt);
        int width = (ScreenUtils.getScreenWidthDip(this) - 30);
        widthEdt.setText(width + "");
        findViewById(R.id.showList).setVisibility(View.GONE);
        adLoader = UMTSDK.createAdLoader();
    }

    protected void initView() {
        container = findViewById(R.id.feedContainer);
        initClickListener();
    }


    public void loadAd() {
        resetAd();
        int width = Integer.parseInt(widthEdt.getText().toString());
        int height = Integer.parseInt(heightEdt.getText().toString());
        UMTAdConfig adConfig = new UMTAdConfig.Builder()
                .setSlotId(slotIdEdt.getText().toString())
                .setWidth(width)
                .setHeight(height)
                .build();


        adLoader.loadBannerAd(this, adConfig, new UMTBannerAdListener() {
            @Override
            public void onAdStart(UMTAdSession<UMTBannerView> umtAdSession) {
                Log.e(TAG, "onAdStart requestId:" + umtAdSession.getRequestId());
                setAdnListener(umtAdSession);
            }

            @Override
            public void onAdLoaded(UMTBannerView umtBannerView) {
                mUMTBannerView = umtBannerView;
                isLoadSuc = true;
                UMTAdnInfo adnInfo = mUMTBannerView.getAdnInfo();
                Log.e(TAG, "onAdLoaded " + adnInfo.toString());
                Log.e(TAG, "is ready:" + mUMTBannerView.isValid());
                Log.e(TAG, "request id:" + mUMTBannerView.getRequestId());
                setLog(logSuc);
                if (isLoadAndShow) {
                    showAd();
                }
            }

            @Override
            public void onAdShow(UMTBannerView umtBannerView) {
                setLog(logExposure);
                Log.e(TAG, "onAdShow");
            }

            @Override
            public void onAdClick() {
                setLog(logClick);
                Log.e(TAG, "onAdClicked");
            }

            @Override
            public void onAdDismiss() {
                setLog(logDismiss);
                Log.e(TAG, "onAdDismiss: ");
                resetAd();
            }

            @Override
            public void onShowError(UMTErrorInfo errorInfo) {
                setLog(logShowError);
                Log.e(TAG, "showError:" + errorInfo.toString() + errorInfo.getAdnErrorList());
                resetAd();
            }

            @Override
            public void onError(UMTErrorInfo errorInfo) {
                Log.e(TAG, "onError" + errorInfo.toString());
                setLog(logError + " code:" + errorInfo.getCode() + " msg:" + errorInfo.getMsg() + " adn error list:" + errorInfo.getAdnErrorList().size());
                resetAd();
            }
        });
    }

    private void setAdnListener(UMTAdSession mUMTAdSession) {
        mUMTAdSession.setADNListener(mAdnListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        resetAd();
    }

    /**
     * 移除广告，重置为加载前状态
     */
    private void resetAd() {
        container.setVisibility(View.GONE);
        container.removeAllViews();
        setLog("");
        isLoadSuc = false;
        if (mUMTBannerView != null) {
            mUMTBannerView.destroy();
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
                    if (mUMTBannerView != null) {
                        setPrice(mUMTBannerView.getAdnInfo().getEcpm() + "");
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
                    if (mUMTBannerView != null) {
                        boolean isValid = !mUMTBannerView.isValid().equals(AdnReadyStatus.ADN_NOT_READY);
                        setLog(isValid ? logAdValid : logAdUnvalid);
                    } else {
                        setLog(logNoAd);
                    }
                }
            }
        };
        setClickListener();
    }

    private void showAd() {
        Log.e(TAG, "showAd");
        container.setVisibility(View.VISIBLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (mUMTBannerView != null) {
            container.removeAllViews();
            container.addView(mUMTBannerView);
        } else {
            setLog("bannerAd is null");
        }
    }
}
