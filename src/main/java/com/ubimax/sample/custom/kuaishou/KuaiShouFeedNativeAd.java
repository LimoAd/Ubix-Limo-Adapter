package com.ubimax.sample.custom.kuaishou;

import android.app.Activity;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;

import com.kwad.sdk.api.KsAdVideoPlayConfig;
import com.kwad.sdk.api.KsApkDownloadListener;
import com.kwad.sdk.api.KsImage;
import com.kwad.sdk.api.KsNativeAd;
import com.kwad.sdk.api.model.AdSourceLogoType;
import com.kwad.sdk.api.model.InteractionType;
import com.kwad.sdk.api.model.MaterialType;
import com.ubimax.api.bean.AdnReadyStatus;
import com.ubimax.api.bean.UMTConstant;
import com.ubimax.api.bean.UMTFeedExtraInfo;
import com.ubimax.api.bean.UMTFeedMaterial;
import com.ubimax.api.bean.UMTImage;
import com.ubimax.api.bean.UMTVideo;
import com.ubimax.feed.api.UMTAbsFeedMaterial;
import com.ubimax.feed.api.UMTCustomFeedAdBean;
import com.ubimax.feed.api.UMTFeedAdView;
import com.ubimax.feed.common.UMTMaterialInfo;
import com.ubimax.sample.UMTApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhanao.feng
 * @date 2024/9/5 15:29
 * @description
 */
public class KuaiShouFeedNativeAd extends UMTCustomFeedAdBean {
    private static final String TAG = KuaiShouFeedNativeAd.class.getSimpleName();
    KsNativeAd nativeAd;


    public void init(final KsNativeAd nativeAd) {
        this.nativeAd = nativeAd;
        UMTAbsFeedMaterial feedMaterial = new UMTAbsFeedMaterial() {
            @Override
            public String getTitle() {
                return nativeAd.getProductName();
            }

            @Override
            public String getDescription() {
                return nativeAd.getAdDescription();
            }

            @Override
            public String getSource() {
                return nativeAd.getAdSource();
            }

            @Override
            public int getInteractionType() {
                if (nativeAd.getInteractionType() == InteractionType.DOWNLOAD) {
                    return UMTConstant.FeedInteractionType.DOWNLOAD;
                } else if (nativeAd.getInteractionType() == InteractionType.H5) {
                    return UMTConstant.FeedInteractionType.LANDING_PAGE;
                }
                return UMTConstant.FeedInteractionType.UNKNOWN;
            }

            @Override
            public boolean isVideo() {
                return nativeAd.getMaterialType() == MaterialType.VIDEO || nativeAd.getMaterialType() == MaterialType.ORIGIN_LIVE;
            }

            @Override
            public DownloadAppInfo getDownloadAppInfo() {
                return new UMTFeedMaterial.DownloadAppInfo() {
                    @Override
                    public String getAppName() {
                        return nativeAd.getAppName();
                    }

                    @Override
                    public String getAppVersionName() {
                        return nativeAd.getAppVersion();
                    }

                    @Override
                    public String getAppPublisher() {
                        return nativeAd.getCorporationName();
                    }

                    @Override
                    public String getPrivacyAgreementUrl() {
                        return nativeAd.getAppPrivacyUrl();
                    }

                    @Override
                    public String getPermissionUrl() {
                        return nativeAd.getPermissionInfoUrl();
                    }

                    @Override
                    public String getFunctionUrl() {
                        return nativeAd.getIntroductionInfoUrl();
                    }

                    @Override
                    public long getAppSize() {
                        return nativeAd.getAppPackageSize();
                    }
                };
            }

            @Override
            public View getAdMediaView() {
                return getNativeMediaView();
            }

            @Override
            public UMTImage getMainImgInfo() {
                return null;
            }

            @Override
            public UMTVideo getVideoInfo() {
                if (nativeAd.getMaterialType() == MaterialType.VIDEO || nativeAd.getMaterialType() == MaterialType.ORIGIN_LIVE) {
                    UMTVideo umtVideo = new UMTVideo(nativeAd.getVideoUrl(), nativeAd.getVideoDuration() * 1000L, nativeAd.getVideoWidth(), nativeAd.getVideoHeight(), 0);
                    if (nativeAd.getVideoCoverImage() != null) {
                        umtVideo.setVideoCoverImgInfo(new UMTImage(nativeAd.getVideoCoverImage().getImageUrl(), nativeAd.getVideoCoverImage().getWidth(), nativeAd.getVideoCoverImage().getHeight()));
                    }
                    return umtVideo;
                }
                return null;
            }

            @Override
            public UMTImage getIconMaterialInfo() {
                return new UMTImage(nativeAd.getAppIconUrl());
            }

            @Override
            public UMTImage getLogoMaterialInfo() {
                return new UMTImage(nativeAd.getAdSourceLogoUrl(AdSourceLogoType.NORMAL));
            }

            @Override
            public List<UMTImage> getImageList() {
                List<KsImage> imageList = nativeAd.getImageList();
                if (imageList != null && !imageList.isEmpty()) {
                    List<UMTImage> umtImages = new ArrayList<>();
                    for (int i = 0; i < imageList.size(); i++) {
                        UMTImage umtImage = new UMTImage(imageList.get(i).getImageUrl(), imageList.get(i).getWidth(), imageList.get(i).getHeight());
                        umtImages.add(umtImage);
                    }
                    return umtImages;
                }
                return null;
            }

            @Override
            public Map<String, Object> getExtraInfo() {
                return null;
            }

            @Override
            public String getCTAText() {
                return nativeAd.getActionDescription();
            }

            @Override
            public double getAppScore() {
                return nativeAd.getAppScore();
            }

            @Override
            public int getAdPatternType() {
                if (nativeAd.getMaterialType() == MaterialType.VIDEO || nativeAd.getMaterialType() == MaterialType.ORIGIN_LIVE) {
                    return UMTConstant.PatternType.VIDEO;
                } else if (nativeAd.getMaterialType() == MaterialType.SINGLE_IMG) {
                    return UMTConstant.PatternType.UNDEFINED_IMG;
                } else if (nativeAd.getMaterialType() == MaterialType.GROUP_IMG || nativeAd.getMaterialType() == MaterialType.SMALL_GROUP_IMG) {
                    return UMTConstant.PatternType.GROUP_IMG;
                }
                return UMTConstant.PatternType.UNKNOWN;
            }

            @Override
            public String getDownloadCountText() {
                return nativeAd.getAppDownloadCountDes();
            }

            @Override
            public double getAppPrice() {
                return 0;
            }

            @Override
            public String getAppPackageName() {
                return nativeAd.getAppPackageName();
            }
        };
        setMaterialInfo(feedMaterial);
    }

