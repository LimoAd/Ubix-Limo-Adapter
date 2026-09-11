package com.ubimax.sample;

/**
 * 常量类，用于存储广告相关的常量
 */
public class AdConstants {

    public static String appId;
    public static String splashId; // 开屏
    public static String expressNativeId; // 信息流模版
    public static String nativeId; // 信息流自渲染广告
    public static String bannerId; // 横幅广告
    public static String interstitialId; // 插屏id
    public static String rewardId; // 激励视频

    public static String userId;

    static {
        setAdIds("133443209962", "50000249", "50000253", "50000251", "", "50000250", "50000252");
    }


    private static void setAdIds(String appId, String splashId, String interstitialId, String rewardId, String expressNativeId, String nativeId, String bannerId) {
        AdConstants.appId = appId;
        AdConstants.splashId = splashId;
        AdConstants.interstitialId = interstitialId;
        AdConstants.rewardId = rewardId;
        AdConstants.expressNativeId = expressNativeId;
        AdConstants.nativeId = nativeId;
        AdConstants.bannerId = bannerId;
    }
}
