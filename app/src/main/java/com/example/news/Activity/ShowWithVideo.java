package com.example.news.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.news.Bean.News;
import com.example.news.R;
import com.example.news.Utils.ApplicationUtil;
import com.example.news.Utils.MyDatabaseHelper;
import com.example.news.Utils.NewsManager;
import com.xyzlf.share.library.bean.ShareEntity;
import com.xyzlf.share.library.interfaces.ShareConstant;
import com.xyzlf.share.library.util.ShareUtil;

import java.util.List;

public class ShowWithVideo extends AppCompatActivity {
    private ImageButton share_weibo;
    private ImageButton share_qq;
    private TextView news_item_title;
    private TextView news_item_publisher;
    private TextView news_item_publishtime;
    private LinearLayout news_show;

    private TextView news_item_content;
    private ImageView collect_news;

    private MyDatabaseHelper helper;

    private VideoView mVideoView;
    private MediaController mController;
    private ImageView mLoading;

    private boolean CollectionStauts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_news);
        mController=new MediaController(this);
        initView();
        helper = MyDatabaseHelper.getInstance(this);
        helper.getWritableDatabase();

        Intent intent = getIntent();
        final String news_url = intent.getStringExtra("url");
        final String news_category = intent.getStringExtra("category");
        final News news = NewsManager.getInstance(null).getNews(news_url);
        final List<String> news_img_url_list = news.getImg_url_list();

        news_item_title.setText(news.getNews_title());
        news_item_publishtime.setText(news.getDate());
        news_item_publisher.setText(news.getAuthor_name());
//        news_item_content.setText(news.getNews_content());

        news_item_content.setText(beautifiedText(news.getNews_content()));

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
                    Toast.makeText(ShowWithVideo.this, "收藏成功！", Toast.LENGTH_SHORT).show();
                }
                else {
                    collect_news.setImageResource(R.drawable.favorite);
                    helper.uncollectNews(news_url);
                    Toast.makeText(ShowWithVideo.this, "取消收藏成功！", Toast.LENGTH_SHORT).show();
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
                ShareUtil.startShare(ShowWithVideo.this, ShareConstant.SHARE_CHANNEL_SINA_WEIBO, testBean, ShareConstant.REQUEST_CODE);
            }
        });
        share_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareEntity testBean = new ShareEntity("我是标题","大家都在看新闻：\n"
                        + news.getNews_title()+"\n"+news_url);
                ShareUtil.startShare(ShowWithVideo.this, ShareConstant.SHARE_CHANNEL_QQ, testBean, ShareConstant.REQUEST_CODE);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri uri=Uri.parse(news.getVideo_url());
                mVideoView.setVideoURI(uri);
                mVideoView.setMediaController(mController);
                mController.setMediaPlayer(mVideoView);
                mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                            @Override
                            public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                                if (i ==MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {

                                    mLoading.setVisibility(View.INVISIBLE);
                                    return true;
                                }
                                if (i == MediaPlayer.MEDIA_INFO_BUFFERING_START){
                                    mLoading.setVisibility(View.VISIBLE);
                                    return true;
                                }
                                else if(i == MediaPlayer.MEDIA_INFO_BUFFERING_END){
                                    mLoading.setVisibility(View.INVISIBLE);
                                    return true;
                                }
                                return false;
                            }
                        });
                    }
                });
                mVideoView.start();
                mVideoView.requestFocus();
            }
        }).start();

        if(!MainActivity.Day){
            news_item_title.setTextColor(Color.WHITE);
            news_item_publisher.setTextColor(Color.WHITE);
            news_item_publishtime.setTextColor(Color.WHITE);
            news_item_content.setTextColor(Color.WHITE);
            news_show.setBackgroundColor(Color.parseColor("#5e5e5e"));
        }

        ApplicationUtil.getInstance().addActivity(this);
    }

    private void initView() {
        collect_news = findViewById(R.id.collect_news);
        news_item_title = findViewById(R.id.news_details_item_title);
        news_item_content = findViewById(R.id.news_details_item_content);
        news_item_publisher = findViewById(R.id.news_details_item_publisher);
        news_item_publishtime = findViewById(R.id.news_details_item_publishtime);
        share_weibo = findViewById(R.id.share_weibo);
        share_qq = findViewById(R.id.share_qq);
        mVideoView = findViewById(R.id.video);
        mLoading = findViewById(R.id.loading);
        news_show = findViewById(R.id.news_show);
    }

    public String beautifiedText(String text) {
        String[] lines = text.split("\n");
        char[] finalText = new char[lines.length * 6 + text.length()];
        int now = 0;
        for (String line : lines) {
            boolean skip = true;
            for (char c : line.toCharArray())
                if (c != ' ' && c != '\0' && c != '\n' && c != '\t') {
                    skip = false;
                    break;
                }
            if (skip) continue;
            if (line.charAt(0) == '\t') {
                for (char c : line.toCharArray())
                    finalText[now ++] = c;
            }
            else {
                int i = 0;
                while (i < line.length() && line.charAt(i) == ' ')
                    i ++;
                while (i < 4) {
                    i ++;
                    finalText[now ++] = ' ';
                }
                for (char c : line.toCharArray())
                    finalText[now ++] = c;
            }
            finalText[now ++] = '\n';
            finalText[now ++] = '\n';
        }
        return new String(finalText);
    }

}
