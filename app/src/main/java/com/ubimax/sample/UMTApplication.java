package com.ubimax.sample;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.adbid.media.AdBidPlatform;
import com.adbid.sdk.AdbidSdk;
import com.ubimax.UMTSDK;
import com.ubimax.api.init.UMTInitCallBack;
import com.ubimax.api.init.UMTInitConfig;
import com.ubimax.api.init.UMTPrivacyConfig;
import com.ubimax.api.init.UMTUserInfoConfig;

import java.util.List;

/**
 * @author zhanao.feng
 * @date 2024/7/9 16:12
 * @description
 */
public class UMTApplication extends Application {
    public static final String TAG = "_ubidding_demo";
    public static Context context;
    private static UMTApplication umtApplication;

    public static void callInitSdkAfterPrivacyAgree() {
        if (umtApplication != null) {
            umtApplication.init();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        umtApplication = this;
    }

    private void initStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog() //在Logcat 中打印违规异常信息
                .build());
    }

    public void init() {

        UMTUserInfoConfig userInfoConfig = new UMTUserInfoConfig();
        userInfoConfig.setUserId("your user id");
        userInfoConfig.setSubChannel("2");
        userInfoConfig.setChannel("3");
        userInfoConfig.setPubSegmentId("4");
        userInfoConfig.setSubScriber(UMTUserInfoConfig.USER_SUBSCRIBER_YES);
        UMTInitConfig config = new UMTInitConfig.Builder()
                .setAppId(AdConstants.appId)
                .setDebug(true)
                .setConfigUserInfo(userInfoConfig)
                .setPrivacyConfig(new UMTPrivacyConfig() {
                    @Override
                    public boolean isCanUseLocation() {
                        return super.isCanUseLocation();
                    }

                    @Override
                    public boolean canReadInstalledPackages() {
                        return super.canReadInstalledPackages();
                    }

                    @Override
                    public List<String> getAppList() {
                        return super.getAppList();
                    }

                    @Override
                    public boolean isCanUsePhoneState() {
                        return super.isCanUsePhoneState();
                    }

                    @Override
                    public String getDevImei() {
                        return super.getDevImei();
                    }

                    @Override
                    public boolean isCanUseWifiState() {
                        return super.isCanUseWifiState();
                    }

                    @Override
                    public boolean isCanUseWriteExternal() {
                        return super.isCanUseWriteExternal();
                    }

                    @Override
                    public boolean isCanUseOaid() {
                        return true;
                    }

                    @Override
                    public String getDevOaid() {
                        return super.getDevOaid();
                    }

                    @Override
                    public boolean isCanUseAndroidId() {
                        return true;
                    }

                    @Override
                    public String getAndroidId() {
                        return super.getAndroidId();
                    }

                    @Override
                    public boolean isCanUseMacAddress() {
                        return super.isCanUseMacAddress();
                    }

                    @Override
                    public String getMacAddress() {
                        return super.getMacAddress();
                    }

                    @Override
                    public boolean canPersonalRecommend() {
                        return super.canPersonalRecommend();
                    }

                    @Override
                    public boolean isProgrammaticRecommend() {
                        return true;
                    }
                }).build();


        UMTSDK.setInitConfig(this, config);
        UMTSDK.init(new UMTInitCallBack() {
            @Override
            public void onSucc() {
                Log.e(TAG, "SDK version:" + UMTSDK.getSdkVersion());
            }

            @Override
            public void onFail(int code, String msg) {
                Log.e(TAG, "SDK init Fail, code=" + code + ", msg=" + msg);
            }
        });
    }
}