    @Override
    public View getNativeMediaView() {
        Log.e(TAG, "getMediaView");
        try {
            if (nativeAd.getMaterialType() == MaterialType.VIDEO || nativeAd.getMaterialType() == MaterialType.ORIGIN_LIVE) {
                final boolean isMute = getMuteStatus() != UMTConstant.MuteType.UNMUTE;
                int playPolicy = getVideoPlayNetType();
                int policy = KsAdVideoPlayConfig.VideoAutoPlayType.AUTO_PLAY_WIFI;
                if (playPolicy == UMTConstant.VideoPlayNetType.NOT_PLAY) {
                    policy = KsAdVideoPlayConfig.VideoAutoPlayType.NO_AUTO_PLAY;
                }
                KsAdVideoPlayConfig.Builder builder = new KsAdVideoPlayConfig.Builder()
                        .videoAutoPlayType(policy)
                        .videoSoundEnable(!isMute);
                return nativeAd.getVideoView(UMTApplication.context, builder.build());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getBiddingPrice() {
        if (isClientBidding() && nativeAd != null) {
            Log.e(TAG, "price:" + nativeAd.getECPM());
            return nativeAd.getECPM();
        }
        return 0;
    }


    @Override
    public boolean isFeedExpress() {
        return false;
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
        Log.e(TAG, "register");
        if (activity != null) {
            nativeAd.registerViewForInteraction(activity, view, extraInfo.getClickViewList(), new KsNativeAd.AdInteractionListener() {
                @Override
                public void onAdClicked(View view, KsNativeAd ksNativeAd) {
                    Log.e(TAG, "onAdClicked");
                    callAdClick();

                }

                @Override
                public void onAdShow(KsNativeAd ksNativeAd) {
                    Log.e(TAG, "onAdShow");
                    callAdShow();
                }

                @Override
                public boolean handleDownloadDialog(DialogInterface.OnClickListener onClickListener) {
                    return false;
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
            nativeAd.setDownloadListener(new KsApkDownloadListener() {
                @Override
                public void onPaused(int i) {
                    showLog(TAG, "onPaused");
                    callDownloadPause(getDownloadAppTotalBytes(), getDownloadAppCurrentBytes(i), "", getDownloadAppName());
                }

                @Override
                public void onIdle() {
                    showLog(TAG, "onIdle");
                    callDownloadIdle();
                }

                @Override
                public void onDownloadStarted() {
                    showLog(TAG, "onDownloadStarted");
                }

                @Override
                public void onProgressUpdate(int i) {
                    showLog(TAG, "onProgressUpdate");
                    callDownloadActive(getDownloadAppTotalBytes(), getDownloadAppCurrentBytes(i), "", getDownloadAppName());

                }

                @Override
                public void onDownloadFinished() {
                    showLog(TAG, "onDownloadFinished");
                    callDownloadFinish(getDownloadAppTotalBytes(), "", getDownloadAppName());

                }

                @Override
                public void onInstalled() {
                    showLog(TAG, "onInstalled");
                    callAPKInstall("", getDownloadAppName());
                }

                @Override
                public void onDownloadFailed() {
                    showLoge(TAG, "onDownloadFailed");
                    callDownloadError(getDownloadAppTotalBytes(), -1, "", getDownloadAppName());
                }
            });
            nativeAd.setVideoPlayListener(new KsNativeAd.VideoPlayListener() {
                @Override
                public void onVideoPlayReady() {
                    Log.e(TAG, "onVideoPlayReady");
                }

                @Override
                public void onVideoPlayStart() {
                    Log.e(TAG, "onVideoPlayStart");
                    callVideoStart();
                }

                @Override
                public void onVideoPlayComplete() {
                    Log.e(TAG, "onVideoPlayComplete");
                    callVideoFinish();
                }

                @Override
                public void onVideoPlayError(int i, int i1) {
                    Log.e(TAG, "onVideoPlayError" + i + "  i1:" + i1);
                    callVideoError(i + "", i1 + "");
                }

                @Override
                public void onVideoPlayPause() {
                    Log.e(TAG, "onVideoPlayPause");
                    callVideoPause();
                }

                @Override
                public void onVideoPlayResume() {
                    Log.e(TAG, "onVideoPlayResume");
                    callVideoResume();
                }
            });
        }

    }


}
