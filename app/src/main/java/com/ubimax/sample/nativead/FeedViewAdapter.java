package com.ubimax.sample.nativead;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.ubimax.api.bean.UMTFeedVideoAdListener;
import com.ubimax.sample.utils.ScreenUtils;
import com.ubimax.api.bean.UMTErrorInfo;
import com.ubimax.api.bean.UMTFeedEventListener;
import com.ubimax.api.bean.UMTFeedExtraInfo;
import com.ubimax.api.bean.UMTFeedRenderListener;
import com.ubimax.feed.api.UMTFeedAdView;
import com.ubimax.sample.R;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class FeedViewAdapter extends RecyclerView.Adapter {
    private static final String TAG = "_ubidding_pub_FeedViewAdapter";
    public static int info = 1;
    public static int tempAd = 2;
    public static int nativeAd = 3;
    public List<TestFeedBean> tetsList = new ArrayList<>();
    private Context context;

    public FeedViewAdapter(Context context, List<TestFeedBean> data) {
        this.context = context;
        tetsList = data;
    }

    public void addData(List<TestFeedBean> data) {
        tetsList.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(TestFeedBean testBean) {
        tetsList.add(testBean);
        notifyDataSetChanged();
    }

    public void addData(int pos, TestFeedBean testBean) {
        tetsList.add(pos, testBean);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (tempAd == viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_feed_ad_item, viewGroup, false);
            return new ExpressAdViewHolder(view);
        } else if (nativeAd == viewType) {
            Log.e("------Adapter", "onCreateViewHolder NativeFeedViewHolder ");
            View view = LayoutInflater.from(viewGroup.getContext()).
                    inflate(R.layout.activity_feed_ad_item, viewGroup, false);
            return new NativeAdViewHolder(view);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).
                    inflate(R.layout.activity_feed_common_item, viewGroup, false);
            return new CommonItemHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int i) {
        final int pos = i;
        if (holder instanceof ExpressAdViewHolder || holder instanceof NativeAdViewHolder) {
            tetsList.get(i).adBean.setFeedVideoAdListener(new UMTFeedVideoAdListener() {
                @Override
                public void onVideoLoaded() {
                    Log.e(TAG, "onVideoLoaded");
                }

                @Override
                public void onVideoReady() {
                    Log.e(TAG, "onVideoReady");
                }

                @Override
                public void onVideoStart() {
                    Log.e(TAG, "onVideoStart");
                }

                @Override
                public void onVideoPause() {
                    Log.e(TAG, "onVideoPause");
                }

                @Override
                public void onVideoResume() {
                    Log.e(TAG, "onVideoResume");
                }

                @Override
                public void onVideoFinish() {
                    Log.e(TAG, "onVideoFinish");
                }

                @Override
                public void onVideoProgressUpdate(long l, long l1) {
                    Log.e(TAG, "onVideoProgressUpdate:" + l + " " + l1);
                }

                @Override
                public void onVideoError(UMTErrorInfo umtErrorInfo) {
                    Log.e(TAG, "onVideoError" + umtErrorInfo.toString());

                }
            });
            tetsList.get(i).adBean.setFeedEventListener(new UMTFeedEventListener() {
                @Override
                public void onAdShow() {
                    Log.e(TAG, "onAdShow");
                }

                @Override
                public void onAdClick() {
                    Log.e(TAG, "onAdClick");
                }

                @Override
                public void onAdDismiss() {
                    Log.e(TAG, "onAdDismiss");
                    if (tetsList.get(i).adBean != null) {
                        tetsList.get(i).adBean.destroy();
                    }
                    tetsList.remove(pos);
                    notifyDataSetChanged();
                }

                @Override
                public void onShowError(UMTErrorInfo errorInfo) {
                    Log.e(TAG, "onShowError");
                    if (tetsList.get(i).adBean != null) {
                        tetsList.get(i).adBean.destroy();
                    }
                    tetsList.remove(pos);
                    notifyDataSetChanged();
                }
            });
        }
        if (holder instanceof ExpressAdViewHolder) {
            UMTFeedAdView adContainer = (UMTFeedAdView) ((ExpressAdViewHolder) holder).adContainer;
            adContainer.removeAllViews();
            tetsList.get(i).adBean.setFeedExpressRenderListener(new UMTFeedRenderListener() {
                @Override
                public void onAdRenderFail(UMTErrorInfo errorInfo) {
                    Log.e(TAG, "onAdRenderFail:" + errorInfo.toString());
                }

                @Override
                public void onAdRenderSucc() {
                    Log.e(TAG, "onAdRenderSucc");
                }
            });
            tetsList.get(i).adBean.render();
            tetsList.get(i).adBean.register((Activity) context, adContainer, null, null);
        } else if (holder instanceof NativeAdViewHolder) {
            UMTFeedAdView adContainer = (UMTFeedAdView) ((NativeAdViewHolder) holder).adContainer;
            ViewGroup selfView = (ViewGroup) View.inflate(adContainer.getContext(), R.layout.native_ad_item, null);
            UMTFeedExtraInfo extraInfo = new UMTFeedExtraInfo();
            RenderUtils.renderNativeView(adContainer.getContext(), tetsList.get(i).adBean.getMaterial(), selfView, extraInfo,tetsList.get(i).adBean.getAdnInfo().getAdnName());
            tetsList.get(i).adBean.register((Activity) context, adContainer, selfView, extraInfo);
        } else if (holder instanceof CommonItemHolder) {
            ((CommonItemHolder) holder).titleTv.setText(tetsList.get(i).title);
            ((CommonItemHolder) holder).descTv.setText(tetsList.get(i).desc);
            ((CommonItemHolder) holder).authorTv.setText(tetsList.get(i).shareUser);
            ((CommonItemHolder) holder).timeTv.setText(new Date(tetsList.get(i).publishTime).toLocaleString());
            ImageView img = ((CommonItemHolder) holder).testImg;
            String[] imgUlrs = {
                    "https://tse3-mm.cn.bing.net/th/id/OIP-C.Is2yEGD5UpcDWjgEZxI3aAHaEo?w=259&h=180&c=7&r=0&o=5&dpr=2&pid=1.7",
                    "https://tse4-mm.cn.bing.net/th/id/OIP-C.j-2GZozKlFHCQxgXidgg9QHaEo?w=256&h=180&c=7&r=0&o=5&dpr=2&pid=1.7",
                    "https://tse2-mm.cn.bing.net/th/id/OIP-C.nqq9s2-bWPwHWsPZjBn46gHaEo?w=262&h=180&c=7&r=0&o=5&dpr=2&pid=1.7",
                    "https://tse4-mm.cn.bing.net/th/id/OIP-C.Vfxh4R-SyqS-SIwTGr4X7wHaEo?w=281&h=180&c=7&r=0&o=5&dpr=2&pid=1.7",
                    "https://tse3-mm.cn.bing.net/th/id/OIP-C.rTwMlM667NmfKexYrB38YQHaEK?w=284&h=180&c=7&r=0&o=5&dpr=2&pid=1.7",
                    "https://tse2-mm.cn.bing.net/th/id/OIP-C.9cSZF5YSMolt30Tvnqv3TwHaEK?w=291&h=180&c=7&r=0&o=5&dpr=2&pid=1.7",
                    "https://tse4-mm.cn.bing.net/th/id/OIP-C.2hCrFBW2uGhro_cv9eBNtQHaEo?w=273&h=180&c=7&r=0&o=5&dpr=2&pid=1.7",
                    "https://tse4-mm.cn.bing.net/th/id/OIP-C.t2scgN14O3PeUIdauzxlTAHaEK?w=280&h=180&c=7&r=0&o=5&dpr=2&pid=1.7"
            };
            RequestOptions options = new RequestOptions().bitmapTransform(new RoundedCorners(ScreenUtils.dp2px(5)));
            Glide.with(img).load(imgUlrs[pos % imgUlrs.length]).apply(options).into(img);
        }

    }

    @Override
    public int getItemCount() {
        return tetsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return tetsList.get(position).itemType;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @IntDef({ItemViewType.ITEM_VIEW_TYPE_NORMAL, ItemViewType.ITEM_VIEW_TYPE_VIDEO,
            ItemViewType.ITEM_VIEW_TYPE_SINGLE_IMG, ItemViewType.ITEM_VIEW_TYPE_GROUP_IMG})
    @interface ItemViewType {
        int ITEM_VIEW_TYPE_NORMAL = 0;
        int ITEM_VIEW_TYPE_VIDEO = 1;
        int ITEM_VIEW_TYPE_SINGLE_IMG = 2;
        int ITEM_VIEW_TYPE_GROUP_IMG = 3;
    }

    private static class CommonItemHolder extends RecyclerView.ViewHolder {
        TextView titleTv;
        TextView authorTv;
        TextView descTv;

        TextView timeTv;
        ImageView testImg;
        ImageView newTag;

        public CommonItemHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.titleTv);
            descTv = itemView.findViewById(R.id.descTv);
            authorTv = itemView.findViewById(R.id.authorTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            testImg = itemView.findViewById(R.id.testImg);
            newTag = itemView.findViewById(R.id.newTag);

        }
    }


    private static class ExpressAdViewHolder extends RecyclerView.ViewHolder {
        ViewGroup adContainer;

        @SuppressWarnings("RedundantCast")
        public ExpressAdViewHolder(View itemView) {
            super(itemView);
            Log.e("------Adapter", "FeedViewHolder");
            adContainer = itemView.findViewById(R.id.umncontainer);
        }
    }


    private static class NativeAdViewHolder extends RecyclerView.ViewHolder {
        ViewGroup adContainer;

        @SuppressWarnings("RedundantCast")
        public NativeAdViewHolder(View itemView) {
            super(itemView);
            adContainer = itemView.findViewById(R.id.umncontainer);
        }
    }


}
