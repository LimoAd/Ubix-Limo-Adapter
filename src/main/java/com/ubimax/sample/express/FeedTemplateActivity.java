package com.ubimax.sample.express;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.ubimax.api.bean.UMTAdSession;
import com.ubimax.api.bean.UMTAdnDetailInfo;
import com.ubimax.api.bean.UMTAdnInfo;
import com.ubimax.api.bean.UMTAdnListener;
import com.ubimax.api.bean.UMTFeedAdSession;
import com.ubimax.sample.AdConstants;
import com.ubimax.sample.base.BaseActivity;
import com.ubimax.sample.utils.ScreenUtils;
import com.ubimax.UMTSDK;
import com.ubimax.api.UMTAdConfig;
import com.ubimax.api.UMTAdLoader;
import com.ubimax.api.bean.AdnReadyStatus;
import com.ubimax.api.bean.UMTConstant;
import com.ubimax.api.bean.UMTErrorInfo;
import com.ubimax.api.bean.UMTFeedAd;
import com.ubimax.api.bean.UMTFeedAdListener;
import com.ubimax.api.bean.UMTFeedEventListener;
import com.ubimax.api.bean.UMTFeedRenderListener;
import com.ubimax.api.bean.UMTFeedVideoAdListener;
import com.ubimax.feed.api.UMTFeedAdView;
import com.ubimax.sample.R;
import com.ubimax.sample.nativead.NativeRecycleActivity;

import java.util.List;


/**
 * 信息流模板广告
 **/
public class FeedTemplateActivity extends BaseActivity {

