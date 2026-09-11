package com.ubimax.network.adbid;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.adbid.media.AdbidError;
import com.adbid.media.ad.AdbidNativeLoader;
import com.adbid.media.nativeAd.AdbidNativeAd;
import com.adbid.media.nativeOverseas.NativeAdbidLoadListener;
import com.ubimax.api.UMTAdConfig;
import com.ubimax.api.bean.UMTConstant;
import com.ubimax.api.custom.UMTAdnServerConfig;
import com.ubimax.constant.ErrorConstant;
import com.ubimax.feed.api.UMTCustomFeedAdBean;
import com.ubimax.feed.api.UMTCustomFeedAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 信息流广告适配（Adbid 文档仅提供自渲染 Native）
 */
public class AdbidFeedAdapter extends UMTCustomFeedAdapter {
    private static final String TAG = AdbidFeedAdapter.class.getSimpleName();
    private AdbidNativeLoader nativeLoader;

    @Override
    public void load(Context context, UMTAdConfig umtAdConfig, final UMTAdnServerConfig customConfig) {
        if (getRenderType() != UMTConstant.RenderType.FEED_NATIVE) {
            Log.e(TAG, "unsupported render type:" + getRenderType());
            callLoadFail(ErrorConstant.ADN_RENDER_TYPE_ERROR.getCodeString(),
                    ErrorConstant.ADN_RENDER_TYPE_ERROR.getMsg());
            return;
        }
        Log.i(TAG, "load slot:" + customConfig.getAdnSlotId());
        destroyInner();
        nativeLoader = new AdbidNativeLoader(context, customConfig.getAdnSlotId(),
                new NativeAdbidLoadListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull AdbidNativeAd nativeAd) {
                        try {
                            Log.i(TAG, "onNativeAdLoaded");
                            List<UMTCustomFeedAdBean> list = new ArrayList<>();
                            AdbidFeedNativeAd feedNativeAd = createFeedAd(AdbidFeedNativeAd.class);
                            feedNativeAd.init(nativeLoader, nativeAd);
                            list.add(feedNativeAd);
                            callLoadAdSucc(list);
                        } catch (Throwable e) {
                            callLoadFail(ErrorConstant.ADN_EXCEPTION_FAIL.getCodeString(),
                                    ErrorConstant.ADN_EXCEPTION_FAIL.getMsg() + e.getMessage());
                        }
                    }

                    @Override
                    public void onNativeAdLoadFail(@NonNull AdbidError adError) {
                        Log.e(TAG, "onNativeAdLoadFail:" + adError.getMessage());
                        callLoadFail(String.valueOf(adError.getCode()), adError.getMessage());
                    }
                });
        // 仅媒体显式设置静音/非静音时下发；DEFAULT 不干预，交给 Adbid 默认/云端静音
        if (getMuteStatus() > UMTConstant.MuteType.DEFAULT) {
            nativeLoader.setAdMuted(getMuteStatus() != UMTConstant.MuteType.UNMUTE);
        }
        nativeLoader.loadAd();
    }

    @Override
    public void destroy() {
        destroyInner();
    }

    private void destroyInner() {
        nativeLoader = null;
    }
}
