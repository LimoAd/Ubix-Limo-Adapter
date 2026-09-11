package com.ubimax.sample.nativead;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.ubimax.api.bean.UMTAdnDetailInfo;
import com.ubimax.api.bean.UMTAdnInfo;
import com.ubimax.api.bean.UMTAdnListener;
import com.ubimax.api.bean.UMTFeedAdSession;
import com.ubimax.api.bean.UMTImage;
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
import com.ubimax.api.bean.UMTFeedDownloadAdListener;
import com.ubimax.api.bean.UMTFeedEventListener;
import com.ubimax.api.bean.UMTFeedExtraInfo;
import com.ubimax.api.bean.UMTFeedRenderListener;
import com.ubimax.api.bean.UMTFeedVideoAdListener;
import com.ubimax.feed.api.UMTFeedAdView;
import com.ubimax.sample.R;

import java.util.List;


/**
 * 信息流自渲染
 **/
public class FeedNativeActivity extends BaseActivity {

    private final String TAG = "_ubidding_pub_feed_native";
    //开发者强引用，SDK内部弱引用
    private final UMTAdnListener mAdnListener = new UMTAdnListener() {
        @Override
        public void onAdnRequest(UMTAdnDetailInfo umtAdnDetailInfo) {
            Log.e(TAG, "onAdnRequest=" + umtAdnDetailInfo.toString());
        }

        @Override
        public void onAdnLoaded(UMTAdnDetailInfo umtAdnDetailInfo) {
            Log.e(TAG, "onAdnLoaded=" + umtAdnDetailInfo.toString());
        }

        @Override
        public void onAdnLoadFail(UMTAdnDetailInfo umtAdnDetailInfo, UMTErrorInfo umtErrorInfo) {
            Log.e(TAG, "onAdnLoadFail=" + umtAdnDetailInfo.toString() + ", err=" + umtErrorInfo.toString());
        }
    };
    UMTAdLoader adLoader;
    private FrameLayout containerFl;
    private UMTFeedAdView nativeAdView;
    private ViewGroup selfRenderView;
    private boolean isBindShow = false;//开发者提前包装好，或者xml布局直接使用UMTFeedAdView当根容器
    private UMTFeedAd feedAdBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        setTitleView("信息流广告");
    }


    @Override
    protected void initView() {
        super.initView();
        containerFl = findViewById(R.id.feedContainer);
        slotIdEdt.setText(AdConstants.nativeId);
        widthEdt = findViewById(R.id.widthEdt);
        findViewById(R.id.showList).setVisibility(View.VISIBLE);
        int width = (ScreenUtils.getScreenWidth(FeedNativeActivity.this) - ScreenUtils.dp2px(30));
        widthEdt.setText(width + "");
        heightEdt = findViewById(R.id.heightEdt);
        heightEdt.setText("0");
        initClickListener();
        findViewById(R.id.showList).setOnClickListener(clickListener);
        findViewById(R.id.bindShowStyle).setOnClickListener(clickListener);
    }

    // 穿山甲不支持指定宽高，传的高度只是把容器扩大，支持宽度定义
    // 快手不能使用固定宽高
    // 百度不能设置宽度
    //
    public void loadAd() {
        int width = ScreenUtils.px2dip(FeedNativeActivity.this, Integer.parseInt(widthEdt.getText().toString()));
        int height = ScreenUtils.px2dip(FeedNativeActivity.this, Integer.parseInt(heightEdt.getText().toString()));
        Log.e("--------loadAd ", "width " + width + " " + height);


        UMTAdConfig config = new UMTAdConfig.Builder()
                .setHeight(height)
                .setWidth(width)
                .setSlotId(slotIdEdt.getText().toString())
                // 开发者自渲染摇一摇 View；百度广告位不受该配置影响，始终挂载百度自己的 View
//                .setDeveloperRenderShakeView(true)
                .build();

        nativeAdView = new UMTFeedAdView(this);
        adLoader = UMTSDK.createAdLoader();
        adLoader.loadFeedAd(FeedNativeActivity.this, config, new UMTFeedAdListener() {

            @Override
            public void onAdStart(UMTFeedAdSession umtFeedAdSession) {
                Log.e(TAG, "onAdStart requestId:" + umtFeedAdSession.getRequestId());
                setAdnListener(umtFeedAdSession);
            }

            @Override
            public void onAdLoaded(List<UMTFeedAd> feedAd) {
                Log.e(TAG, "onAdLoaded");
                isLoadSuc = true;
                setLog(logSuc);
                if (feedAdBean != null) {
                    feedAdBean.destroy();
                }
                feedAdBean = feedAd.get(0);
                UMTAdnInfo adnInfo = feedAdBean.getAdnInfo();
                Log.e(TAG, "adnInfo " + adnInfo.toString());
                Log.e(TAG, "is ready:" + feedAdBean.isValid());
                Log.e(TAG, "request id:" + feedAdBean.getRequestId());
                Log.e(TAG, "isExpressAd: " + feedAdBean.isExpressAd());
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
                    public void onShowError(UMTErrorInfo errorInfo) {
                        isLoadSuc = false;
                        setLog(logShowError);
                        Log.e(TAG, "onShowError");
                        containerFl.removeAllViews();
                        if (feedAdBean != null) {
                            feedAdBean.destroy();
                        }
                    }
                });
                if (feedAdBean.isExpressAd()) {
                    Log.e(TAG, "getExpressAdMaterialType : " + (feedAdBean.getExpressAdMaterialType() == UMTConstant.PatternType.VIDEO));

                    feedAdBean.setFeedExpressRenderListener(new UMTFeedRenderListener() {
                        @Override
                        public void onAdRenderFail(UMTErrorInfo errorInfo) {
                            Log.e(TAG, "onAdRenderFail");
                            setLog(logRenderFail);

                        }

                        @Override
                        public void onAdRenderSucc() {
                            Log.e(TAG, "onAdRenderSucc");
                            setLog(logRenderSucc);
                            if (feedAdBean.isValid() != AdnReadyStatus.ADN_NOT_READY) {
                                if (isLoadAndShow) {
                                    containerFl.removeAllViews();
                                    containerFl.addView(nativeAdView);

                                }
                            }
                        }
                    });
                    feedAdBean.render();
                    feedAdBean.register(FeedNativeActivity.this, nativeAdView, null, null);
                } else {
                    if (feedAdBean.getMaterial() != null) {
//                        Log.e(TAG, "shakeStatus: " + feedAdBean.getMaterial().getShakeStatus());
                        if (feedAdBean.getMaterial().getMainImgInfo() != null) {
                            UMTImage image = feedAdBean.getMaterial().getMainImgInfo();
                            Log.e(TAG, "getMainImageUrl: " + image.getImgUrl());
                            Log.e(TAG, "getMainImageWidth: " + image.getWidth());
                            Log.e(TAG, "getMainImageHeight: " + image.getHeight());
                        }

                        Log.e(TAG, "getDescription: " + feedAdBean.getMaterial().getDescription());
                        if (feedAdBean.getMaterial().getIconMaterialInfo() != null) {
                            Log.e(TAG, "getIconUrl: " + feedAdBean.getMaterial().getIconMaterialInfo().getImgUrl());
                        }

                        Log.e(TAG, "getTitle: " + feedAdBean.getMaterial().getTitle());
                        Log.e(TAG, "getSource: " + feedAdBean.getMaterial().getSource());
                        Log.e(TAG, "getInteractionType: " + feedAdBean.getMaterial().getInteractionType());
                        if (feedAdBean.getMaterial().isVideo() && feedAdBean.getMaterial().getVideoInfo() != null) {
                            Log.e(TAG, "getVideoUrl: " + feedAdBean.getMaterial().getVideoInfo().getVideoUrl());
                            Log.e(TAG, "getVideoDuration: " + feedAdBean.getMaterial().getVideoInfo().getVideoDuration());
                        }

                    }
                    feedAdBean.setFeedDownloadAdListener(new UMTFeedDownloadAdListener() {

                        @Override
                        public void onIdle() {
                            Log.e(TAG, "onIdle");
                        }

                        @Override
                        public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                            Log.e(TAG, "onDownloadActive" + "totalBytes:" + totalBytes + " currBytes:" + currBytes + " fileName:" + fileName + " appName:" + appName);
                        }

                        @Override
                        public void onDownloadPause(long totalBytes, long currBytes, String fileName, String appName) {
                            Log.e(TAG, "onDownloadPause" + "totalBytes:" + totalBytes + " currBytes:" + currBytes + " fileName:" + fileName + " appName:" + appName);
                        }

                        @Override
                        public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                            Log.e(TAG, "onDownloadFailed" + "totalBytes:" + totalBytes + " currBytes:" + currBytes + " fileName:" + fileName + " appName:" + appName);
                        }

                        @Override
                        public void onDownloadFinish(long totalBytes, String fileName, String appName) {
                            Log.e(TAG, "onDownloadFinish" + "totalBytes:" + totalBytes + " fileName:" + fileName + " appName:" + appName);
                        }

                        @Override
                        public void onInstall(String fileName, String appName) {
                            Log.e(TAG, "onInstall: " + "fileName:" + fileName + " appName:" + appName);
                        }
                    });
                    if (feedAdBean.getMaterial().getInteractionType() == UMTConstant.FeedInteractionType.DOWNLOAD) {
                        if (feedAdBean.getMaterial().getDownloadAppInfo() != null) {
                            Log.e(TAG, "下载六要素: "
                                    + "\nAppName:" + feedAdBean.getMaterial().getDownloadAppInfo().getAppName()
                                    + "\nAppVersion:" + feedAdBean.getMaterial().getDownloadAppInfo().getAppVersionName()
                                    + "\nAppPublisher:" + feedAdBean.getMaterial().getDownloadAppInfo().getAppPublisher()
                                    + "\nPrivacyAgreementUrl:" + feedAdBean.getMaterial().getDownloadAppInfo().getPrivacyAgreementUrl()
                                    + "\nPermissionUrl:" + feedAdBean.getMaterial().getDownloadAppInfo().getPermissionUrl()
                                    + "\nFunctionUrl:" + feedAdBean.getMaterial().getDownloadAppInfo().getFunctionUrl()
                            );
                        }
                    }


                    selfRenderView = (ViewGroup) LayoutInflater.from(FeedNativeActivity.this).inflate(R.layout.native_ad_item, null);
                    UMTFeedExtraInfo extraInfo = new UMTFeedExtraInfo();
                    RenderUtils.renderNativeView(FeedNativeActivity.this, feedAdBean.getMaterial(), selfRenderView, extraInfo, feedAdBean.getAdnInfo().getAdnName());
                    if (isBindShow) {//开发者提前包装好，或者xml布局直接使用UMTFeedAdView当根容器
                        nativeAdView.addView(selfRenderView);
                        feedAdBean.register(FeedNativeActivity.this, nativeAdView, extraInfo);
                    } else {
                        feedAdBean.register(FeedNativeActivity.this, nativeAdView, selfRenderView, extraInfo);
                    }
                    if (isLoadAndShow) {
                        containerFl.removeAllViews();
                        containerFl.addView(nativeAdView);
                    }
                }
            }

            @Override
            public void onError(UMTErrorInfo errorInfo) {
                Log.e(TAG, errorInfo.toString());
                setLog(logError + " code:" + errorInfo.getCode() + " msg:" + errorInfo.getMsg() + "pMsg:" + errorInfo.getAdnErrorList().size());

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (feedAdBean != null) {
            feedAdBean.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (feedAdBean != null) {
            feedAdBean.onResume();
        }
    }

    private void showAd() {
        containerFl.removeAllViews();
        containerFl.addView(nativeAdView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (feedAdBean != null) {
            feedAdBean.destroy();
        }
    }

    private void setAdnListener(UMTFeedAdSession mUMTAdSession) {
        mUMTAdSession.setADNListener(mAdnListener);
    }

    private void initClickListener() {
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.loadBtn) {
                    setLog(logStart);
                    isLoadAndShow = false;
                    isBindShow = false;
                    loadAd();
                } else if (v.getId() == R.id.priceBtn) {
                    if (feedAdBean != null && feedAdBean.getAdnInfo() != null && isLoadSuc) {
                        setPrice(feedAdBean.getAdnInfo().getEcpm() + "");
                    } else {
                        setLog(logNoAd);
                    }
                } else if (v.getId() == R.id.showBtn) {
                    // 设置页面全屏显示
                    if (isLoadSuc) {
                        if (nativeAdView.getParent() == null) {
                            showAd();
                        } else {
                            setLog("广告已展示");
                        }
                    } else {
                        setLog(logNoAd);
                    }
                } else if (v.getId() == R.id.loadAndShow) {
                    setLog(logStart);
                    isLoadAndShow = true;
                    isBindShow = false;
                    loadAd();
                } else if (v.getId() == R.id.bindShowStyle) {
                    setLog(logStart);
                    isLoadAndShow = true;
                    isBindShow = true;
                    loadAd();
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
                    Intent intent = new Intent(FeedNativeActivity.this, NativeRecycleActivity.class);
                    intent.putExtra("slotId", slotIdEdt.getText().toString());
                    startActivity(intent);
                }
            }
        };
        setClickListener();
    }

}
