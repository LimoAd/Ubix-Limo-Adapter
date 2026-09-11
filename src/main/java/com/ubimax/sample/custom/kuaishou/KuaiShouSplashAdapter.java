package com.ubimax.sample.custom.kuaishou;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.kwad.sdk.api.KsAdSDK;
import com.kwad.sdk.api.KsLoadManager;
import com.kwad.sdk.api.KsScene;
import com.kwad.sdk.api.KsSplashScreenAd;
import com.ubimax.api.UMTAdConfig;
import com.ubimax.api.bean.AdnReadyStatus;
import com.ubimax.api.bean.UMTConstant;
import com.ubimax.api.custom.UMTAdnServerConfig;
import com.ubimax.splash.api.UMTCustomSplashAdapter;
import com.ubimax.utils.BaseUtils;

/**
 * @author zhanao.feng
 * @date 2022/1/17 4:39 下午
 * @description
 */

public class KuaiShouSplashAdapter extends UMTCustomSplashAdapter {
    private static final String TAG = KuaiShouSplashAdapter.class.getSimpleName();
    private KsSplashScreenAd splashScreenAd;
    private int status;


    @Override
    public void load(Context context, UMTAdConfig umtAdConfig, final UMTAdnServerConfig customConfig) {
        Log.e(TAG, customConfig.getServerCustomConfig());
        KsScene.Builder builder = new KsScene.Builder(Utils.getLongValue(customConfig.getAdnSlotId()));
        KsAdSDK.getLoadManager().loadSplashScreenAd(builder.build(), new KsLoadManager.SplashScreenAdListener() {
            @Override
            public void onError(int i, String s) {
                Log.e(TAG, "onError: code" + i + " msg:" + s);
                if (status == loadSucc) {
                    callAdCacheFail(i + "", s);
                } else {
                    callLoadFail(i + "", s);
                }
            }

            @Override
            public void onRequestResult(int i) {
                Log.e(TAG, "onRequestResult");
                status = loadSucc;
                callAdDataLoadSucc();

            }

            @Override
            public void onSplashScreenAdLoad(KsSplashScreenAd ksSplashScreenAd) {
                Log.e(TAG, "onSplashScreenAdLoad");
                splashScreenAd = ksSplashScreenAd;
                if (customConfig.getBiddingType() == UMTConstant.BiddingType.CLIENT_BIDDING) {
                    Log.e(TAG, "onAdLoadSuccess price:" + splashScreenAd.getECPM());
                    callAdCacheSucc(splashScreenAd.getECPM());
                } else {
                    callAdCacheSucc();
                }
            }
        });

    }

    @Override
    public void show(ViewGroup group) {
        Log.e(TAG, "show");
        if (splashScreenAd == null || group == null) return;
        View view = splashScreenAd.getView(BaseUtils.getContext(), new KsSplashScreenAd.SplashScreenAdInteractionListener() {
            @Override
            public void onAdClicked() {
                Log.e(TAG, "onAdClicked");
                callAdClick();
            }

            @Override
            public void onAdShowError(int i, String s) {
                Log.e(TAG, "onAdShowError code:" + i + " msg:" + s);
                callAdShowError(i + "", s);
            }

            @Override
            public void onAdShowEnd() {
                Log.e(TAG, "onAdShowEnd");
                if (getDismissType() != UMTConstant.DismissType.SKIP) {
                    setDismissType(UMTConstant.DismissType.NORMAL);
                }
                callAdDismiss();
            }

            @Override
            public void onAdShowStart() {
                Log.e(TAG, "onAdShowStart");
                callAdShow();
            }

            @Override
            public void onSkippedAd() {
                Log.e(TAG, "onSkippedAd");
                setDismissType(UMTConstant.DismissType.SKIP);
                callAdDismiss();
            }

            @Override
            public void onDownloadTipsDialogShow() {
                Log.e(TAG, "onDownloadTipsDialogShow");

            }

            @Override
            public void onDownloadTipsDialogDismiss() {
                Log.e(TAG, "onDownloadTipsDialogDismiss");
            }

            @Override
            public void onDownloadTipsDialogCancel() {
                Log.e(TAG, "onDownloadTipsDialogCancel");
            }
        });
        try {
            group.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } catch (Throwable e) {
            e.printStackTrace();
        }


    }

    @Override
    public AdnReadyStatus isReady() {
        if (splashScreenAd.isAdEnable()) {
            return AdnReadyStatus.ADN_READY;
        } else {
            return AdnReadyStatus.ADN_NOT_READY;
        }
    }


    @Override
    public void destroy() {

    }
}
