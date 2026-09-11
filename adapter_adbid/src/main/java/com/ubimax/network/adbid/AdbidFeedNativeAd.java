package com.ubimax.network.adbid;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adbid.media.AdMaterialType;
import com.adbid.media.AdbidAdInfo;
import com.adbid.media.AdbidError;
import com.adbid.media.ad.AdbidNativeLoader;
import com.adbid.media.nativeAd.AdbidAppDownLoadListener;
import com.adbid.media.nativeAd.AdbidCustomVideo;
import com.adbid.media.nativeAd.AdbidNativeAd;
import com.adbid.media.nativeAd.AdbidNativeAdView;
import com.adbid.media.nativeAd.AdbidNativeAppInfo;
import com.adbid.media.nativeAd.AdbidNativeEventListener;
import com.adbid.media.nativeAd.AdbidNativeVideoListener;
import com.ubimax.api.bean.AdnReadyStatus;
import com.ubimax.api.bean.BiddingFeedBackBean;
import com.ubimax.api.bean.UMTConstant;
import com.ubimax.api.bean.UMTFeedExtraInfo;
import com.ubimax.api.bean.UMTFeedMaterial;
import com.ubimax.api.bean.UMTImage;
import com.ubimax.api.bean.UMTVideo;
import com.ubimax.constant.ErrorConstant;
import com.ubimax.feed.api.UMTAbsFeedMaterial;
import com.ubimax.feed.api.UMTCustomFeedAdBean;
import com.ubimax.feed.api.UMTFeedAdView;
import com.ubimax.utils.BaseUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 自渲染信息流广告
 */
public class AdbidFeedNativeAd extends UMTCustomFeedAdBean {
    private static final String TAG = AdbidFeedNativeAd.class.getSimpleName();
    private AdbidNativeLoader nativeLoader;
    private AdbidNativeAd nativeAd;
    private AdbidNativeAdView adbidNativeAdView;
    private int currentDownloadProgress;

    public void init(AdbidNativeLoader loader, final AdbidNativeAd ad) {
        this.nativeLoader = loader;
        this.nativeAd = ad;
        // 仅媒体显式设置静音/非静音时下发；DEFAULT 不干预，交给 Adbid 默认/云端静音
        if (getMuteStatus() > UMTConstant.MuteType.DEFAULT) {
            ad.setMuted(getMuteStatus() != UMTConstant.MuteType.UNMUTE);
        }
        UMTAbsFeedMaterial feedMaterial = new UMTAbsFeedMaterial() {
            @Override
            public String getTitle() {
                return ad.getTitle();
            }

            @Override
            public String getDescription() {
                return ad.getDescriptionText();
            }

            @Override
            public String getSource() {
                return ad.getAdFrom();
            }

            @Override
            public int getInteractionType() {
                return ad.isDownload()
                        ? UMTConstant.FeedInteractionType.DOWNLOAD
                        : UMTConstant.FeedInteractionType.LANDING_PAGE;
            }

            @Override
            public boolean isVideo() {
                return ad.getAdMaterialType() == AdMaterialType.VIDEO;
            }

            @Override
            public DownloadAppInfo getDownloadAppInfo() {
                final AdbidNativeAppInfo appInfo = ad.getNativeAppInfo();
                if (appInfo == null) {
                    return null;
                }
                return new UMTFeedMaterial.DownloadAppInfo() {
                    @Override
                    public String getAppName() {
                        return appInfo.getAppName();
                    }

                    @Override
                    public String getAppVersionName() {
                        return appInfo.getAppVersion();
                    }

                    @Override
                    public String getAppPublisher() {
                        return appInfo.getPublisher();
                    }

                    @Override
                    public String getPrivacyAgreementUrl() {
                        return appInfo.getAppPrivacyUrl();
                    }

                    @Override
                    public String getPermissionUrl() {
                        return appInfo.getAppPermissonUrl();
                    }

                    @Override
                    public String getFunctionUrl() {
                        return appInfo.getFunctionUrl();
                    }

                    @Override
                    public long getAppSize() {
                        return 0;
                    }
                };
            }

            @Override
            public View getAdMediaView() {
                return getNativeMediaView();
            }

            @Override
            public UMTImage getMainImgInfo() {
                if (!TextUtils.isEmpty(ad.getMainImageUrl())) {
                    return new UMTImage(ad.getMainImageUrl());
                }
                return null;
            }

            @Override
            public UMTVideo getVideoInfo() {
                AdbidCustomVideo customVideo = ad.getCustomVideo();
                if (customVideo == null) {
                    return null;
                }
                UMTVideo umtVideo = new UMTVideo(
                        customVideo.getVideoUrl(),
                        ad.getVideoDuration(),
                        customVideo.getVideoWidth(),
                        customVideo.getVideoHeight(),
                        0);
                if (!TextUtils.isEmpty(customVideo.getCoverUrl())) {
                    umtVideo.setVideoCoverImgInfo(new UMTImage(customVideo.getCoverUrl()));
                }
                return umtVideo;
            }

            @Override
            public UMTImage getIconMaterialInfo() {
                if (!TextUtils.isEmpty(ad.getIconImgUrl())) {
                    return new UMTImage(ad.getIconImgUrl());
                }
                return null;
            }

            @Override
            public UMTImage getLogoMaterialInfo() {
                if (ad.getAdLogoBitmap() != null) {
                    return new UMTImage(ad.getAdLogoBitmap());
                }
                return null;
            }

            @Override
            public List<UMTImage> getImageList() {
                List<String> urls = ad.getImageUrlList();
                if (urls == null || urls.isEmpty()) {
                    return null;
                }
                List<UMTImage> images = new ArrayList<>();
                for (String url : urls) {
                    if (!TextUtils.isEmpty(url)) {
                        images.add(new UMTImage(url));
                    }
                }
                return images;
            }

            @Override
            public String getCTAText() {
                return ad.getCallToAction();
            }

            @Override
            public int getAdPatternType() {
                AdMaterialType type = ad.getAdMaterialType();
                if (type == AdMaterialType.VIDEO) {
                    return UMTConstant.PatternType.VIDEO;
                } else if (type == AdMaterialType.MULTIPLE_IMAGE) {
                    return UMTConstant.PatternType.GROUP_IMG;
                } else if (type == AdMaterialType.IMAGE) {
                    return UMTConstant.PatternType.UNDEFINED_IMG;
                }
                return UMTConstant.PatternType.UNKNOWN;
            }
        };
        setMaterialInfo(feedMaterial);
        try {
            reportMaterial(createReportMaterialWrapper(feedMaterial));
        } catch (Throwable t) {
            showException(t);
        }
    }

