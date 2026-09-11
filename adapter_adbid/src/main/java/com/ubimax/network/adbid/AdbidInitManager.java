package com.ubimax.network.adbid;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.adbid.media.AdBidPlatform;
import com.adbid.media.AdbidError;
import com.adbid.sdk.AdbidCustomController;
import com.adbid.sdk.AdbidInitConfig;
import com.adbid.sdk.AdbidLocation;
import com.adbid.sdk.AdbidSdk;
import com.adbid.sdk.AdbidSdkInitListener;
import com.ubimax.api.UMTCustomInitManager;
import com.ubimax.api.custom.UMTCustomInitConfig;
import com.ubimax.api.init.UMTInitConfig;
import com.ubimax.api.init.UMTPrivacyConfig;
import com.ubimax.api.init.UMTUserInfoConfig;
import com.ubimax.base.adapter.ProxyAdapter;
import com.ubimax.common.config.PrivacyConfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * 领摩 Adbid SDK 初始化适配，包名与 libs 中 adapter 一致：com.ubimax.network.adbid
 */
public class AdbidInitManager extends UMTCustomInitManager {
    public static final String TAG = AdbidInitManager.class.getSimpleName();
    private static final String ADAPTER_VERSION = "1.0.0";

    @Override
    public void initAdn(Context context, UMTCustomInitConfig customInitConfig) {
        if (context == null || customInitConfig == null) {
            callInitFail("-1", "context or config is null");
            return;
        }
        String appId = customInitConfig.getAdnAppId();
        if (TextUtils.isEmpty(appId)) {
            callInitFail("-1", "adn appId is empty");
            return;
        }


        UMTInitConfig umtInitConfig = customInitConfig.getUmtInitConfig();
        boolean debug = umtInitConfig != null && umtInitConfig.isDebug;
        AdbidSdk.getInstance(context).setDebugMode(debug);

        AdbidInitConfig.Builder builder = AdbidInitConfig.builder(appId);
        if (umtInitConfig != null && umtInitConfig.userInfo != null) {
            UMTUserInfoConfig userInfo = umtInitConfig.userInfo;
            if (!TextUtils.isEmpty(userInfo.getChannel())) {
                builder.setAppChannel(userInfo.getChannel());
            }
            if (!TextUtils.isEmpty(userInfo.getUserId())) {
                builder.setUserId(userInfo.getUserId());
            }
        }
        builder.addCustomController(createController(customInitConfig));

        AdbidSdk.getInstance(context).initialize(builder.build(), new AdbidSdkInitListener() {
            @Override
            public void onSdkInitCallback(boolean isSuccess, AdbidError adbidError) {
                if (isSuccess) {
                    Log.i(TAG, "init onSuccess");
                    callInitSuccess();
                } else {
                    String msg = adbidError != null ? adbidError.getMessage() : "unknown";
                    int code = adbidError != null ? adbidError.getCode() : -1;
                    Log.e(TAG, "init onFail code:" + code + " msg:" + msg);
                    callInitFail(String.valueOf(code), msg);
                }
            }
        });
    }

