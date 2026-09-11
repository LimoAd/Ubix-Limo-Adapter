package com.ubimax.sample.custom.kuaishou;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.kwad.sdk.api.KsAdSDK;
import com.kwad.sdk.api.KsInterstitialAd;
import com.kwad.sdk.api.KsLoadManager;
import com.kwad.sdk.api.KsScene;
import com.kwad.sdk.api.KsVideoPlayConfig;
import com.ubimax.api.UMTAdConfig;
import com.ubimax.api.bean.AdnReadyStatus;
import com.ubimax.api.bean.UMTConstant;
import com.ubimax.api.custom.UMTAdnServerConfig;
import com.ubimax.constant.ErrorConstant;
import com.ubimax.interstitial.api.UMTCustomInterstitialAdapter;

import java.util.List;


/**
 * @author zhanao.feng
 * @date 2024/9/1 23:53
 * @description
 */
public class KuaiShouInterstitialAdapter extends UMTCustomInterstitialAdapter {
    private KsInterstitialAd InterstitialAd;
    public static final String TAG = KuaiShouInterstitialAdapter.class.getSimpleName();


    @Override
    public void load(Context context, UMTAdConfig umtAdConfig, final UMTAdnServerConfig customConfig) {
        KsScene scene = new KsScene.Builder(Utils.getLongValue(customConfig.getAdnSlotId()))
                .adNum(1).build();
        KsAdSDK.getLoadManager().loadInterstitialAd(scene, new KsLoadManager.InterstitialAdListener() {
            @Override
            public void onError(int i, String s) {
                Log.e(TAG, "onError code:" + i + " msg:" + s);
                callLoadFail(i + "", s);
            }

            @Override
            public void onRequestResult(int i) {
                Log.e(TAG, "onRequestResult");
                status = loadSucc;
                callAdDataLoadSucc();

            }

            @Override
            public void onInterstitialAdLoad(List<KsInterstitialAd> list) {
                Log.e(TAG, "onInterstitialAdLoad");
                if (list != null) {
                    InterstitialAd = list.get(0);
                    if (customConfig.getBiddingType() == UMTConstant.BiddingType.CLIENT_BIDDING) {
                        callAdCacheSucc((InterstitialAd.getECPM()));
                    } else {
                        callAdCacheSucc();
                    }
                } else
                    callAdCacheFail(ErrorConstant.ADN_LOAD_CACHE_FAIL.getCodeString(), ErrorConstant.ADN_LOAD_CACHE_FAIL.getMsg());
            }
        });
    }

    @Override
    public AdnReadyStatus isReady() {
        if (InterstitialAd != null) {
            return AdnReadyStatus.ADN_READY;
        } else {
            return AdnReadyStatus.ADN_NOT_READY;
        }
    }

    private void showInterstitialAd(final Activity activity, KsVideoPlayConfig videoPlayConfig) {
        if (InterstitialAd != null) {
            InterstitialAd.setAdInteractionListener(new KsInterstitialAd.AdInteractionListener() {
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
                public void onAdClosed() {
                    Log.e(TAG, "onAdClosed");
                    callAdDismiss();
                }

                @Override
                public void onPageDismiss() {
                    Log.e(TAG, "--onPageDismiss");
                    callAdDismiss();
                }

                @Override
                public void onVideoPlayError(int code, int extra) {
                    Log.e(TAG, "onVideoPlayError code:" + code);
                    callAdShowError(String.valueOf(code), ErrorConstant.ADN_SHOW_FAIL.getMsg());
                }

                @Override
                public void onVideoPlayEnd() {
                    Log.e(TAG, "onVideoPlayEnd:");

                }

                @Override
                public void onVideoPlayStart() {
                    Log.e(TAG, "onVideoPlayStart");
                }

                @Override
                public void onSkippedAd() { //
                    Log.e(TAG, "onSkippedAd");

                }

            });
            InterstitialAd.showInterstitialAd(activity, videoPlayConfig);
        }
    }


    @Override
    public void show(Activity activity) {
        Log.e(TAG, "show");
        boolean isMute = getMuteStatus() == UMTConstant.MuteType.UNMUTE;
        KsVideoPlayConfig videoPlayConfig = new KsVideoPlayConfig.Builder()
                .videoSoundEnable(isMute)
                .build();
        showInterstitialAd(activity, videoPlayConfig);
    }

    @Override
    public void destroy() {

    }
}
