package com.ubimax.sample.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubimax.sample.utils.ScreenUtils;
import com.ubimax.sample.R;


//所有页面的顶部标题通用组件
public class DemoTitleView extends FrameLayout {

    private ImageView imageView;
    private TextView titleTv;
    View viewLine;

    public DemoTitleView(Context context) {
        super(context);
        initView(context);
    }

    public DemoTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public DemoTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private void initView(final Context context) {
        imageView = new ImageView(context);
        titleTv = new TextView(context);
        viewLine = new View(context);

        this.addView(imageView);
        this.addView(titleTv);
        this.addView(viewLine);

    }

    public void setTitle(String titleStr) {
        titleTv.setText(titleStr);
        titleTv.setGravity(Gravity.CENTER);
        titleTv.setTextColor(getResources().getColor(R.color.ubix_tool_black85));
        titleTv.setTypeface(Typeface.DEFAULT_BOLD);
        titleTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        ((LayoutParams) titleTv.getLayoutParams()).gravity = Gravity.CENTER;

        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) imageView.getContext()).finish();
            }
        });
        ((LayoutParams) imageView.getLayoutParams()).gravity = Gravity.CENTER_VERTICAL;
        imageView.getLayoutParams().height = ScreenUtils.dp2px(45);
        imageView.getLayoutParams().width = ScreenUtils.dp2px(45);
        imageView.setImageResource(R.drawable.ubix_back_icon);
        int padddig = ScreenUtils.dp2px(10);
        imageView.setPadding(padddig, padddig, padddig, padddig);

        viewLine.setBackgroundColor(getResources().getColor(R.color.ubix_tool_whiteee));
        viewLine.getLayoutParams().height = 1;
//        dp2px(1);
        viewLine.getLayoutParams().width = LayoutParams.MATCH_PARENT;
        ((LayoutParams) viewLine.getLayoutParams()).gravity = Gravity.BOTTOM;
        setBackgroundColor(getResources().getColor(R.color.ubix_tool_whitefe));
    }
}
