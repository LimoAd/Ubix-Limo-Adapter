package com.ubimax.network.adbid;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;

import com.adbid.media.AdbidAdInfo;
import com.adbid.media.AdbidBannerListener;
import com.adbid.media.AdbidError;
import com.adbid.media.ad.AdbidBannerView;
import com.ubimax.api.UMTAdConfig;
import com.ubimax.api.bean.AdnReadyStatus;
import com.ubimax.api.bean.BiddingFeedBackBean;
import com.ubimax.api.bean.UMTConstant;
import com.ubimax.api.custom.UMTAdnServerConfig;
import com.ubimax.banner.api.UMTCustomBannerAdapter;
import com.ubimax.utils.BaseUtils;

/**
 * Banner 广告适配
 */
public class AdbidBannerAdapter extends UMTCustomBannerAdapter {
    private static final String TAG = AdbidBannerAdapter.class.getSimpleName();
    private AdbidBannerView bannerView;
    private boolean loaded;

    @Override
    public void load(Context context, UMTAdConfig umtAdConfig, final UMTAdnServerConfig customConfig) {
        Log.i(TAG, "load slot:" + (customConfig != null ? customConfig.getAdnSlotId() : "null"));
        destroyInner();
        if (context == null || customConfig == null) {
            callLoadFail("-1", "context or config is null");
            return;
        }
        Context ctx = context != null ? context : BaseUtils.getContext();
        try {
            bannerView = new AdbidBannerView(ctx);
            bannerView.setUnitId(customConfig.getAdnSlotId());
            bannerView.setAdSize(dp2px(umtAdConfig.width), dp2px(umtAdConfig.height));
        // 仅媒体显式设置静音/非静音时下发；DEFAULT 不干预，交给 Adbid 默认/云端静音
        if (getMuteStatus() > UMTConstant.MuteType.DEFAULT) {
            bannerView.setAdMuted(getMuteStatus() != UMTConstant.MuteType.UNMUTE);
        }
        bannerView.setBannerAdListener(new AdbidBannerListener() {
            @Override
            public void onBannerLoad(@NonNull AdbidAdInfo adInfo) {
                loaded = true;
                try {
                    long price = Math.round(adInfo.getPrice());
                    Log.i(TAG, "onBannerLoad price:" + price);
                    if (isClientBidding()) {
                        callAdCacheSucc(price);
                    } else {
                        callAdCacheSucc();
                    }
                } catch (Throwable t) {
                    Log.e(TAG, "onBannerLoad exception", t);
                    callAdCacheSucc();
                }
            }

            @Override
            public void onBannerFail(@NonNull String adUnitId, @NonNull AdbidError error) {
                Log.e(TAG, "onBannerFail:" + error.getMessage());
                callLoadFail(String.valueOf(error.getCode()), error.getMessage());
            }

            @Override
            public void onBannerShow(@NonNull AdbidAdInfo adInfo) {
                Log.i(TAG, "onBannerShow");
                callAdShow();
            }

            @Override
            public void onBannerClose(@NonNull AdbidAdInfo adInfo) {
                Log.i(TAG, "onBannerClose");
                callAdDismiss();
            }

            @Override
            public void onBannerClicked(@NonNull AdbidAdInfo adInfo) {
                Log.i(TAG, "onBannerClicked");
                callAdClick();
            }
        });
            bannerView.loadAd();
        } catch (Throwable t) {
            Log.e(TAG, "load exception", t);
            callLoadFail("-1", t.getMessage() != null ? t.getMessage() : "load exception");
        }
    }

    @Override
    public AdnReadyStatus isReady() {
        if (bannerView != null && loaded) {
            return AdnReadyStatus.ADN_READY;
        }
        return AdnReadyStatus.ADN_NOT_READY;
    }

    @Override
    public View getBannerView() {
        return bannerView;
    }

    @Override
    public void notifyWin(BiddingFeedBackBean bean) {
        Log.i(TAG, "notifyWin");
        if (bannerView != null) {
            try {
                bannerView.winNotice(AdbidBiddingUtils.getSecondPrice(bean));
            } catch (Throwable t) {
                Log.e(TAG, "notifyWin exception", t);
            }
        }
    }

    @Override
    public void notifyLoss(BiddingFeedBackBean bean) {
        Log.i(TAG, "notifyLoss");
        if (bannerView != null) {
            try {
                bannerView.lossNotice(AdbidBiddingUtils.getFailInfo(bean));
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
        loaded = false;
        if (bannerView != null) {
            try {
                ViewParent parent = bannerView.getParent();
                if (parent instanceof ViewGroup) {
                    ((ViewGroup) parent).removeView(bannerView);
                }
                bannerView.destroy();
            } catch (Throwable t) {
                t.printStackTrace();
            }
            bannerView = null;
        }
    }
}
