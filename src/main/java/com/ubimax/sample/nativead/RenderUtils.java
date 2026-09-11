package com.ubimax.sample.nativead;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.ubimax.sample.base.WebViewActivity;
import com.ubimax.sample.utils.ScreenUtils;
import com.ubimax.api.bean.UMTConstant;
import com.ubimax.api.bean.UMTFeedExtraInfo;
import com.ubimax.api.bean.UMTFeedMaterial;
import com.ubimax.sample.R;


import java.util.ArrayList;

/**
 * 信息流自渲染封装工具
 */
public class RenderUtils {
    private static final String TAG = "_ubidding_pub_feed_native";

    public static void renderNativeView(Context context, UMTFeedMaterial material, View container, UMTFeedExtraInfo extraInfo,String adnName) {
        TextView title = container.findViewById(R.id.native_ad_title);
        TextView source = container.findViewById(R.id.native_ad_source);
        TextView description = container.findViewById(R.id.native_ad_desc);
        FrameLayout iconArea = (FrameLayout) container.findViewById(R.id.native_ad_image);
        FrameLayout contentArea = (FrameLayout) container.findViewById(R.id.native_ad_content_image_area);
        View closeView = container.findViewById(R.id.native_ad_close);
        ImageView mimageView = new ImageView(context);
        ViewGroup.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.
                LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);


        ArrayList<View> clickView = new ArrayList<>();
        clickView.add(contentArea);
        clickView.add(description);
        clickView.add(title);
        clickView.add(iconArea);

        extraInfo.setCloseView(closeView);
        extraInfo.setChoiceViewLayoutParams(new FrameLayout.LayoutParams(0, 0));

        source.setText(adnName +"广告");
        title.setText(material.getTitle());
        description.setText(material.getDescription());
        if (material.getIconMaterialInfo() != null && !TextUtils.isEmpty(material.getIconMaterialInfo().getImgUrl())) {
            ImageView imageView = new ImageView(context);
            ViewGroup.LayoutParams mparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            imageView.setLayoutParams(mparams);
            Glide.with(imageView).load(material.getIconMaterialInfo().getImgUrl()).into(imageView);
            iconArea.addView(imageView, mparams);
        }
        Log.e(TAG, " 广告是否是视频 " + material.isVideo());
        if (material.getVideoInfo() != null) {
            Log.e(TAG, " 视频地址 " + material.getVideoInfo().getVideoUrl());
            Log.e(TAG, " 视频时长 " + material.getVideoInfo().getVideoDuration());
        } else if (material.getMainImgInfo() != null) {
            Log.e(TAG, " 图片宽： " + material.getMainImgInfo().getWidth() + material.getMainImgInfo().getHeight());
        }
        // ！！！ 如果 mediaView不为空 则是视频类型
        View mediaView = material.getAdMediaView();
        if (mediaView != null && material.isVideo()) {
            if (mediaView.getParent() != null) {
                ((ViewGroup) mediaView.getParent()).removeView(mediaView);
            }
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.
                    LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            mediaView.setLayoutParams(params);
            contentArea.addView(mediaView, params);
            clickView.add(mediaView); //快手广告只有把MediaView 传到clickView中，视频才支持点击。
        } else {

            mimageView.setLayoutParams(layoutParams);
            mimageView.setScaleType(ImageView.ScaleType.FIT_XY);
            RequestOptions options = new RequestOptions().bitmapTransform(new RoundedCorners(ScreenUtils.dp2px(5)));
            String imgUrl = "";
            if (material.getMainImgInfo() != null && !TextUtils.isEmpty(material.getMainImgInfo().getImgUrl())) {
                imgUrl = material.getMainImgInfo().getImgUrl();
            } else if (material.getImageList() != null && !material.getImageList().isEmpty()) {
                imgUrl = material.getImageList().get(0).getImgUrl();
            }
            Log.e(TAG, " 图片地址 " + imgUrl);
            Glide.with(context).load(imgUrl).apply(options).into(mimageView);
            contentArea.addView(mimageView, layoutParams);
        }
        extraInfo.setClickViewList(clickView);
        // 下载类广告六要素显示
        if (material.getDownloadAppInfo() != null && material.getInteractionType() == UMTConstant.FeedInteractionType.DOWNLOAD) {
            UMTFeedMaterial.DownloadAppInfo downloadAppinfo = material.getDownloadAppInfo();
            container.findViewById(R.id.appinfoContainer).setVisibility(View.VISIBLE);
            TextView appName = container.findViewById(R.id.appName);
            TextView appVersion = container.findViewById(R.id.appVersion);
            TextView appPublisher = container.findViewById(R.id.appPublisher);
            TextView appPermission = container.findViewById(R.id.appPermission);

            appPermission.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "权限：url " + downloadAppinfo.getPrivacyAgreementUrl());
                    Intent intent = new Intent(context, WebViewActivity.class);
                    intent.putExtra("title", "权限");
                    intent.putExtra("url", downloadAppinfo.getPermissionUrl());
                    context.startActivity(intent);
                }
            });
            TextView appAgreement = container.findViewById(R.id.appAgreement);
            appAgreement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "隐私：url " + downloadAppinfo.getPrivacyAgreementUrl());
                    Intent intent = new Intent(context, WebViewActivity.class);
                    intent.putExtra("url", downloadAppinfo.getPrivacyAgreementUrl());
                    context.startActivity(intent);
                }
            });
            TextView desc = container.findViewById(R.id.app_desc);
            desc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "功能：url " + downloadAppinfo.getFunctionUrl());
                    Intent intent = new Intent(context, WebViewActivity.class);
                    intent.putExtra("title", "功能描述");
                    intent.putExtra("url", downloadAppinfo.getFunctionUrl());
                    context.startActivity(intent);
                }
            });
            appName.setText(downloadAppinfo.getAppName());
            appVersion.setText(downloadAppinfo.getAppVersionName());
            appPublisher.setText(downloadAppinfo.getAppPublisher());
//            appPermission.setText(downloadAppinfo.getPermissionUrl());
//            appAgreement.setText(downloadAppinfo.getPrivacyAgreementUrl());
        }
    }

}
