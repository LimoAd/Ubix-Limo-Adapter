package com.ubimax.sample.custom.kuaishou;

import android.text.TextUtils;

/**
 * @author zhanao.feng
 * @date 2024/12/5 10:23
 * @description
 */
public class Utils {
    public static long getLongValue(String num) {
        try {
            if (!TextUtils.isEmpty(num) && num.length() > 1 && (num.endsWith("L") || num.endsWith("l"))) {
                num = num.substring(0, num.length() - 1);
            }
            return Long.parseLong(num);
        } catch (Exception e) {
            return -1;
        }
    }
}