    @Override
    public View getNativeMediaView() {
        if (nativeAd == null) {
            return null;
        }
        try {
            View mediaView = nativeAd.getMediaView();
            // 媒体侧常直接 addView；若仍挂在 ADN 内部父布局会崩溃
            if (mediaView != null && mediaView.getParent() instanceof ViewGroup) {
                ((ViewGroup) mediaView.getParent()).removeView(mediaView);
            }
            return mediaView;
        } catch (Throwable t) {
            showException(t);
            return null;
        }
    }

    @Override
    public long getBiddingPrice() {
        long price = 0;
        if (isClientBidding() && nativeAd != null && nativeAd.getAdbidAdInfo() != null) {
            price = Math.round(nativeAd.getAdbidAdInfo().getPrice());
        }
        Log.i(TAG, "getBiddingPrice price:" + price);
        return price;
    }

    @Override
    public boolean isFeedExpress() {
        return false;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        if (nativeAd != null) {
            nativeAd.destroy();
            nativeAd = null;
        }
        if (adbidNativeAdView != null) {
            adbidNativeAdView.removeAllViews();
            adbidNativeAdView = null;
        }
        nativeLoader = null;
    }

    @Override
    public AdnReadyStatus getReadyStatus() {
        if (nativeAd != null && nativeAd.isReady()) {
            return AdnReadyStatus.ADN_READY;
        }
        return AdnReadyStatus.ADN_NOT_READY;
    }

    /**
     * 对齐 YLH：通过 {@link #getCustomAdContainer()} 把 ADN 容器交给 SDK 包裹自渲染内容，
     * 最终层级为 UMTFeedAdView -> AdbidNativeAdView -> selfRender，避免抢走宿主 root 的 parent。
     */
    @Override
    public ViewGroup getCustomAdContainer() {
        Log.i(TAG, "getCustomAdContainer");
        if (nativeAd != null) {
            android.content.Context ctx = BaseUtils.getContext();
            if (ctx == null) {
                Log.e(TAG, "getCustomAdContainer context is null");
                return null;
            }
            adbidNativeAdView = new ContextSafeNativeAdView(ctx);
        }
        return adbidNativeAdView;
    }

