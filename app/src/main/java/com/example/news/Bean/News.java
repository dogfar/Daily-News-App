package com.example.news.Bean;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ts on 18-8-20.
 */

public class News {
    static final String TAG = "News";
    String news_url;
    String news_title;
    String news_content;
    List<String> img_url_list;
//    List<Bitmap> img_list;

    String uniquekey;
    String date;
    String author_name;
    List<String> key_words;
    List<Float> key_words_scores;
    String video_url;

    public News() {
        img_url_list = new ArrayList<>();
        key_words = new ArrayList<>();
        key_words_scores = new ArrayList<>();
    }

    public News(String news_title, String news_url, String date, String author_name, String news_content,
                List<String> img_url_list) {
        this.news_url = news_url;
        this.news_title = news_title;
        this.news_content = news_content;
        this.img_url_list = img_url_list;
//        this.img_list = img_list;
        this.date = date;
        this.author_name = author_name;
    }

    public String getUniquekey() { return uniquekey; }

    public void setUniquekey(String uniquekey) {
        this.uniquekey = uniquekey;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

//    public List<Bitmap> getNews_img() {
//        return img_list;
//    }
//
//    public void setNews_img(List<Bitmap> img_list) {
//        this.img_list = img_list;
//    }

    public String getNews_title() {
        return news_title;
    }

    public void setNews_title(String news_title) {
        this.news_title = news_title;
    }

    public String getNews_url() {
        return news_url;
    }

    public void setNews_url(String news_url) {
        this.news_url = news_url;
    }

    public String getNews_content(){ return news_content; }

    public void setNews_content(String news_content){ this.news_content = news_content; }

    public List<String> getImg_url_list() {
        return img_url_list;
    }

    public void setImg_url_list(List<String> img_url_list) {
        this.img_url_list = img_url_list;
    }

    public void setVideo_url(String url) { this.video_url = url; }

    public String getVideo_url() { return video_url; }

    public void setKey_words(List<String> key_words) {
        this.key_words = key_words;
    }

    public List<String> getKey_words() {
        return key_words;
    }

    public void setKey_words_scores(List<Float> key_words_scores) {
        this.key_words_scores = key_words_scores;
    }

    public List<Float> getKey_words_scores() {
        return key_words_scores;
    }

    public static int getType(Class<?> type) {
        if (type != null && (String.class.isAssignableFrom(type) || Character.class.isAssignableFrom(type) || Character.TYPE.isAssignableFrom(type) || char.class.isAssignableFrom(type)))
            return 0;
        if (type != null && (Byte.TYPE.isAssignableFrom(type) || Short.TYPE.isAssignableFrom(type) || Integer.TYPE.isAssignableFrom(type) || Integer.class.isAssignableFrom(type) || Number.class.isAssignableFrom(type) || int.class.isAssignableFrom(type) || byte.class.isAssignableFrom(type) || short.class.isAssignableFrom(type)))
            return 1;
        if (type != null && (Long.TYPE.isAssignableFrom(type) || long.class.isAssignableFrom(type)))
            return 2;
        if (type != null && (Float.TYPE.isAssignableFrom(type) || float.class.isAssignableFrom(type)))
            return 3;
        if (type != null && (Double.TYPE.isAssignableFrom(type) || double.class.isAssignableFrom(type)))
            return 4;
        if (type != null && (Boolean.TYPE.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type)))
            return 5;
        if (type != null && type.isArray())
            return 6;
        if (type != null && Connection.class.isAssignableFrom(type))
            return 7;
        if (type != null && JSONArray.class.isAssignableFrom(type))
            return 8;
        if (type != null && List.class.isAssignableFrom(type))
            return 9;
        if (type != null && Map.class.isAssignableFrom(type))
            return 10;
        return 11;
    }

    public static String toJson(Object obj)throws IllegalAccessException, JSONException {
        JSONObject json = new JSONObject();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            Log.d(TAG, "toJson: field: " + field.getName() + " " + field.getType());
            switch (getType(field.getType())) {
                case 0:
                    json.put(field.getName(), (field.get(obj) == null ? "" : field.get(obj)));
                    break;
                case 1:
                    json.put(field.getName(), (int) (field.get(obj) == null ? 0 : field.get(obj)));
                    break;
                case 2:
                    json.put(field.getName(), (long) (field.get(obj) == null ? 0 : field.get(obj)));
                    break;
                case 3:
                    json.put(field.getName(), (float) (field.get(obj) == null ? 0 : field.get(obj)));
                    break;
                case 4:
                    json.put(field.getName(), (double) (field.get(obj) == null ? 0 : field.get(obj)));
                    break;
                case 5:
                    json.put(field.getName(), (boolean) (field.get(obj) == null ? false : field.get(obj)));
                    break;
                case 6:
                case 7:
                case 8://JsonArray型
                    json.put(field.getName(), (field.get(obj) == null ? null : field.get(obj)));
                    break;
                case 9:
                    json.put(field.getName(), new JSONArray((List<?>) field.get(obj)));
                    break;
                case 10:
                    json.put(field.getName(), new JSONObject((HashMap<?, ?>) field.get(obj)));
                    break;
            }
        }
        Log.d(TAG, "toJson: " + json.toString());
        return json.toString();
    }

    public static News fromJson(JSONObject json) {
        News news = new News();
        Log.d(TAG, "fromJson: ");
        try {
            news.news_url = json.getString("news_url");
            news.news_title = json.getString("news_title");
            news.news_content = json.getString("news_content");

            Log.d(TAG, "fromJson: middle");

            JSONArray array = json.getJSONArray("img_url_list");
            for (int i = 0; i < array.length(); i++)
                news.img_url_list.add((String)(array.get(i)));

            array = json.getJSONArray("key_words");
            for (int i = 0; i < array.length(); i++)
                news.key_words.add((String)(array.get(i)));

            array = json.getJSONArray("key_words_scores");
            for (int i = 0; i < array.length(); i++)
                news.key_words_scores.add(Float.parseFloat(array.get(i).toString()));

            Log.d(TAG, "fromJson: after list");

            news.uniquekey = json.getString("uniquekey");
            news.date = json.getString("date");
            news.author_name = json.getString("author_name");
            news.video_url = json.getString("video_url");
            if (news.video_url == null)
                news.video_url = "";
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "fromJson: return news");
        return news;
    }
}