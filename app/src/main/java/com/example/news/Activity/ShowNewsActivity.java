
package com.example.news.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.news.Bean.News;
import com.example.news.R;
import com.example.news.Utils.ApplicationUtil;
import com.example.news.Utils.MyBitmapUtils;
import com.example.news.Utils.MyDatabaseHelper;
import com.example.news.Utils.NewsManager;
import com.xyzlf.share.library.bean.ShareEntity;
import com.xyzlf.share.library.interfaces.ShareConstant;
import com.xyzlf.share.library.util.ShareUtil;

import java.util.ArrayList;
import java.util.List;

public class ShowNewsActivity extends AppCompatActivity {
    static String TAG = "ShowNewsActivity";

    private ImageButton share_weibo;
    private ImageButton share_qq;
    private List<Bitmap> bitmapList;
    private TextView news_item_title;
    private TextView news_item_publisher;
    private TextView news_item_publishtime;
    private TextView news_item_content;
    private ImageView collect_news;
    private LinearLayout news_show;

    private MyBitmapUtils myBitmapUtils;
    private MyDatabaseHelper helper;
    private ImageView[] pointList;
    private ViewGroup pointGroup;
    private ViewPager viewPager;

    private boolean CollectionStauts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_news);
        initView();
        helper = MyDatabaseHelper.getInstance(this);
        helper.getWritableDatabase();

        myBitmapUtils = new MyBitmapUtils(this);

        Intent intent = getIntent();
        final String news_url = intent.getStringExtra("url");
        final String news_category = intent.getStringExtra("category");
        final News news = NewsManager.getInstance(null).getNews(news_url);
        final List<String> news_img_url_list = news.getImg_url_list();

        news_item_title.setText(news.getNews_title());
        news_item_publishtime.setText(news.getDate());
        news_item_publisher.setText(news.getAuthor_name());

        news_item_content.setText(beautifiedText(news.getNews_content()));

        bitmapList = new ArrayList<>();
        for (String img_url : news_img_url_list) {
            Bitmap img = null;
            try {
                img = NewsManager.getInstance(null).getImage(img_url);
            }
            catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "onCreate: getImage error");
                img = null;
            }
            if (img != null)
                bitmapList.add(img);
        }
        if(bitmapList.size() == 0)
            bitmapList.add(BitmapFactory.decodeResource(getResources(),R.drawable.loading));
        final List<ImageView> imageViews = new ArrayList<>();
        LayoutInflater inflater = getLayoutInflater();
        for (Bitmap bitmap : bitmapList) {
            ImageView imageView = (ImageView)(inflater.inflate(R.layout.news_image, null));
            imageView.setImageBitmap(bitmap);
            imageViews.add(imageView);
        }

        Log.d(TAG, "onCreate: image size: " + imageViews.size());

        pointList = new ImageView[bitmapList.size()];
        for(int i = 0; i < bitmapList.size();i++){
            ImageView point = new ImageView(this);
            point.setLayoutParams(new LinearLayout.LayoutParams(20,20));
            point.setPadding(20, 0, 20, 0);
            if (i == 0)
                point.setBackgroundResource(R.drawable.point_red);
            else
                point.setBackgroundResource(R.drawable.point_gray);
            pointList[i] = point;
            pointGroup.addView(pointList[i]);
        }
        if (pointList.length > 1)
            pointGroup.setVisibility(View.VISIBLE);
        else
            pointGroup.setVisibility(View.INVISIBLE);

        final List<ImageView> imageViews_ = imageViews;
        PagerAdapter mPagerAdapter = new PagerAdapter(){
            @Override
            public int getCount() {
                Log.d(TAG, "getCount: " + imageViews_.size());
                return imageViews_.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(View view, int i, Object object) {
                ((ViewPager) view).removeView(imageViews_.get(i));
            }

            @Override
            public Object instantiateItem(View view, int i){
                ((ViewPager)view).addView(imageViews_.get(i));
                return imageViews_.get(i);
            }
        };

        class PageChangeListener implements ViewPager.OnPageChangeListener {
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int index) {
                for(int i = 0; i < pointList.length;i++){
                    if (i == index)
                        pointList[i].setBackgroundResource(R.drawable.point_red);
                    else
                        pointList[i].setBackgroundResource(R.drawable.point_gray);
                }
            }

        }

        //绑定监听事件
        viewPager.setOnPageChangeListener(new PageChangeListener());
        viewPager.setAdapter(mPagerAdapter);


        CollectionStauts = helper.isCollected(news_url);
        if (CollectionStauts)
            collect_news.setImageResource(R.drawable.favorite_selected);
        else
            collect_news.setImageResource(R.drawable.favorite);

        collect_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CollectionStauts = ! CollectionStauts;
                if (CollectionStauts) {
                    collect_news.setImageResource(R.drawable.favorite_selected);
                    helper.collectNews(news_url);
                    Toast.makeText(ShowNewsActivity.this, "收藏成功！", Toast.LENGTH_SHORT).show();
                }
                else {
                    collect_news.setImageResource(R.drawable.favorite);
                    helper.uncollectNews(news_url);
                    Toast.makeText(ShowNewsActivity.this, "取消收藏成功！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        share_weibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareEntity testBean = new ShareEntity("我是标题","大家都在看新闻：\n"
                        + news.getNews_title());
                testBean.setUrl(news_url); //分享链接
                if (news_img_url_list.size() > 0)
                    testBean.setImgUrl(news_img_url_list.get(0));
                ShareUtil.startShare(ShowNewsActivity.this, ShareConstant.SHARE_CHANNEL_SINA_WEIBO, testBean, ShareConstant.REQUEST_CODE);
            }
        });
        share_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareEntity testBean = new ShareEntity("我是标题","大家都在看新闻：\n"
                        + news.getNews_title()+"\n"+news_url);
                ShareUtil.startShare(ShowNewsActivity.this, ShareConstant.SHARE_CHANNEL_QQ, testBean, ShareConstant.REQUEST_CODE);
            }
        });

        ApplicationUtil.getInstance().addActivity(this);
        if(!MainActivity.Day){
            news_item_title.setTextColor(Color.WHITE);
            news_item_publisher.setTextColor(Color.WHITE);
            news_item_publishtime.setTextColor(Color.WHITE);
            news_item_content.setTextColor(Color.WHITE);
            news_show.setBackgroundColor(Color.parseColor("#5e5e5e"));
        }
    }

    private void initView() {
        collect_news = findViewById(R.id.collect_news);
        news_item_title = findViewById(R.id.news_details_item_title);
        news_item_content = findViewById(R.id.news_details_item_content);
        news_item_publisher = findViewById(R.id.news_details_item_publisher);
        news_item_publishtime = findViewById(R.id.news_details_item_publishtime);
        share_weibo = findViewById(R.id.share_weibo);
        share_qq = findViewById(R.id.share_qq);
        viewPager = findViewById(R.id.view_images);
        pointGroup = findViewById(R.id.view_points);
        news_show = findViewById(R.id.news_show);
    }

    boolean unWord(char c) {
        String s = String.valueOf(c);
        boolean res;
        res = (s.contains(" ") || s.contains("\n") || s.contains("\t") || s.contains("\r") || s.contains("　"));
        Log.d(TAG, "beautifiedText unWord: " + s + "__result" + res);
        return res;
    }

    public String beautifiedText(String text) {
        String[] lines = text.split("\n");
        char[] finalText = new char[lines.length * 6 + text.length()];
        int now = 0;
        for (String line : lines) {
            Log.d(TAG, "beautifiedText: line: " + line);
            if (line.length() > 4) {
                Log.d(TAG, "beautifiedText: line start 4: "
                        + line.charAt(0) + "_"
                        + line.charAt(1) + "_"
                        + line.charAt(2) + "_"
                        + line.charAt(3) + "_");
            }
            int start = now;
            int i = 0;
            while (i < line.length() && unWord(line.charAt(i)))
                i ++;
            for (int j = 0; j < 2; j++)
                finalText[now ++] = '　';
            while (i < line.length())
                finalText[now ++] = line.charAt(i ++);
            while (now > 0 && unWord(finalText[now - 1]))
                now --;
            finalText[now ++] = '\n';
//            finalText[now ++] = '\n';
            Log.d(TAG, "beautifiedText: final: " + new String(finalText, start, now - start));
        }
        return new String(finalText);
    }
}