    private AdbidCustomController createController(final UMTCustomInitConfig customInitConfig) {
        return new AdbidCustomController() {
            private UMTPrivacyConfig privacy() {
                if (customInitConfig.getUmtInitConfig() != null) {
                    return customInitConfig.getUmtInitConfig().privacyConfig;
                }
                return null;
            }

            @Override
            public boolean isCanUsePhoneState() {
                UMTPrivacyConfig privacy = privacy();
                if (privacy != null) {
                    return privacy.isCanUsePhoneState();
                }
                return PrivacyConfig.isCanUseReadPhoneState;
            }

            @Override
            public boolean isSupportPersonalized() {
                UMTPrivacyConfig privacy = privacy();
                if (privacy != null) {
                    return privacy.canPersonalRecommend();
                }
                return PrivacyConfig.isGetPersonAds;
            }

            @Override
            public boolean isCanUseLocation() {
                UMTPrivacyConfig privacy = privacy();
                if (privacy != null) {
                    return privacy.isCanUseLocation();
                }
                return PrivacyConfig.isCanUseLocation;
            }

            @Override
            public boolean isCanUseWifiState() {
                UMTPrivacyConfig privacy = privacy();
                if (privacy != null) {
                    return privacy.isCanUseWifiState();
                }
                return PrivacyConfig.isCanUseWifiState;
            }

            @Override
            public boolean isCanUseOaid() {
                UMTPrivacyConfig privacy = privacy();
                if (privacy != null) {
                    return privacy.isCanUseOaid();
                }
                return PrivacyConfig.isCanUseOaid;
            }

            @Nullable
            @Override
            public String getDevOaid() {
                String oaid = getUMTOaid();
                if (!TextUtils.isEmpty(oaid)) {
                    return oaid;
                }
                UMTPrivacyConfig privacy = privacy();
                if (privacy != null) {
                    return privacy.getDevOaid();
                }
                return PrivacyConfig.oaid;
            }

            @Override
            public boolean isCanUseAppList() {
                UMTPrivacyConfig privacy = privacy();
                if (privacy != null) {
                    return privacy.canReadInstalledPackages();
                }
                return PrivacyConfig.isCanAppList;
            }

            @Nullable
            @Override
            public List<PackageInfo> getAppList() {
                return Collections.emptyList();
            }

            @Override
            public boolean isCanUseAndroidId() {
                UMTPrivacyConfig privacy = privacy();
                if (privacy != null) {
                    return privacy.isCanUseAndroidId();
                }
                return PrivacyConfig.isCanUseAndroidId;
            }

            @Nullable
            @Override
            public String getAndroidId() {
                String androidId = getUMTAndroidId();
                if (!TextUtils.isEmpty(androidId)) {
                    return androidId;
                }
                UMTPrivacyConfig privacy = privacy();
                if (privacy != null) {
                    return privacy.getAndroidId();
                }
                return PrivacyConfig.androidId;
            }

            @Override
            public boolean isCanUseMacAddress() {
                UMTPrivacyConfig privacy = privacy();
                if (privacy != null) {
                    return privacy.isCanUseMacAddress();
                }
                return PrivacyConfig.isCanUseMacAddress;
            }

            @Nullable
            @Override
            public String getMacAddress() {
                UMTPrivacyConfig privacy = privacy();
                if (privacy != null) {
                    return privacy.getMacAddress();
                }
                return PrivacyConfig.mac;
            }

            @Override
            public boolean isCanUseWriteExternal() {
                UMTPrivacyConfig privacy = privacy();
                if (privacy != null) {
                    return privacy.isCanUseWriteExternal();
                }
                return PrivacyConfig.isCanUseWriteExternal;
            }

            @Override
            public boolean isCanUseShakeAd() {
                return true;
            }

            @Override
            public boolean isCanUseRecordAudio() {
                return false;
            }

            @Nullable
            @Override
            public String getDevImei() {
                UMTPrivacyConfig privacy = privacy();
                if (privacy != null) {
                    return privacy.getDevImei();
                }
                return PrivacyConfig.imei;
            }

            @Nullable
            @Override
            public String[] getDevImeiList() {
                return new String[0];
            }

            @Nullable
            @Override
            public AdbidLocation getLocation() {
                return null;
            }

            @Override
            public boolean isCanUseIP() {
                return true;
            }

            @Nullable
            @Override
            public String getIP() {
                return "";
            }
        };
    }

    @Override
    public HashMap<Integer, Class<? extends ProxyAdapter>> initAdapter() {
        HashMap<Integer, Class<? extends ProxyAdapter>> map = new HashMap<>();
        // 与 libs 中 kuaishou/blk adapter 一致：1开屏 2信息流 3激励 4Banner 5插屏
        map.put(1, AdbidSplashAdapter.class);
        map.put(2, AdbidFeedAdapter.class);
        map.put(3, AdbidRewardAdapter.class);
        map.put(4, AdbidBannerAdapter.class);
        map.put(5, AdbidInterstitialAdapter.class);
        return map;
    }

    @Override
    public String getAdapterVersion() {
        return ADAPTER_VERSION;
    }

    @Override
    public String getAdnSDKVersion() {
        return AdbidSdk.VERSION;
    }

    @Override
    public String getAdnInitClassPath() {
        return "com.adbid.sdk.AdbidSdk";
    }
}
