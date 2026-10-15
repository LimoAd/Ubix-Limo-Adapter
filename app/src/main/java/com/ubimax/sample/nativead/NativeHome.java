package com.ubimax.sample.nativead;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ubimax.sample.base.BaseActivity;
import com.ubimax.sample.express.FeedTemplateActivity;
import com.ubimax.sample.R;


public class NativeHome extends BaseActivity {
    private LinearLayout topLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nativehome);
        setTitleView("信息流");
        topLL = findViewById(R.id.rootLL);
        View tempView = LayoutInflater.from(this).inflate(R.layout.ad_type_list_item, topLL, false);
        bindButton(tempView, R.mipmap.ubix_feed, "模版渲染", topLL);
        tempView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NativeHome.this, FeedTemplateActivity.class);
                startActivity(intent);
            }
        });
        tempView.setBackgroundColor(getResources().getColor(R.color.ubix_tool_whitefe));
        View nativeView = LayoutInflater.from(this).inflate(R.layout.ad_type_list_item, topLL, false);
        nativeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NativeHome.this, FeedNativeActivity.class);
                startActivity(intent);
            }
        });
        nativeView.setBackgroundColor(getResources().getColor(R.color.ubix_tool_whitefe));
        bindButton(nativeView, R.mipmap.ubix_native_feed, "模版+自渲染广告", topLL);
        FrameLayout frameLayout = new FrameLayout(this);
        topLL.addView(frameLayout);

    }

    private void bindButton(View itemView, int iconId, String name, ViewGroup rootVG) {
        boolean showDivider = true;
        TextView textView = itemView.findViewById(R.id.item_name);
        textView.setText(name);
        ImageView icon = itemView.findViewById(R.id.left_icon);
        icon.setImageResource(iconId);
        View divider = itemView.findViewById(R.id.divider);
        divider.setVisibility(showDivider ? View.VISIBLE : View.GONE);
        rootVG.addView(itemView);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
