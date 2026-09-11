package com.ubimax.sample.custom.kuaishou;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.kwad.sdk.api.KsAdVideoPlayConfig;
import com.kwad.sdk.api.KsFeedAd;
import com.kwad.sdk.api.model.MaterialType;
import com.ubimax.api.bean.AdnReadyStatus;
import com.ubimax.api.bean.UMTConstant;
import com.ubimax.api.bean.UMTFeedExtraInfo;
import com.ubimax.feed.api.UMTCustomFeedAdBean;
import com.ubimax.feed.api.UMTFeedAdView;
import com.ubimax.sample.UMTApplication;

/**
 * @author zhanao.feng
 * @date 2024/9/5 15:29
 * @description
 */
public class KuaiShouFeedExpressAd extends UMTCustomFeedAdBean {
    private static final String TAG = KuaiShouFeedExpressAd.class.getSimpleName();
    private KsFeedAd expressResponse;

    public void init(KsFeedAd expressResponse) {
        this.expressResponse = expressResponse;
        final boolean isMute = getMuteStatus() != UMTConstant.MuteType.UNMUTE;
        expressResponse.setVideoSoundEnable(!isMute);
        int playPolicy = getVideoPlayNetType();
        int policy = KsAdVideoPlayConfig.VideoAutoPlayType.AUTO_PLAY_WIFI;
        if (playPolicy == UMTConstant.VideoPlayNetType.NOT_PLAY) {
            policy = KsAdVideoPlayConfig.VideoAutoPlayType.NO_AUTO_PLAY;
        }
        KsAdVideoPlayConfig config = new KsAdVideoPlayConfig.Builder()
                .videoAutoPlayType(policy)
                .videoSoundEnable(!isMute)
                .build();
        expressResponse.setVideoPlayConfig(config);

    }


    @Override
    public boolean isFeedExpress() {
        return true;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public AdnReadyStatus getReadyStatus() {
        return AdnReadyStatus.ADN_READY;
    }

    @Override
    public void register(Activity activity, UMTFeedAdView view, UMTFeedExtraInfo extraInfo) {
        expressResponse.setAdInteractionListener(new KsFeedAd.AdInteractionListener() {
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
            public void onDislikeClicked() {
                Log.e(TAG, "onDislikeClicked");
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
        });
    }

    @Override
    public void renderView() {
        Log.e(TAG, "renderView");
        expressResponse.render(new KsFeedAd.AdRenderListener() {
            @Override
            public void onAdRenderSuccess(View view) {
                Log.e(TAG, "onAdRenderSuccess");
                callRenderSucc();
            }

            @Override
            public void onAdRenderFailed(int i, String s) {
                Log.e(TAG, "onAdRenderFailed code:" + i + " msg:" + s);
                callRenderFail(i + "", s);
            }
        });
    }

    @Override
    public View getExpressView() {
        return expressResponse.getFeedView(UMTApplication.context);
    }


    @Override
    public long getBiddingPrice() {
        if (isClientBidding() && expressResponse != null) {
            Log.e(TAG, "price:" + expressResponse.getECPM());
            return expressResponse.getECPM();
        }
        return 0;
    }


    @Override
    public int getExpressAdMaterialType() {
        if (expressResponse != null) {
            if (expressResponse.getMaterialType() == MaterialType.VIDEO || expressResponse.getMaterialType() == MaterialType.ORIGIN_LIVE) {
                return UMTConstant.PatternType.VIDEO;
            } else if (expressResponse.getMaterialType() == MaterialType.UNKNOWN) {
                return UMTConstant.PatternType.UNKNOWN;
            } else if (expressResponse.getMaterialType() == MaterialType.SINGLE_IMG) {
                return UMTConstant.PatternType.UNDEFINED_IMG;
            } else if (expressResponse.getMaterialType() == MaterialType.SMALL_GROUP_IMG || expressResponse.getMaterialType() == MaterialType.GROUP_IMG) {
                return UMTConstant.PatternType.GROUP_IMG;
            }
        }
        return UMTConstant.PatternType.UNKNOWN;
    }

}
