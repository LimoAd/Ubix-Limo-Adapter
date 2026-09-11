package com.ubimax.sample.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import java.math.BigDecimal;

public class ScreenUtils {
    public static final double SPLASH_MIN = 0.49d;
    public static final double SPLASH_MIDDLE = 0.61d;
    public static final double SPLASH_MAX = 0.75d;

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getScreenWidthDip(Context context) {
        return px2dip(context, ScreenUtils.getScreenWidth(context));
    }

    public static int getScreenHeightDip(Context context) {
        return px2dip(context, ScreenUtils.getScreenHeight(context));

    }

    public static int getRealScreenHeightDp(Context context) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.getDisplay().getRealMetrics(outMetrics);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealMetrics(outMetrics);
        } else {
            wm.getDefaultDisplay().getMetrics(outMetrics);
        }
        return px2dip(context, outMetrics.heightPixels);
    }

    public static int getRealScreenHeightPx(Context context) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.getDisplay().getRealMetrics(outMetrics);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealMetrics(outMetrics);
        } else {
            wm.getDefaultDisplay().getMetrics(outMetrics);
        }
        return (outMetrics.heightPixels);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int dp2px(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    public static double getAspectRatio(View view) {
        int height = view.getMeasuredHeight();
        int width = view.getMeasuredWidth();
        return div(width, height);
    }

    public static double getScreenAspectRatio(Context context) {
        int height = getScreenHeight(context);
        int width = getScreenWidth(context);
        return div(width, height);
    }

    public static double div(int width, int height) {
        if (width == 0 || height == 0) {
            return 0d;
        }
        BigDecimal b1 = new BigDecimal(String.valueOf(height));
        BigDecimal b2 = new BigDecimal(String.valueOf(width));
        return b2.divide(b1, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double div(float width, float height) {
        if (width == 0 || height == 0) {
            return 0d;
        }
        BigDecimal b1 = new BigDecimal(String.valueOf(height));
        BigDecimal b2 = new BigDecimal(String.valueOf(width));
        return b2.divide(b1, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    /**
     * 获得状态栏的高度
     */
    public static int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {

        }
        return statusHeight;
    }


}