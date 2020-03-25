package com.example.news.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.news.Activity.MainActivity;
import com.example.news.Activity.ShowNewsActivity;
import com.example.news.Activity.ShowWithVideo;
import com.example.news.Bean.News;
import com.example.news.R;
import com.example.news.Utils.NewsManager;

import java.util.HashMap;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private final String TAG = "NewsAdapter";
    private final int EMPTY_VIEW = -1;
    private final int NO_IMAGE_VIEW = 0;
    private final int SINGLE_IMAGE_VIEW = 1;
    private final int MULTI_IMAGE_VIEW = 2;
    private final int FOOTER_VIEW = 3;
    private final int VIDEO_VIEW = 4;

    private Context context;
    private List<News> newsList;
    private String category;

    public abstract static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView newsTitle;
        TextView newsAuthor;
        TextView newsDate;

        public NewsViewHolder(View v) {
            super(v);
        }

        abstract public void setNews(News news);
    }

    public static class NewsNoImageViewHolder extends NewsViewHolder {

        public NewsNoImageViewHolder(View v) {
            super(v);
            Log.d("NewsAdapter", "NewsNoImageViewHolder: " + v.toString());
            newsTitle = v.findViewById(R.id.news_item_title);
            newsAuthor = v.findViewById(R.id.news_item_author);
            newsDate = v.findViewById(R.id.news_item_date);
        }

        @Override
        public void setNews(News news) {
            Log.d("NewsAdapter", "NewsNoImageViewHolder: setNews date" + news.getDate());
            this.newsTitle.setText(news.getNews_title());
            this.newsAuthor.setText(news.getAuthor_name());
            this.newsDate.setText(news.getDate());
        }
    }

    public static class NewsSingleImageViewHolder extends NewsViewHolder {
        ImageView newsImg;

        public NewsSingleImageViewHolder(View v) {
            super(v);
            Log.d("NewsAdapter", "NewsSingleImageViewHolder: " + v.toString());
            newsTitle = v.findViewById(R.id.news_item_title);
            newsAuthor = v.findViewById(R.id.news_item_author);
            newsDate = v.findViewById(R.id.news_item_date);
            newsImg = v.findViewById(R.id.news_item_img);
        }

        @Override
        public void setNews(News news) {
            Log.d("NewsAdapter", "NewsSingleImageViewHolder: setNews date" + news.getDate());
            this.newsTitle.setText(news.getNews_title());
            this.newsAuthor.setText(news.getAuthor_name());
            this.newsDate.setText(news.getDate());
            try {
                this.newsImg.setImageBitmap((NewsManager.getInstance(null)).getImage(news.getImg_url_list().get(0)));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class NewsVideoViewHolder extends NewsViewHolder  {
        ImageView newsImg;
        public NewsVideoViewHolder(View v) {
            super(v);
            Log.d("NewsAdapter", "NewsSingleImageViewHolder: " + v.toString());
            newsTitle = v.findViewById(R.id.news_item_title);
            newsAuthor = v.findViewById(R.id.news_item_author);
            newsDate = v.findViewById(R.id.news_item_date);
            newsImg = v.findViewById(R.id.news_item_img);
        }

        @Override
        public void setNews(News news) {
            Log.d("NewsAdapter", "NewsSingleImageViewHolder: setNews date" + news.getDate());
            this.newsTitle.setText(news.getNews_title());
            this.newsAuthor.setText(news.getAuthor_name());
            this.newsDate.setText(news.getDate());
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            Bitmap bitmap=null;
            try {
                retriever.setDataSource(news.getVideo_url(), new HashMap<String, String>());
                bitmap = retriever.getFrameAtTime();
            }catch(Exception e){
                e.printStackTrace();
            }
                if(bitmap!=null)this.newsImg.setImageBitmap(bitmap);
        }

    }
    public static class NewsMultiImgViewHolder extends NewsViewHolder  {
        ImageView newsImg_1;
        ImageView newsImg_2;
        ImageView newsImg_3;

        public NewsMultiImgViewHolder(View v) {
            super(v);
            Log.d("NewsAdapter", "NewsMultiImgViewHolder: " + v.toString());
            newsTitle = v.findViewById(R.id.news_item_title);
            newsAuthor = v.findViewById(R.id.news_item_author);
            newsDate = v.findViewById(R.id.news_item_date);
            newsImg_1 = v.findViewById(R.id.news_item_img_1);
            newsImg_2 = v.findViewById(R.id.news_item_img_2);
            newsImg_3 = v.findViewById(R.id.news_item_img_3);
        }

        @Override
        public void setNews(News news) {
            Log.d("NewsAdapter", "NewsMultiImgViewHolder: setNews date" + news.getDate());

            this.newsTitle.setText(news.getNews_title());
            this.newsAuthor.setText(news.getAuthor_name());
            this.newsDate.setText(news.getDate());

            try {
                this.newsImg_1.setImageBitmap((NewsManager.getInstance(null)).getImage(news.getImg_url_list().get(0)));
                this.newsImg_2.setImageBitmap((NewsManager.getInstance(null)).getImage(news.getImg_url_list().get(1)));
                this.newsImg_3.setImageBitmap((NewsManager.getInstance(null)).getImage(news.getImg_url_list().get(2)));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class FooterViewHolder extends NewsViewHolder {
        public FooterViewHolder(View v) {
            super(v);
            Log.d("NewsAdapter", "FooterViewHolder: " + v.toString());
        }

        @Override
        public void setNews(News news) {
            Log.d("NewsAdapter", "FooterViewHolder: setNews date" + news.getDate());
        }
    }

    public NewsAdapter(Context context, String category, List<News> data) {
        Log.d(TAG, "NewsAdapter: in");
        this.context = context;
        this.newsList = data;
        this.category = category;
    }

    @Override
    public int getItemViewType(int position) {
        if (newsList.size() == 0)
            return  EMPTY_VIEW;
        if (position == newsList.size())
            return FOOTER_VIEW;
        if(!newsList.get(position).getVideo_url().equals("")){
            return VIDEO_VIEW;
        }
        switch (newsList.get(position).getImg_url_list().size()) {
            case 0:
                return NO_IMAGE_VIEW;
            case 1:
                return SINGLE_IMAGE_VIEW;
            case 2:
                return SINGLE_IMAGE_VIEW;
            case 3:
                return MULTI_IMAGE_VIEW;
            default:
                return MULTI_IMAGE_VIEW;
        }
    }

    @Override
    public void onBindViewHolder(final NewsViewHolder viewHolder, final int position) {
        if (position == newsList.size())
            return;
        final News news = newsList.get(position);
        Log.d("NewsAdapter", "onBindViewHolder: " + news.toString());
        viewHolder.setNews(news);
        String url = newsList.get(position).getNews_url();
        if (NewsManager.getInstance(context).isVisited(url) && category != "收藏" && category != "已读")
                viewHolder.itemView.setBackgroundColor(Color.parseColor("#E6E6E6"));
        else {
            if(MainActivity.Day){
                Log.d(TAG, "onBindViewHolder: day");
                viewHolder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                viewHolder.newsAuthor.setTextColor(Color.parseColor("#FFBEBEBE"));
                viewHolder.newsDate.setTextColor(Color.parseColor("#FFBEBEBE"));
                viewHolder.newsTitle.setTextColor(Color.parseColor("#000000"));
            }
            else{
                Log.d(TAG, "onBindViewHolder: night");
                viewHolder.itemView.setBackgroundColor(Color.parseColor("#5e5e5e"));
                viewHolder.newsAuthor.setTextColor(Color.parseColor("#FFFFFF"));
                viewHolder.newsDate.setTextColor(Color.parseColor("#FFFFFF"));
                viewHolder.newsTitle.setTextColor(Color.parseColor("#FFFFFF"));
            }
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + position);
                String video_url = news.getVideo_url();
                Intent intent=null;
                if(!video_url.equals(""))
                    intent = new Intent(context, ShowWithVideo.class);
                else{
                    intent = new Intent(context, ShowNewsActivity.class);
                }
                System.out.println(video_url);
                String url = news.getNews_url();
                intent.putExtra("url", url);
                intent.putExtra("category", category);
                viewHolder.itemView.setBackgroundColor(Color.parseColor("#E6E6E6"));

                if (category != "收藏" && category != "已读")
                    viewHolder.itemView.setBackgroundColor(Color.parseColor("#E6E6E6"));

                NewsManager.getInstance(context).visitNews(news);
                NewsManager.getInstance(context).storeNews(category, news);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                    }
                }).start();

                context.startActivity(intent);
            }
        });
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG + "_" + category, "onCreateViewHolder: ");
        View v;
        switch (viewType) {
            case NO_IMAGE_VIEW:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_no_img, parent, false);
                return new NewsNoImageViewHolder(v);
            case SINGLE_IMAGE_VIEW:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_single_img, parent, false);
                return new NewsSingleImageViewHolder(v);
            case MULTI_IMAGE_VIEW:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_multi_img, parent, false);
                return new NewsMultiImgViewHolder(v);
            case FOOTER_VIEW:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer, parent, false);
                return new FooterViewHolder(v);
            case VIDEO_VIEW:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_video, parent, false);
                return new NewsVideoViewHolder(v);
            default:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_no_img, parent, false);
                return new NewsNoImageViewHolder(v);
        }
    }

    @Override
    public int getItemCount() {
        if (category.equals("收藏") || category.equals("推荐") || category.equals("已读"))
            return newsList.size();
        else
            return newsList.size() == 0? newsList.size() : newsList.size() + 1;
    }
}