package com.ubimax.sample.custom.kuaishou;

import android.content.Context;
import android.util.Log;

import com.kwad.sdk.api.KsAdSDK;
import com.kwad.sdk.api.KsCustomController;
import com.kwad.sdk.api.KsInitCallback;
import com.kwad.sdk.api.SdkConfig;
import com.ubimax.api.UMTCustomInitManager;
import com.ubimax.api.custom.UMTCustomInitConfig;

import java.util.List;

/**
 * @author zhanao.feng
 * @date 2024/8/7 16:24
 * @description
 */
public class KuaiShouInitManager extends UMTCustomInitManager {
    public static final String TAG = KuaiShouInitManager.class.getSimpleName();

    @Override
    public void initAdn(Context context, UMTCustomInitConfig customInitConfig) {
        KsAdSDK.init(context,
                new SdkConfig.Builder()
                        .appId(customInitConfig.getAdnAppId())
                        .showNotification(true)
                        .debug(customInitConfig.getUmtInitConfig().isDebug)
                        .customController(new KsCustomController() {
                            @Override
                            public boolean canReadLocation() {
                                if (customInitConfig.getUmtInitConfig().privacyConfig != null) {
                                    return customInitConfig.getUmtInitConfig().privacyConfig.isCanUseLocation();
                                }
                                return true;
                            }

                            @Override
                            public boolean canUsePhoneState() {
                                if (customInitConfig.getUmtInitConfig().privacyConfig != null) {
                                    return customInitConfig.getUmtInitConfig().privacyConfig.isCanUsePhoneState();
                                }
                                return true;
                            }

                            @Override
                            public String getImei() {
                                if (customInitConfig.getUmtInitConfig().privacyConfig != null) {
                                    return customInitConfig.getUmtInitConfig().privacyConfig.getDevImei();
                                }
                                return "";
                            }

                            @Override
                            public String[] getImeis() {
                                return super.getImeis();
                            }

                            @Override
                            public String getAndroidId() {
                                return getUMTAndroidId();
                            }

                            @Override
                            public boolean canUseOaid() {
                                if (customInitConfig.getUmtInitConfig().privacyConfig != null) {
                                    return customInitConfig.getUmtInitConfig().privacyConfig.isCanUseOaid();
                                }
                                return true;
                            }

                            @Override
                            public String getOaid() {
                                return getUMTOaid();
                            }

                            @Override
                            public boolean canUseMacAddress() {
                                if (customInitConfig.getUmtInitConfig().privacyConfig != null) {
                                    return customInitConfig.getUmtInitConfig().privacyConfig.isCanUseMacAddress();
                                }
                                return true;
                            }

                            @Override
                            public String getMacAddress() {
                                if (customInitConfig.getUmtInitConfig().privacyConfig != null) {
                                    return customInitConfig.getUmtInitConfig().privacyConfig.getMacAddress();
                                }
                                return "";
                            }

                            @Override
                            public boolean canUseNetworkState() {
                                if (customInitConfig.getUmtInitConfig().privacyConfig != null) {
                                    return customInitConfig.getUmtInitConfig().privacyConfig.isCanUseWifiState();
                                }
                                return true;
                            }

                            @Override
                            public boolean canUseStoragePermission() {
                                if (customInitConfig.getUmtInitConfig().privacyConfig != null) {
                                    return customInitConfig.getUmtInitConfig().privacyConfig.isCanUseWriteExternal();
                                }
                                return true;
                            }

                            @Override
                            public boolean canReadInstalledPackages() {
                                if (customInitConfig.getUmtInitConfig().privacyConfig != null) {
                                    return customInitConfig.getUmtInitConfig().privacyConfig.canReadInstalledPackages();
                                }
                                return true;
                            }

                            @Override
                            public List<String> getInstalledPackages() {
                                if (customInitConfig.getUmtInitConfig().privacyConfig != null) {
                                    return customInitConfig.getUmtInitConfig().privacyConfig.getAppList();
                                }
                                return null;
                            }
                        })
                        .setStartCallback(new KsInitCallback() {
                            @Override
                            public void onSuccess() {
                                Log.e(TAG, "init onSuccess");
                                callInitSuccess();
                            }

                            @Override
                            public void onFail(int i, String s) {
                                Log.e(TAG, "init onFail code:" + i + " msg:" + s);
                                callInitFail(i + "", s);

                            }
                        }).build());
        KsAdSDK.start();
        if (customInitConfig.getUmtInitConfig().privacyConfig != null) {
            KsAdSDK.setPersonalRecommend(customInitConfig.getUmtInitConfig().privacyConfig.canPersonalRecommend());
        }
        if (customInitConfig.getUmtInitConfig().privacyConfig != null) {
            KsAdSDK.setProgrammaticRecommend(customInitConfig.getUmtInitConfig().privacyConfig.isProgrammaticRecommend());
        }
    }

    @Override
    public String getAdapterVersion() {
        return "1.0.0";
    }


    @Override
    public String getAdnSDKVersion() {
        return KsAdSDK.getSDKVersion();
    }

    public String getAdnInitClassPath() {
        return "com.kwad.sdk.api.KsAdSDK";
    }
}