    @Override
    public void register(Activity activity, UMTFeedAdView view, UMTFeedExtraInfo extraInfo) {
        Log.i(TAG, "register");
        if (nativeAd == null || view == null || extraInfo == null) {
            callAdShowError(ErrorConstant.ADN_SHOW_FAIL.getCodeString(), "nativeAd or view is null");
            return;
        }
        try {
            // SDK registerInternal 已通过 getCustomAdContainer() 完成包裹
            AdbidNativeAdView container = adbidNativeAdView;
            if (container == null) {
                callAdShowError(ErrorConstant.ADN_SHOW_FAIL.getCodeString(), "adbidNativeAdView is null");
                return;
            }
            List<View> clickViews = extraInfo.getClickViewList();
            if (clickViews == null) {
                clickViews = new ArrayList<>();
            }
            nativeAd.setEventListener(new AdbidNativeEventListener() {
                @Override
                public void onImpression(@NonNull AdbidNativeAdView adView, @NonNull AdbidAdInfo adInfo) {
                    Log.i(TAG, "onImpression");
                    callAdShow();
                }

                @Override
                public void onNativeAdClick(@NonNull AdbidNativeAdView adView, @NonNull AdbidAdInfo adInfo) {
                    Log.i(TAG, "onNativeAdClick");
                    callAdClick();
                }

                @Override
                public void onAdClose(@Nullable AdbidNativeAdView adView) {
                    Log.i(TAG, "onAdClose");
                    callAdDismiss();
                }
            });
            nativeAd.setDownLoadListener(new AdbidAppDownLoadListener() {
                @Override
                public void onDownloadPaused(int progress) {
                    currentDownloadProgress = progress;
                    callDownloadPause(getDownloadAppTotalBytes(), getDownloadAppCurrentBytes(progress), "", getDownloadAppName());
                }

                @Override
                public void onDownloadStarted() {
                    callDownloadIdle();
                }

                @Override
                public void onDownloadProgressUpdate(int progress) {
                    currentDownloadProgress = progress;
                    callDownloadActive(getDownloadAppTotalBytes(), getDownloadAppCurrentBytes(progress), "", getDownloadAppName());
                }

                @Override
                public void onDownloadFinished() {
                    callDownloadFinish(getDownloadAppTotalBytes(), "", getDownloadAppName());
                }

                @Override
                public void onDownloadResume(int progress) {
                    currentDownloadProgress = progress;
                    callDownloadActive(getDownloadAppTotalBytes(), getDownloadAppCurrentBytes(progress), "", getDownloadAppName());
                }

                @Override
                public void onDownloadFailed(AdbidError error) {
                    callDownloadError(getDownloadAppTotalBytes(), -1, "", getDownloadAppName());
                }

                @Override
                public void onInstalled() {
                    callAPKInstall("", getDownloadAppName());
                }
            });
            nativeAd.setVideoListener(new AdbidNativeVideoListener() {
                @Override
                public void onVideoStart() {
                    callVideoStart();
                }

                @Override
                public void onVideoPause() {
                    callVideoPause();
                }

                @Override
                public void onVideoResume() {
                    callVideoResume();
                }

                @Override
                public void onVideoComplete() {
                    callVideoFinish();
                }

                @Override
                public void onVideoError(AdbidError error) {
                    String msg = error != null ? error.getMessage() : "";
                    int code = error != null ? error.getCode() : -1;
                    callVideoError(String.valueOf(code), msg);
                }

                @Override
                public void onVideoProgressUpdate(long currentLength, long allLength) {
                    callVideoProgressUpdate(currentLength, allLength);
                }
            });
            nativeAd.registerViews(container, clickViews, extraInfo.getCloseView());
            int playPolicy = getVideoPlayNetType();
            if (playPolicy == UMTConstant.VideoPlayNetType.NOT_PLAY) {
                pauseVideo();
            } else if (playPolicy != UMTConstant.VideoPlayNetType.DEFAULT) {
                startVideo();
            }
        } catch (Throwable t) {
            showException(t);
            callAdShowError(ErrorConstant.ADN_EXCEPTION_FAIL.getCodeString(), t.getMessage());
        }
    }

    @Override
    public void startVideo() {
        if (nativeAd != null) {
            nativeAd.startVideo();
        }
    }

    @Override
    public void pauseVideo() {
        if (nativeAd != null) {
            nativeAd.pauseVideo();
        }
    }

    @Override
    public void setVideoMute(boolean mute) {
        if (nativeAd != null) {
            nativeAd.setMuted(mute);
        }
    }

    @Override
    public void notifyWin(BiddingFeedBackBean bean) {
        Log.i(TAG, "notifyWin");
        if (nativeLoader != null) {
            nativeLoader.winNotice(AdbidBiddingUtils.getSecondPrice(bean));
        }
    }

    @Override
    public void notifyLoss(BiddingFeedBackBean bean) {
        Log.i(TAG, "notifyLoss");
        if (nativeLoader != null) {
            nativeLoader.lossNotice(AdbidBiddingUtils.getFailInfo(bean));
        }
    }

    @Override
    public long getDownloadAppCurrentBytes(int progress) {
        return progress;
    }

    /**
     * 避免与宿主 Context 生命周期强耦合的简单包装。
     */
    private static class ContextSafeNativeAdView extends AdbidNativeAdView {
        ContextSafeNativeAdView(android.content.Context context) {
            super(context);
        }
    }
}
