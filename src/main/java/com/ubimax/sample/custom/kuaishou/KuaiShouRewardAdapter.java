package com.ubimax.sample.custom.kuaishou;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.kwad.sdk.api.KsAdSDK;
import com.kwad.sdk.api.KsLoadManager;
import com.kwad.sdk.api.KsRewardVideoAd;
import com.kwad.sdk.api.KsScene;
import com.kwad.sdk.api.KsVideoPlayConfig;
import com.ubimax.api.UMTAdConfig;
import com.ubimax.api.bean.AdnReadyStatus;
import com.ubimax.api.bean.UMTConstant;
import com.ubimax.api.custom.UMTAdnServerConfig;
import com.ubimax.constant.ErrorConstant;
import com.ubimax.reward.api.UMTCustomRewardAdapter;

import java.util.List;
import java.util.Map;

/**
 * @author zhanao.feng
 * @date 2024/9/2 13:51
 * @description
 */
public class KuaiShouRewardAdapter extends UMTCustomRewardAdapter {
    public static final String TAG = KuaiShouRewardAdapter.class.getSimpleName();
    private KsRewardVideoAd rewardAd;


    @Override
    public void load(Context context, UMTAdConfig umtAdConfig, final UMTAdnServerConfig customConfig) {
        KsScene.Builder builder = new KsScene.Builder(Utils.getLongValue(customConfig.getAdnSlotId()));
        KsAdSDK.getLoadManager().loadRewardVideoAd(builder.build(), new KsLoadManager.RewardVideoAdListener() {
            @Override
            public void onError(int i, String s) {
                Log.e(TAG, "onError code: " + i + "  msg:" + s);
                callLoadFail(i + "", s);
            }

            @Override
            public void onRewardVideoResult(List<KsRewardVideoAd> list) {
                Log.e(TAG, "onRewardVideoResult");
                if (list != null) {
                    rewardAd = list.get(0);
                    if (customConfig.getBiddingType() == UMTConstant.BiddingType.CLIENT_BIDDING) {
                        callAdDataLoadSucc(rewardAd.getECPM());
                    } else {
                        callAdDataLoadSucc();
                    }
                } else
                    callLoadFail(ErrorConstant.ADN_LOAD_FAIL.getCodeString(), ErrorConstant.ADN_LOAD_FAIL.getMsg());

            }

            @Override
            public void onRewardVideoAdLoad(List<KsRewardVideoAd> list) {
                Log.e(TAG, "onRewardVideoAdLoad");
                if (list != null) {
                    rewardAd = list.get(0);
                    if (customConfig.getBiddingType() == UMTConstant.BiddingType.CLIENT_BIDDING) {
                        callAdCacheSucc(rewardAd.getECPM());
                    } else {
                        callAdCacheSucc();
                    }
                } else {
                    callAdCacheFail(ErrorConstant.ADN_LOAD_CACHE_FAIL.getCodeString(), ErrorConstant.ADN_LOAD_CACHE_FAIL.getMsg());
                }
            }
        });

    }

    @Override
    public void destroy() {

    }

    private void showRewardVideoAd(Activity activity, KsRewardVideoAd mRewardVideoAd, KsVideoPlayConfig videoPlayConfig) {
        if (mRewardVideoAd != null && mRewardVideoAd.isAdEnable()) {
            mRewardVideoAd.setRewardAdInteractionListener(new KsRewardVideoAd.RewardAdInteractionListener() {
                @Override
                public void onAdClicked() {
                    Log.e(TAG, "onAdClicked");
                    callAdClick();
                }

                @Override
                public void onPageDismiss() {
                    Log.e(TAG, "onPageDismiss");
                    callAdDismiss();
                }

                @Override
                public void onVideoPlayError(int code, int extra) {
                    Log.e(TAG, "onVideoPlayError code" + code + "   extra:" + extra);
                    callVideoPlayError(String.valueOf(code), ErrorConstant.VIDEO_ERROR.getMsg());
                }

                @Override
                public void onVideoPlayEnd() {
                    Log.e(TAG, "onVideoPlayEnd");
                    callVideoComplete();
                }

                @Override
                public void onVideoSkipToEnd(long playDuration) {
                    Log.e(TAG, "onVideoSkipToEnd");
                    callVideoSkip();
                }

                @Override
                public void onVideoPlayStart() {
                    Log.e(TAG, "onVideoPlayStart");
                    callAdShow();
                    callVideoStart();
                }


                public void onRewardVerify() {
                    Log.e(TAG, "onRewardVerify");
                    callRewardVerify();
                }


                public void onRewardVerify(Map<String, Object> map) {
                    Log.e(TAG, "onRewardVerify");
                    callRewardVerify();
                }

                public void onRewardStepVerify(int i, int i1) {
                    Log.e(TAG, "onRewardStepVerify");
                }


                public void onExtraRewardVerify(int i) {
                    Log.e(TAG, "onExtraRewardVerify");

                }
            });
            mRewardVideoAd.showRewardVideoAd(activity, videoPlayConfig);
        }
    }


    @Override
    public void show(Activity activity) {
        Log.e(TAG, "show");
        boolean isMute = getMuteStatus() != UMTConstant.MuteType.MUTE;
        if (rewardAd != null && activity != null) {
            KsVideoPlayConfig videoPlayConfig = new KsVideoPlayConfig.Builder()
                    .videoSoundEnable(isMute)
                    .build();
            showRewardVideoAd(activity, rewardAd, videoPlayConfig);
        }
    }

    @Override
    public AdnReadyStatus isReady() {
        if (rewardAd != null) {
            Log.e(TAG, "isReady:" + rewardAd.isAdEnable());
            if (rewardAd.isAdEnable()) {
                return AdnReadyStatus.ADN_READY;
            } else {
                return AdnReadyStatus.ADN_NOT_READY;
            }
        }
        return AdnReadyStatus.ADN_NOT_READY;
    }


}
