package com.ubimax.sample.custom.kuaishou;

import android.content.Context;
import android.util.Log;

import com.kwad.sdk.api.KsAdSDK;
import com.kwad.sdk.api.KsFeedAd;
import com.kwad.sdk.api.KsLoadManager;
import com.kwad.sdk.api.KsNativeAd;
import com.kwad.sdk.api.KsScene;
import com.ubimax.api.UMTAdConfig;
import com.ubimax.api.bean.UMTConstant;
import com.ubimax.api.custom.UMTAdnServerConfig;
import com.ubimax.constant.ErrorConstant;
import com.ubimax.feed.api.UMTCustomFeedAdBean;
import com.ubimax.feed.api.UMTCustomFeedAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhanao.feng
 * @date 2024/9/5 15:29
 * @description
 */
public class KuaiShouFeedAdapter extends UMTCustomFeedAdapter {
    private static final String TAG = KuaiShouFeedAdapter.class.getSimpleName();


    @Override
    public void load(Context context, UMTAdConfig umtAdConfig, UMTAdnServerConfig customConfig) {
        if (getRenderType() == UMTConstant.RenderType.FEED_EXPRESS) {
            KsScene scene = new KsScene.Builder(Utils.getLongValue(customConfig.getAdnSlotId()))
                    .width((dp2px(umtAdConfig.width))).adNum(1).build();
            KsAdSDK.getLoadManager().loadConfigFeedAd(scene, new KsLoadManager.FeedAdListener() {
                @Override
                public void onError(int i, String s) {
                    Log.e(TAG, "onError code:" + i + " msg:" + s);
                    callLoadFail(i + "", s);
                }

                @Override
                public void onFeedAdLoad(List<KsFeedAd> list) {
                    try {
                        Log.e(TAG, "onFeedAdLoad");
                        List<UMTCustomFeedAdBean> tempList = new ArrayList<>();
                        for (KsFeedAd expressResponse : list) {
                            //此处必须使用createFeedAd方法创建KuaiShouFeedExpressAd对象
                            KuaiShouFeedExpressAd expressAd = createFeedAd(KuaiShouFeedExpressAd.class);
                            expressAd.init(expressResponse);
                            tempList.add(expressAd);
                        }
                        callLoadAdSucc(tempList);
                    } catch (Exception e) {
                        callLoadFail(ErrorConstant.ADN_EXCEPTION_FAIL.getCodeString(), ErrorConstant.ADN_EXCEPTION_FAIL.getMsg() + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });

        } else if (getRenderType() == UMTConstant.RenderType.FEED_NATIVE) {
            KsScene.Builder builder = new KsScene.Builder(Utils.getLongValue(customConfig.getAdnSlotId())).adNum(1);
            KsAdSDK.getLoadManager().loadNativeAd(builder.build(), new KsLoadManager.NativeAdListener() {
                @Override
                public void onError(int i, String s) {
                    Log.e(TAG, "onError code:" + i + " msg:" + s);
                    callLoadFail(i + "", s);
                }

                @Override
                public void onNativeAdLoad(List<KsNativeAd> list) {
                    try {
                        Log.e(TAG, "onFeedAdLoad");
                        List<UMTCustomFeedAdBean> tempList = new ArrayList<>();
                        for (KsNativeAd nativeAd : list) {
                            KuaiShouFeedNativeAd ksFeedNativeAd = createFeedAd(KuaiShouFeedNativeAd.class);
                            ksFeedNativeAd.init(nativeAd);
                            tempList.add(ksFeedNativeAd);
                        }
                        callLoadAdSucc(tempList);
                    } catch (Exception e) {
                        callLoadFail(ErrorConstant.ADN_EXCEPTION_FAIL.getCodeString(), ErrorConstant.ADN_EXCEPTION_FAIL.getMsg() + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Log.e(TAG, "onError code:" + ErrorConstant.ADN_RENDER_TYPE_ERROR.getCode() + " msg:" + ErrorConstant.ADN_RENDER_TYPE_ERROR.getMsg());
            callLoadFail(ErrorConstant.ADN_RENDER_TYPE_ERROR.getCodeString(), ErrorConstant.ADN_RENDER_TYPE_ERROR.getMsg());
        }


    }

    @Override
    public void destroy() {

    }
}