    private final String TAG = "_ubidding_pub_feed_temp";
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
    private FrameLayout containerFl;
    private UMTFeedAdView nativeAdView;
    private UMTFeedAd feedAdBean;
    private UMTAdLoader adLoader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        setTitleView("模板渲染");
    }

    @Override
    protected void initView() {
        super.initView();
        containerFl = findViewById(R.id.feedContainer);
        slotIdEdt.setText(AdConstants.expressNativeId);
        widthEdt = findViewById(R.id.widthEdt);
        findViewById(R.id.showList).setVisibility(View.VISIBLE);
        int width = (ScreenUtils.getScreenWidth(this) - ScreenUtils.dp2px(30));
        widthEdt.setText(width + "");
        heightEdt = findViewById(R.id.heightEdt);
        heightEdt.setText("0");
        initClickListener();
        findViewById(R.id.showList).setOnClickListener(clickListener);
    }

    public void loadFeed() {
        int width = ScreenUtils.px2dip(FeedTemplateActivity.this, Integer.parseInt(widthEdt.getText().toString()));
        int height = ScreenUtils.px2dip(FeedTemplateActivity.this, Integer.parseInt(heightEdt.getText().toString()));
        Log.e("--------loadAd ", "width " + width + " " + height);
        UMTAdConfig config = new UMTAdConfig.Builder()
                .setHeight(height)
                .setWidth(width)
                .setSlotId(slotIdEdt.getText().toString())
                .build();
        nativeAdView = new UMTFeedAdView(this);
        adLoader = UMTSDK.createAdLoader();
        adLoader.loadFeedAd(this, config, new UMTFeedAdListener() {

            @Override
            public void onAdStart(UMTFeedAdSession umtFeedAdSession) {
                Log.e(TAG, "onAdStart requestId:" + umtFeedAdSession.getRequestId());
                setAdnListener(umtFeedAdSession);
            }

            @Override
            public void onAdLoaded(List<UMTFeedAd> feedAdList) {
                if (feedAdBean != null) {
                    feedAdBean.destroy();
                }
                feedAdBean = feedAdList.get(0);
                UMTAdnInfo adnInfo = feedAdBean.getAdnInfo();
                Log.e(TAG, "onAdLoaded " + adnInfo.toString());
                Log.e(TAG, "is ready:" + feedAdBean.isValid());
                Log.e(TAG, "request id:" + feedAdBean.getRequestId());
                Log.e(TAG, "getExpressAdMaterialType : " + (feedAdBean.getExpressAdMaterialType() == UMTConstant.PatternType.VIDEO));
                isLoadSuc = true;
                setLog(logSuc);
                feedAdBean.setFeedVideoAdListener(new UMTFeedVideoAdListener() {
                    @Override
                    public void onVideoLoaded() {
                        Log.e(TAG, "onVideoLoaded");
                        setLog(logVideoLoaded);
                    }

                    @Override
                    public void onVideoReady() {
                        Log.e(TAG, "onVideoReady");
                        setLog(logVideoReady);
                    }

                    @Override
                    public void onVideoStart() {
                        Log.e(TAG, "onVideoStart");
                        setLog(logVideoStart);
                    }

                    @Override
                    public void onVideoPause() {
                        Log.e(TAG, "onVideoPause");
                        setLog(logVideoPause);
                    }

                    @Override
                    public void onVideoResume() {
                        Log.e(TAG, "onVideoResume");
                        setLog(logVideoResume);
                    }

                    @Override
                    public void onVideoFinish() {
                        Log.e(TAG, "onVideoFinish");
                        setLog(logVideoFinish);
                    }

                    @Override
                    public void onVideoProgressUpdate(long currentPosition, long duration) {
                        Log.e(TAG, "onVideoProgressUpdate currentPosition:" + currentPosition + " duration:" + duration);
                        setLog(logVideoProgressUpdate + " currentPosition:" + currentPosition + " duration:" + duration);
                    }

                    @Override
                    public void onVideoError(UMTErrorInfo errorInfo) {
                        Log.e(TAG, "onVideoError:" + errorInfo.toString());
                        setLog(logVideoError);
                    }
                });
                feedAdBean.setFeedEventListener(new UMTFeedEventListener() {
                    @Override
                    public void onAdShow() {
                        Log.e(TAG, "onAdShow");
                        setLog(logExposure);
                        isLoadSuc = false;
                    }

                    @Override
                    public void onAdClick() {
                        Log.e(TAG, "onAdClicked");
                        setLog(logClick);
                    }

                    @Override
                    public void onAdDismiss() {
                        isLoadSuc = false;
                        setLog(logDismiss);
                        Log.e(TAG, "onAdClose");
                        containerFl.removeAllViews();
                        if (feedAdBean != null) {
                            feedAdBean.destroy();
                        }
                    }

                    @Override
                    public void onShowError(UMTErrorInfo info) {
                        isLoadSuc = false;
                        setLog(logShowError + info.toString());
                        Log.e(TAG, "onShowError:" + info);
                        containerFl.removeAllViews();
                        if (feedAdBean != null) {
                            feedAdBean.destroy();
                        }

                    }
                });
                feedAdBean.setFeedExpressRenderListener(new UMTFeedRenderListener() {
                    @Override
                    public void onAdRenderFail(UMTErrorInfo info) {
                        Log.e(TAG, "onAdRenderFail:" + info.toString());
                        setLog(logRenderFail);

                    }

                    @Override
                    public void onAdRenderSucc() {
                        Log.e(TAG, "onAdRenderSucc");
                        setLog(logRenderSucc);
                        if (feedAdBean.isExpressAd() && feedAdBean.isValid() != AdnReadyStatus.ADN_NOT_READY) {
                            if (isLoadAndShow) {
                                containerFl.removeAllViews();
                                containerFl.addView(nativeAdView);

                            }
                        }
                    }
                });
                feedAdBean.render();
                feedAdBean.register(FeedTemplateActivity.this, nativeAdView, null, null);
            }

            @Override
            public void onError(UMTErrorInfo errorInfo) {
                Log.e(TAG, errorInfo.toString());
                setLog(logError + " code:" + errorInfo.getCode() + " msg:" + errorInfo.getMsg() + "adn error list:" + errorInfo.getAdnErrorList().size());

            }
        });

    }

    private void setAdnListener(UMTFeedAdSession mUMTAdSession) {
        mUMTAdSession.setADNListener(mAdnListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (feedAdBean != null) {
            feedAdBean.destroy();
        }
    }


    private void initClickListener() {
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.loadBtn) {
                    setLog(logStart);
                    isLoadAndShow = false;
                    loadFeed();
                } else if (v.getId() == R.id.priceBtn) {
                    if (feedAdBean != null && feedAdBean.getAdnInfo() != null && isLoadSuc) {
                        setPrice(feedAdBean.getAdnInfo().getEcpm() + "");
                    } else {
                        setLog(logNoAd);
                    }
                } else if (v.getId() == R.id.showBtn) {
                    // 设置页面全屏显示
                    if (isLoadSuc && feedAdBean != null) {
                        if (nativeAdView.getParent() == null) {
                            containerFl.removeAllViews();
                            containerFl.addView(nativeAdView);
                        } else {
                            setLog("广告已展示");
                        }
                    } else {
                        setLog(logNoAd);
                    }
                } else if (v.getId() == R.id.loadAndShow) {
                    setLog(logStart);
                    isLoadAndShow = true;
                    loadFeed();
                } else if (v.getId() == R.id.isValid) {
                    Log.e(TAG, " isValid: " + (feedAdBean != null));
                    if (feedAdBean != null && isLoadSuc) {
                        boolean isValid = feedAdBean.isValid() != AdnReadyStatus.ADN_NOT_READY;
                        setLog(isValid ? logAdValid : logAdUnvalid);
                    } else {
                        setLog(logNoAd);
                    }
                } else if (v.getId() == R.id.showList) {
                    setLog("列表展示");
                    Intent intent = new Intent(FeedTemplateActivity.this, NativeRecycleActivity.class);
                    intent.putExtra("slotId", slotIdEdt.getText().toString());
                    startActivity(intent);
                }
            }
        };
        setClickListener();
    }

}
