package com.ubimax.network.adbid;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adbid.media.AdbidAdInfo;
import com.adbid.media.AdbidError;
import com.adbid.media.AdbidRewardListener;
import com.adbid.media.ad.AdbidRewarded;
import com.ubimax.api.UMTAdConfig;
import com.ubimax.api.bean.AdnReadyStatus;
import com.ubimax.api.bean.BiddingFeedBackBean;
import com.ubimax.api.bean.UMTConstant;
import com.ubimax.api.custom.UMTAdnServerConfig;
import com.ubimax.reward.api.UMTCustomRewardAdapter;

/**
 * 激励视频广告适配
 */
public class AdbidRewardAdapter extends UMTCustomRewardAdapter {
    private static final String TAG = AdbidRewardAdapter.class.getSimpleName();
    private AdbidRewarded rewardAd;

    @Override
    public void load(Context context, UMTAdConfig umtAdConfig, final UMTAdnServerConfig customConfig) {
        Log.i(TAG, "load slot:" + (customConfig != null ? customConfig.getAdnSlotId() : "null"));
        destroyInner();
        if (customConfig == null) {
            callLoadFail("-1", "config is null");
            return;
        }
        try {
            rewardAd = new AdbidRewarded(customConfig.getAdnSlotId());
            applyMute(rewardAd);
            rewardAd.setAdListener(new AdbidRewardListener() {
            @Override
            public void onUserReward(@NonNull AdbidAdInfo adInfo) {
                Log.i(TAG, "onUserReward");
                callRewardVerify();
            }

            @Override
            public void onAdLoad(@NonNull AdbidAdInfo adInfo) {
                try {
                    long price = Math.round(adInfo.getPrice());
                    Log.i(TAG, "onAdLoad price:" + price);
                    if (customConfig.getBiddingType() == UMTConstant.BiddingType.CLIENT_BIDDING) {
                        callAdDataLoadSucc(price);
                        callAdCacheSucc(price);
                    } else {
                        callAdDataLoadSucc();
                        callAdCacheSucc();
                    }
                } catch (Throwable t) {
                    Log.e(TAG, "onAdLoad exception", t);
                    callAdDataLoadSucc();
                    callAdCacheSucc();
                }
            }

            @Override
            public void onAdLoadFail(@Nullable String adUnitId, @NonNull AdbidError error) {
                Log.e(TAG, "onAdLoadFail:" + error.getMessage());
                callLoadFail(String.valueOf(error.getCode()), error.getMessage());
            }

            @Override
            public void onAdDisplayed(@NonNull AdbidAdInfo adInfo) {
                Log.i(TAG, "onAdDisplayed");
                callAdShow();
                callVideoStart();
            }

            @Override
            public void onAdDisplayedFailed(@Nullable AdbidAdInfo adInfo, AdbidError error) {
                String msg = error != null ? error.getMessage() : "show fail";
                int code = error != null ? error.getCode() : -1;
                Log.e(TAG, "onAdDisplayedFailed:" + msg);
                callVideoPlayError(String.valueOf(code), msg);
            }

            @Override
            public void onAdHidden(@NonNull AdbidAdInfo adInfo) {
                Log.i(TAG, "onAdHidden");
                callVideoComplete();
                callAdDismiss();
            }

            @Override
            public void onAdClicked(@NonNull AdbidAdInfo adInfo) {
                Log.i(TAG, "onAdClicked");
                callAdClick();
            }
        });
            rewardAd.loadAd();
        } catch (Throwable t) {
            Log.e(TAG, "load exception", t);
            callLoadFail("-1", t.getMessage() != null ? t.getMessage() : "load exception");
        }
    }

    @Override
    public void show(Activity activity) {
        Log.i(TAG, "show");
        if (activity == null || activity.isFinishing()) {
            callVideoPlayError("-1", "activity is invalid");
            return;
        }
        if (rewardAd == null || !rewardAd.isReady()) {
            callVideoPlayError("-1", "reward not ready");
            return;
        }
        try {
            rewardAd.showAd();
        } catch (Throwable t) {
            Log.e(TAG, "show exception", t);
            callVideoPlayError("-1", t.getMessage() != null ? t.getMessage() : "show exception");
        }
    }

    @Override
    public AdnReadyStatus isReady() {
        if (rewardAd != null && rewardAd.isReady()) {
            return AdnReadyStatus.ADN_READY;
        }
        return AdnReadyStatus.ADN_NOT_READY;
    }

    @Override
    public void notifyWin(BiddingFeedBackBean bean) {
        Log.i(TAG, "notifyWin");
        if (rewardAd != null) {
            try {
                rewardAd.winNotice(AdbidBiddingUtils.getSecondPrice(bean));
            } catch (Throwable t) {
                Log.e(TAG, "notifyWin exception", t);
            }
        }
    }

    @Override
    public void notifyLoss(BiddingFeedBackBean bean) {
        Log.i(TAG, "notifyLoss");
        if (rewardAd != null) {
            try {
                rewardAd.lossNotice(AdbidBiddingUtils.getFailInfo(bean));
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
        if (rewardAd != null) {
            try {
                rewardAd.destroy();
            } catch (Throwable t) {
                Log.e(TAG, "destroy exception", t);
            }
            rewardAd = null;
        }
    }

    private void applyMute(AdbidRewarded ad) {
        if (ad == null) {
            return;
        }
        // 仅媒体显式设置静音/非静音时下发；DEFAULT 不干预，交给 Adbid 默认/云端静音
        if (getMuteStatus() > UMTConstant.MuteType.DEFAULT) {
            ad.setAdMuted(getMuteStatus() != UMTConstant.MuteType.UNMUTE);
        }
    }
}
