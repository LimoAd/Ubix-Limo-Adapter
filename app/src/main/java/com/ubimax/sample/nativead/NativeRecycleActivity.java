package com.ubimax.sample.nativead;

import static android.widget.LinearLayout.VERTICAL;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ubimax.api.bean.UMTFeedAdSession;
import com.ubimax.sample.base.BaseActivity;
import com.ubimax.sample.utils.ScreenUtils;
import com.ubimax.UMTSDK;
import com.ubimax.api.UMTAdConfig;
import com.ubimax.api.UMTAdLoader;
import com.ubimax.api.bean.UMTErrorInfo;
import com.ubimax.api.bean.UMTFeedAd;
import com.ubimax.api.bean.UMTFeedAdListener;
import com.ubimax.sample.R;

import org.json.JSONArray;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 信息流列表页，包括信息流模板和自渲染
 */
public class NativeRecycleActivity extends BaseActivity {

    private final String TAG = "_ubidding_pub_feed_rec";
    private final List<TestFeedBean> testBeanList = new ArrayList<>();
    JSONArray jsonArray = null;
    private FeedViewAdapter adapter;
    private RecyclerView recyclerView;
    private boolean isLoading = false;
    private UMTAdLoader adLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_recycle);
        setTitleView("信息流广告列表");
        initRV();
        laodTestData();
    }

    public void loadAd() {
        int width = (ScreenUtils.getScreenWidth(this) - ScreenUtils.dp2px(30));
        int height = 0;
        String slotId = getIntent().getStringExtra("slotId");
        // 1自渲染  2模板
        UMTAdConfig config = new UMTAdConfig.Builder()
                .setSlotId(slotId)
                .setWidth(ScreenUtils.px2dip(this, width)) //单位dp
                .setHeight(height)
                .build();
        adLoader = UMTSDK.createAdLoader();
        adLoader.loadFeedAd(this, config, new UMTFeedAdListener() {
            @Override
            public void onAdStart(UMTFeedAdSession umtAd) {
                Log.e(TAG, "onAdStart:");
            }

            @Override
            public void onAdLoaded(List<UMTFeedAd> feedAd) {
                isLoading = false;
                Log.e(TAG, "onAdLoaded:");
                TestFeedBean testFeedBean = new TestFeedBean();
                testFeedBean.adBean = feedAd.get(0);
                if (!testFeedBean.adBean.isExpressAd()) {
                    String iconUrl = testFeedBean.adBean.getMaterial().getIconMaterialInfo() == null ? "" : testFeedBean.adBean.getMaterial().getIconMaterialInfo().getImgUrl();
                    String mainUrl = testFeedBean.adBean.getMaterial().getMainImgInfo() == null ? "" : testFeedBean.adBean.getMaterial().getMainImgInfo().getImgUrl();
                    if (testFeedBean.adBean.getMaterial() != null) {
                        Log.e(TAG, "获取广告信息: "
                                + "\ntitle:" + testFeedBean.adBean.getMaterial().getTitle()
                                + "\nDescription:" + testFeedBean.adBean.getMaterial().getDescription()
                                + "\nisVideo:" + testFeedBean.adBean.getMaterial().isVideo()
                                + "\nIconImageUrl:" + iconUrl
                                + "\nInteractionType:" + testFeedBean.adBean.getMaterial().getInteractionType() + "\nMainImageUrl:" + mainUrl
                        );
                        if (testFeedBean.adBean.getMaterial().getImageList() != null) {
                            for (int i = 0; i < testFeedBean.adBean.getMaterial().getImageList().size(); i++) {
                                Log.e(TAG, "ImageUrlList :" + (i + 1)
                                        + " \nimageUrl:" + testFeedBean.adBean.getMaterial().getImageList().get(i).getImgUrl()
                                        + " \nwidth:" + testFeedBean.adBean.getMaterial().getImageList().get(i).getWidth()
                                        + "\nheight:" + testFeedBean.adBean.getMaterial().getImageList().get(i).getHeight());
                            }
                        }
                    }
                }
                if (testFeedBean.adBean.isExpressAd()) {
                    testFeedBean.itemType = FeedViewAdapter.tempAd;
                } else {
                    testFeedBean.itemType = FeedViewAdapter.nativeAd;
                }
                testBeanList.add(testFeedBean);
                adapter.notifyItemChanged(testBeanList.size() - 1);
            }

            @Override
            public void onError(UMTErrorInfo errorInfo) {
                isLoading = false;
                Log.e(TAG, "onError:" + errorInfo.toString());
            }
        });
    }

    private void initRV() {
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new FeedViewAdapter(this, testBeanList);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration decoration = new DividerItemDecoration(this, VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.item_line));
        recyclerView.addItemDecoration(decoration);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isScrollToBottom(recyclerView)) {
                    Log.e(TAG, "-------onScrollStateChanged bottom");
//
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            laodTestData();
                        }
                    }, 200);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

    }

    public boolean isScrollToBottom(RecyclerView container) {
        LinearLayoutManager manager = (LinearLayoutManager) container.getLayoutManager();
        //当前屏幕所看到的子项个数
        int visibleCount = manager.getChildCount();
        //当前RecyclerView的所有子项个数
        int totalCount = manager.getItemCount();
        if (visibleCount == testBeanList.size()) return true;
        //屏幕中最后一个可见子项的position
        int lastVisiblePosition = manager.findLastVisibleItemPosition();
        //RecyclerView的滑动状态
        int state = container.getScrollState();
        return lastVisiblePosition == totalCount - 1 && state == RecyclerView.SCROLL_STATE_IDLE;
    }

    private void laodTestData() {
        try {
            if (isLoading) {
                return;
            }
            isLoading = true;
            List<TestFeedBean> feedBeanList = new ArrayList<>();
            if (jsonArray == null) {
//                打开存放在assets文件夹下面的json格式的文件并且放在文件输入流里面
                InputStreamReader inputStreamReader = new InputStreamReader(getAssets().open("list.json"), StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line;
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                bufferedReader.close();
                inputStreamReader.close();
                jsonArray = new JSONArray(new JSONTokener(stringBuilder.toString()));
            }
            //新建一个json对象，用它对数据进行操作
            for (int i = 0; i < jsonArray.length(); i++) {
                TestFeedBean testFeedBean = new TestFeedBean();
                testFeedBean.desc = jsonArray.getJSONObject(i).getString("desc");
                testFeedBean.title = jsonArray.getJSONObject(i).getString("title");
                testFeedBean.link = jsonArray.getJSONObject(i).getString("link");
                testFeedBean.author = jsonArray.getJSONObject(i).getString("author");
                testFeedBean.publishTime = jsonArray.getJSONObject(i).getLong("publishTime");
                testFeedBean.envelopePic = jsonArray.getJSONObject(i).getString("envelopePic");
                feedBeanList.add(testFeedBean);
            }
            Log.e("------laodTestData: ", "----jsonArray " + jsonArray.length());
            testBeanList.addAll(feedBeanList);
            loadAd();
        } catch (Exception e) {
            Log.e("------laodTestData: ", "----Exception e " + e.getMessage());

        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
