package com.example.news.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

import com.example.news.Bean.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewsManager {
    final String TAG = "NewsManager";
    private static NewsManager newsManager = null;
    private static MyBitmapUtils myBitmapUtils;

    private static String url = "https://api2.newsminer.net/svc/news/queryNewsList";
    private static String former_url = "http://api.tianapi.com/military/?key=7d829a4176fef4ad7409c2dc129905ed&num=30";
    private static Date mainDate = new Date();
    private static Date endDate = new Date();
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private static Context context;
    private static MyDatabaseHelper myDBHelper;

    private NewsManager(Context _context) {
        Log.d(TAG, "NewsManager: construction");
        context = _context;
        myBitmapUtils = new MyBitmapUtils(context);
        myDBHelper = MyDatabaseHelper.getInstance(context);
        Log.d(TAG, "NewsManager: context " + context);
    }

    public static NewsManager getInstance(Context context) {
        if (newsManager == null)
            return newsManager = new NewsManager(context);
        return newsManager;
    }

    public void storeNews(String category, News news) {
        try {
            myDBHelper.storeNews(category, news);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void visitNews(News news) {
        String news_url = news.getNews_url();
        List<String> key_words = news.getKey_words();
        List<Float> key_words_scores = news.getKey_words_scores();

        Log.d(TAG, "visitNews: ");
        for (String word : key_words)
            Log.d(TAG, "visitNews: key_words: " + word);
        myDBHelper.visitNews(news_url);
        for (int i = 0; i < (key_words.size() > 2? 2 : key_words.size()); i++)
            myDBHelper.addKeyWord(key_words.get(i), key_words_scores.get(i));
    }

    public boolean isVisited(String url) {
        return myDBHelper.isVisited(url);
    }

    public List<News> getLoadNews(final int count, final String words, final String category) {
        Date startDate = new Date(endDate.getTime() - 3 * 24 * 60 * 60 * 1000);
        String requestStr = url + String.format("?size=%d&startDate=%s&endDate=%s&words=%s&categories=%s",
                count, dateFormat.format(startDate), dateFormat.format(endDate), words, category);
        endDate = startDate;
        String jsonData = null;
        try {
            jsonData = HttpUtils.requestHttp(requestStr);
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "getLoadNews: request error");
            jsonData = null;
        }
        Log.d(TAG, "getLoadNews: request " + requestStr);
        Log.d(TAG, "getLoadNews: get " + jsonData);
        return parseNewsList(jsonData);
    }

    public List<News> getSearchNews(final int count, final String words, final String category) {
//        Date startDate = new Date(endDate.getTime() - 24 * 60 * 60 * 1000);
        String requestStr = url + String.format("?size=%d&words=%s&categories=%s",
                count, words, category);
//        endDate = startDate;
        String jsonData = null;
        try {
            jsonData = HttpUtils.requestHttp(requestStr);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "getSearchNews: request error");
            jsonData = null;
        }
        Log.d(TAG, "getSearchNews: request " + requestStr);
        Log.d(TAG, "getSearchNews: get " + jsonData);
        return parseNewsList(jsonData);
    }

    public List<News> getFreshNews(final int count, final String words, final String category) {
        Date nowDate = new Date();
        String requestStr = url + String.format("?size=%d&startDate=%s&endDate=%s&words=%s&categories=%s",
                count, dateFormat.format(mainDate), dateFormat.format(nowDate), words, category);
        mainDate = nowDate;
        Log.d(TAG, "getFreshNews: " + requestStr);
        String jsonData = null;
        try {
            jsonData = HttpUtils.requestHttp(requestStr);
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "getFreshNews: request error");
            jsonData = null;
        }
        return parseNewsList(jsonData);
    }

    public List<News> getListedReadNews(){
        return myDBHelper.getListedReadNews();
    }

    private List<String> parseImgUrlList(String rawString) {
        List<String> urlList = new ArrayList<>();
        if(urlList.equals("") || urlList.equals("[]"))
            return urlList;
        Pattern p = Pattern.compile("h.*?[,|\\]]");
        Matcher m = p.matcher(rawString);
        int cnt = 0;
        while(m.find() && cnt < 3) {
            String s = m.group();
            urlList.add(s.substring(0,s.length()-1));
            cnt ++;
        }
        return urlList;
    }

    public News parseNews(String string_news) {
        try {
            return parseNews(new JSONObject(string_news));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public News parseNews(JSONObject json_news) {
        try {
            List<String> imgUrlList = parseImgUrlList(json_news.getString("image"));
            List<Bitmap> imgList = new ArrayList<>();

            for (String imgUrl : imgUrlList) {
                Bitmap bitmap = myBitmapUtils.getBitmap(imgUrl);
                imgList.add(bitmap);
            }

            String title = json_news.getString("title");
            String date = json_news.getString("publishTime");
            String author_name = json_news.getString("publisher");
            String url = json_news.getString("url");
            String content = json_news.getString("content");
            String video_url = json_news.getString("video");
            List<String> key_words = new ArrayList<>();
            List<Float> key_words_scores = new ArrayList<>();
            JSONArray key_words_array = json_news.getJSONArray("keywords");
            for (int i = 0; i < key_words_array.length(); i++) {
                JSONObject t = (JSONObject)(key_words_array.get(i));
                key_words.add(t.getString("word"));
                key_words_scores.add(Float.parseFloat(t.getString("score")));
            }

            Log.d(TAG, "parseNews: video_url: " + video_url);
            Log.d(TAG, "parseNews: imgList: " + imgList);
            Log.d(TAG, "parseNews: imgUrl: " + imgUrlList);
            Log.d(TAG, "parseNews: title: " + title);
            Log.d(TAG, "parseNews: key_words size: " + key_words.size());
            for (String key_word : key_words)
                Log.d(TAG, "parseNews: key_words " + key_word);
            for (Float score : key_words_scores)
                Log.d(TAG, "parseNews: key_words_scores " + score);

            News news = new News(title, url, date, author_name, content, imgUrlList);
            news.setKey_words(key_words);
            news.setKey_words_scores(key_words_scores);
            news.setVideo_url(video_url);
            return news;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<News> parseNewsList(String jsonData){
        if (jsonData == null)
            return new ArrayList<>();
        List<News> newsList = new ArrayList<News>();
        try {
            Log.d(TAG, "parseJSONWithGSON: jsonData: " + jsonData);
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json_news = jsonArray.getJSONObject(i);
                News news = parseNews(json_news);
                newsList.add(news);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newsList;
    }

    public News getNews(String url) {
        return myDBHelper.getNews(url);
    }

    public Bitmap getImage(String url) {
        return myBitmapUtils.getBitmap(url);
    }

    public List<Pair<String, Float>> getKeyWords() {
        return myDBHelper.getKeyWords();
    }
}
