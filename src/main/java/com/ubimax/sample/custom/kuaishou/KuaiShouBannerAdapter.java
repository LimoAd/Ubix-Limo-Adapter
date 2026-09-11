package com.ubimax.sample.custom.kuaishou;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.kwad.sdk.api.KsAdSDK;
import com.kwad.sdk.api.KsAdVideoPlayConfig;
import com.kwad.sdk.api.KsBannerAd;
import com.kwad.sdk.api.KsLoadManager;
import com.kwad.sdk.api.KsScene;
import com.ubimax.api.UMTAdConfig;
import com.ubimax.api.bean.AdnReadyStatus;
import com.ubimax.api.bean.UMTConstant;
import com.ubimax.api.custom.UMTAdnServerConfig;
import com.ubimax.banner.api.UMTCustomBannerAdapter;
import com.ubimax.constant.ErrorConstant;
import com.ubimax.utils.BaseUtils;

public class KuaiShouBannerAdapter extends UMTCustomBannerAdapter {
    private final String TAG = getClass().getSimpleName();
    KsBannerAd mKsBannerAd;
    View expressView;
    //注册监听
    private KsBannerAd.BannerAdInteractionListener mBannerAdInteractionListener = new KsBannerAd.BannerAdInteractionListener() {


        @Override
        public void onAdClicked() {
            Log.e(TAG, "onAdClicked");
            callAdClick();
        }

        @Override
        public void onAdShow() {
            Log.e(TAG, "onAdShow");
            callAdShow();
        }

        @Override
        public void onAdClose() {
            Log.e(TAG, "onAdClose");
            callAdDismiss();
        }

        @Override
        public void onAdShowError(int i, String onMediaPlayError) {
            Log.e(TAG, "onAdShowError code=" + i + ", msg=" + onMediaPlayError);
            callAdShowError(String.valueOf(i), onMediaPlayError);
        }
    };

    @Override
    public void load(Context context, UMTAdConfig umtAdConfig, final UMTAdnServerConfig customConfig) {
        long AdnSlotId = 0;
        try {
            AdnSlotId = Long.parseLong(customConfig.getAdnSlotId());
        } catch (NumberFormatException e) {
            Log.e(TAG, "load: " + e.getMessage());
        }
        KsScene.Builder builder = new KsScene.Builder(AdnSlotId)
                .width(dp2px(umtAdConfig.width)).height(dp2px(umtAdConfig.height));
        KsScene scene = builder.build();
        KsAdSDK.getLoadManager().loadBannerAd(scene, new KsLoadManager.BannerAdListener() {
            @Override
            public void onError(int code, String msg) {
                Log.e(TAG, "onError code:" + code + " msg:" + msg);
                callLoadFail(String.valueOf(code), msg);
            }

            @Override
            public void onBannerAdLoad(KsBannerAd ksBannerAd) {
                Log.e(TAG, "onBannerAdLoad: ");
                //Nullable
                if (ksBannerAd == null) {
                    callLoadFail(ErrorConstant.ADN_LOAD_FAIL.getCodeString(), "ksBannerAd is null");
                    return;
                }
                int price = 0;
                mKsBannerAd = ksBannerAd;
                if (isClientBidding()) {
                    Log.e(TAG, "price:" + ksBannerAd.getECPM());//单位：分，默认为0
                    price = (ksBannerAd.getECPM());
                }
                //
                callAdCacheSucc(price);

            }
        });
    }

    @Override
    public AdnReadyStatus isReady() {
        if (mKsBannerAd != null) {
            return AdnReadyStatus.ADN_READY;
        }
        return AdnReadyStatus.ADN_NOT_READY;
    }

    @Override
    public View getBannerView() {
        //开发者调用再调用，保证计费、回调时机统一
        if (mKsBannerAd != null && expressView == null) {
            KsAdVideoPlayConfig videoPlayConfig = new KsAdVideoPlayConfig.Builder().build();
            if (getMuteStatus() > UMTConstant.MuteType.DEFAULT) {
                final boolean isMute = getMuteStatus() != UMTConstant.MuteType.UNMUTE;
                videoPlayConfig.setVideoSoundEnable(!isMute);
            }
            //
            int playPolicy = getVideoPlayNetType();
            if (playPolicy > UMTConstant.VideoPlayNetType.DEFAULT) {
                int policy = KsAdVideoPlayConfig.VideoAutoPlayType.AUTO_PLAY_WIFI;
                if (playPolicy == UMTConstant.VideoPlayNetType.NOT_PLAY) {
                    policy = KsAdVideoPlayConfig.VideoAutoPlayType.NO_AUTO_PLAY;
                }
                videoPlayConfig.setVideoAutoPlayType(policy);
            }
            Context context = mWrActivity != null && mWrActivity.get() != null ? mWrActivity.get() : BaseUtils.getContext();
            expressView = mKsBannerAd.getView(context, mBannerAdInteractionListener, videoPlayConfig);
        }
        return expressView;
    }

    @Override
    public void destroy() {
        try {
            mBannerAdInteractionListener = null;
            if (expressView != null) {
                ViewParent parent = expressView.getParent();
                if (parent instanceof ViewGroup) {
                    ((ViewGroup) parent).removeView(expressView);
                }
                expressView = null;
            }
            mKsBannerAd = null;
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
