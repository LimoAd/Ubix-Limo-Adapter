package com.ubimax.network.adbid;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adbid.media.AdbidAdInfo;
import com.adbid.media.AdbidError;
import com.adbid.media.AdbidListener;
import com.adbid.media.ad.AdbidAppOpen;
import com.ubimax.api.UMTAdConfig;
import com.ubimax.api.bean.AdnReadyStatus;
import com.ubimax.api.bean.BiddingFeedBackBean;
import com.ubimax.api.bean.UMTConstant;
import com.ubimax.api.custom.UMTAdnServerConfig;
import com.ubimax.splash.api.UMTCustomSplashAdapter;

/**
 * 开屏广告适配
 */
public class AdbidSplashAdapter extends UMTCustomSplashAdapter {
    private static final String TAG = AdbidSplashAdapter.class.getSimpleName();
    private AdbidAppOpen splashAd;

    @Override
    public void load(Context context, UMTAdConfig umtAdConfig, final UMTAdnServerConfig customConfig) {
        Log.i(TAG, "load slot:" + (customConfig != null ? customConfig.getAdnSlotId() : "null"));
        destroyInner();
        if (customConfig == null) {
            callLoadFail("-1", "config is null");
            return;
        }
        try {
            splashAd = new AdbidAppOpen(customConfig.getAdnSlotId());
            applyMute(splashAd);
            splashAd.setAdListener(new AdbidListener() {
            @Override
            public void onAdLoad(@NonNull AdbidAdInfo info) {
                long price = priceOf(info);
                Log.i(TAG, "onAdLoad price:" + price);
                if (customConfig.getBiddingType() == UMTConstant.BiddingType.CLIENT_BIDDING) {
                    callAdCacheSucc(price);
                } else {
                    callAdCacheSucc();
                }
            }

            @Override
            public void onAdLoadFail(@Nullable String adUnitId, @NonNull AdbidError error) {
                Log.e(TAG, "onAdLoadFail:" + error.getMessage());
                callLoadFail(String.valueOf(error.getCode()), error.getMessage());
            }

            @Override
            public void onAdDisplayed(@NonNull AdbidAdInfo info) {
                Log.i(TAG, "onAdDisplayed");
                callAdShow();
            }

            @Override
            public void onAdDisplayedFailed(@Nullable AdbidAdInfo info, AdbidError error) {
                String msg = error != null ? error.getMessage() : "show fail";
                int code = error != null ? error.getCode() : -1;
                Log.e(TAG, "onAdDisplayedFailed:" + msg);
                callAdShowError(String.valueOf(code), msg);
            }

            @Override
            public void onAdHidden(@NonNull AdbidAdInfo info) {
                Log.i(TAG, "onAdHidden");
                callAdDismiss();
            }

            @Override
            public void onAdClicked(@NonNull AdbidAdInfo info) {
                Log.i(TAG, "onAdClicked");
                callAdClick();
            }
        });
        splashAd.loadAd();
        } catch (Throwable t) {
            Log.e(TAG, "load exception", t);
            callLoadFail("-1", t.getMessage() != null ? t.getMessage() : "load exception");
        }
    }

    @Override
    public void show(ViewGroup group) {
        Log.i(TAG, "show");
        if (splashAd == null || group == null) {
            callAdShowError("-1", "splashAd or container is null");
            return;
        }
        try {
            splashAd.showAd(group);
        } catch (Throwable t) {
            Log.e(TAG, "show exception", t);
            callAdShowError("-1", t.getMessage() != null ? t.getMessage() : "show exception");
        }
    }

    @Override
    public AdnReadyStatus isReady() {
        if (splashAd != null && splashAd.isReady()) {
            return AdnReadyStatus.ADN_READY;
        }
        return AdnReadyStatus.ADN_NOT_READY;
    }

    @Override
    public void notifyWin(BiddingFeedBackBean bean) {
        Log.i(TAG, "notifyWin");
        if (splashAd != null) {
            try {
                splashAd.winNotice(AdbidBiddingUtils.getSecondPrice(bean));
            } catch (Throwable t) {
                Log.e(TAG, "notifyWin exception", t);
            }
        }
    }

    @Override
    public void notifyLoss(BiddingFeedBackBean bean) {
        Log.i(TAG, "notifyLoss");
        if (splashAd != null) {
            try {
                splashAd.lossNotice(AdbidBiddingUtils.getFailInfo(bean));
            } catch (Throwable t) {
                Log.e(TAG, "notifyLoss exception", t);
            }
        }
    }

    @Override
    public void destroy() {
        destroyInner();
    }

    private void destroyInner() {
        if (splashAd != null) {
            try {
                splashAd.destroy();
            } catch (Throwable t) {
                Log.e(TAG, "destroy exception", t);
            }
            splashAd = null;
        }
    }

    private void applyMute(AdbidAppOpen ad) {
        if (ad == null) {
            return;
        }
        // 仅媒体显式设置静音/非静音时下发；DEFAULT 不干预，交给 Adbid 默认/云端静音
        if (getMuteStatus() > UMTConstant.MuteType.DEFAULT) {
            ad.setAdMuted(getMuteStatus() != UMTConstant.MuteType.UNMUTE);
        }
    }

    private static long priceOf(AdbidAdInfo info) {
        if (info == null) {
            return 0;
        }
        return Math.round(info.getPrice());
    }
}
