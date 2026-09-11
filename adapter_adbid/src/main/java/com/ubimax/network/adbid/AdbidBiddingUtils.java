package com.ubimax.network.adbid;

import com.adbid.media.AdBidLossInfo;
import com.adbid.media.AdBidPlatform;
import com.ubimax.api.bean.BiddingFeedBackBean;
import com.ubimax.api.bean.BidLossReasons;
import com.ubimax.api.bean.CommonFeedBackBean;

/**
 * 客户端竞价 win/loss 参数转换，参考 merak adapter。
 */
public final class AdbidBiddingUtils {

    private AdbidBiddingUtils() {
    }

    /**
     * 竞胜回传次高价（单位：分）。
     */
    public static double getSecondPrice(BiddingFeedBackBean bean) {
        if (bean == null) {
            return 0;
        }
        CommonFeedBackBean failBean = bean.getFailBean();
        if (failBean != null && failBean.getPrice() > 0) {
            return failBean.getPrice();
        }
        CommonFeedBackBean succBean = bean.getSuccBean();
        if (succBean != null) {
            return succBean.getPrice();
        }
        return 0;
    }

    /**
     * 竞败回传信息。
     */
    public static AdBidLossInfo getFailInfo(BiddingFeedBackBean bean) {
        if (bean == null) {
            return new AdBidLossInfo(0);
        }
        double winPrice = 0;
        CommonFeedBackBean succBean = bean.getSuccBean();
        if (succBean != null) {
            winPrice = succBean.getPrice();
        }
        return new AdBidLossInfo(AdBidPlatform.Other, winPrice, mapLossReason(bean.getLossType()));
    }

    private static String mapLossReason(int lossType) {
        switch (lossType) {
            case BidLossReasons.FLOOR_PRICE_FILTER:
                return "floor_price_filter";
            case BidLossReasons.LOW_PRICE:
                return "low_price";
            case BidLossReasons.TIMEOUT:
                return "timeout";
            case BidLossReasons.MATERIAL_BLOCKED:
                return "material_blocked";
            case BidLossReasons.AD_EXPIRED:
                return "ad_expired";
            case BidLossReasons.OTHER:
            default:
                return "other";
        }
    }
}
