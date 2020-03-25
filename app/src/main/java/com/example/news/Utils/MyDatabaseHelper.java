package com.example.news.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;

import com.example.news.Bean.News;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    static final private String TAG = "MyDatabaseHelper";
    private String[] categoryList = {"娱乐", "军事", "教育", "文化", "健康", "财经", "体育", "汽车", "科技", "社会"};
    private Context mContext;
    private List<String> newlyWords;
    private float alpha = 2;
    private int wordsMaxSize = 4;

    static private MyDatabaseHelper instance = null;

    public static final String CREATE_USER = "create table User ("
            + "id integer primary key autoincrement,"
            + "name text,"
            + "password text)";
    public static final String CREATE_COLLECTION_NEWS = "create table Collection_News ("
            + "id integer primary key autoincrement,"
            + "news_url text)";
    public static final String CREATE_VISITED_NEWS = "create table Visited_News ("
            + "id integer primary key autoincrement,"
            + "news_url text)";
    public static final String CREATE_STORED_NEWS = "create table Stored_News ("
            + "id integer primary key autoincrement,"
            + "news_url text,"
            + "news_date text,"
            + "news_category text,"
            + "news_json text)";
    public static final String CREATE_KEY_WORDS = "create table Key_Words ("
            + "id integer primary key autoincrement,"
            + "key_word text,"
            + "score integer)";

    private MyDatabaseHelper(Context context) {
        super(context, "data.db", null, 1);
        mContext = context;
        newlyWords = new ArrayList<>();
    }

    static public MyDatabaseHelper getInstance(Context context) {
        if (instance == null)
            instance = new MyDatabaseHelper(context);
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "onCreate: ");
        sqLiteDatabase.execSQL(CREATE_USER);
        sqLiteDatabase.execSQL(CREATE_COLLECTION_NEWS);
        sqLiteDatabase.execSQL(CREATE_VISITED_NEWS);
        sqLiteDatabase.execSQL(CREATE_STORED_NEWS);
        sqLiteDatabase.execSQL(CREATE_KEY_WORDS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.d(TAG, "onUpgrade: ");
    }

    public News getNews(String url) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(String.format("Select * from Stored_News Where news_url = \"%s\"", url), null);
        Log.d(TAG, "getNews: " + String.format("Select * from Stored_News Where news_url = \"%s\"", url));
        if (cursor.moveToFirst()) {
            String news_json = cursor.getString(cursor.getColumnIndex("news_json"));
            db.close();
            Log.d(TAG, "getNews: news_json_string -> json" + news_json);
            News news = null;
            try {
                news = News.fromJson(new JSONObject(news_json));
                Log.d(TAG, "getNews: after fromJson: ");
                Log.d(TAG, "getNews: " + news.getNews_url());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return news;
        }
        return null;
    }

    public News getReadNews(String url){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(String.format("Select * from Stored_News Where news_url = \"%s\"", url), null);
        Log.d(TAG, "getNews: " + String.format("Select * from Stored_News Where news_url = \"%s\"", url));
        if (cursor.moveToFirst()) {
            String news_json = cursor.getString(cursor.getColumnIndex("news_json"));
            db.close();
            Log.d(TAG, "getNews: news_json_string -> json" + news_json);
            News news = null;
            try {
                news = News.fromJson(new JSONObject(news_json));
                news.getImg_url_list().clear();
                Log.d(TAG, "getNews: after fromJson: ");
                Log.d(TAG, "getNews: " + news.getNews_url());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return news;
        }
        return null;
    }
    public List<News> getListedReadNews(){
        List <News> ReadNewsList= new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from Stored_News",null);
        while(cursor.moveToNext()){
            String readUrl = cursor.getString(cursor.getColumnIndex("news_url"));
            News news=getReadNews(readUrl);
            if(news!=null)ReadNewsList.add(news);
        }
        return ReadNewsList;
    }

    public void storeNews(String category, News news) {
        if (isStored(news.getNews_url()))
            return;

        Log.d(TAG, "storeNews: " + news.getNews_url());
        String news_url = news.getNews_url();
        String news_date = news.getDate();
        String news_category = category;
        String news_json = null;
        try {
            news_json = (News.toJson(news)).toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            news_json = "";
        }

        Log.d(TAG, "storeNews: JSONString: " + news_json);

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("news_url", news_url);
        values.put("news_date", news_date);
        values.put("news_category", news_category);
        values.put("news_json", news_json);
        db.insert("Stored_News", null, values);
        db.close();
    }

    public boolean isStored(String url) {
        SQLiteDatabase db = getReadableDatabase();
        Log.d(TAG, "isStored: " + String.format("Select * from Stored_News Where news_url = \"%s\"", url));
        Cursor cursor = db.rawQuery(String.format("Select * from Stored_News Where news_url = \"%s\"", url), null);
        Log.d(TAG, "isStored: " + url + " " + String.valueOf(cursor.getCount()));
        return cursor.getCount() > 0;
    }

    public boolean isCollected(String url) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(String.format("Select * from Collection_News Where news_url = \"%s\"", url), null);
        return cursor.getCount() > 0;
    }

    public void collectNews(String url) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("news_url", url);
        db.insert("Collection_News", null, values);
        db.close();
    }

    public int uncollectNews(String url) {
        SQLiteDatabase db = getWritableDatabase();
        int flag = db.delete("Collection_News", "news_url = ?", new String[] {url});
        db.close();
        return flag;
    }

    public List<News> getCollection() {
        List<String> newsUrlList = new ArrayList<>();
        List<News> newsList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(String.format("Select * from Collection_News"), null);
        Log.d(TAG, "getCollection: " + String.format("Select * from Collection_News"));
        if (cursor.getCount() > 0)
            if (cursor.moveToFirst()) {
                do {
                    String news_url = cursor.getString(cursor.getColumnIndex("news_url"));
//                    News news = getNews(news_url);
                    newsUrlList.add(news_url);
                } while (cursor.moveToNext());
            }
        for (String url : newsUrlList)
            newsList.add(getNews(url));
        db.close();
        Log.d(TAG, "getCollection: " + newsList.size());
        return newsList;
    }

    public void visitNews(String url) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("news_url", url);
        db.insert("Visited_News", null, values);
        db.close();
    }

    public boolean isVisited(String url) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(String.format("Select * from Visited_News Where news_url = \"%s\"", url), null);
        return cursor.getCount() > 0;
    }

    public List<Pair<String, Float>> getKeyWords() {
        List<String> keyWords = new ArrayList<>();
        List<Float> scores = new ArrayList<>();
        List<Pair<String, Float>> pairList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(String.format("Select * from Key_Words"), null);
        Log.d(TAG, "getKeyWords: " + String.format("Select * from Key_Words"));
        if (cursor.getCount() > 0)
            if (cursor.moveToFirst()) {
                do {
                    String word = cursor.getString(cursor.getColumnIndex("key_word"));
                    Float score = cursor.getFloat(cursor.getColumnIndex("score"));
                    pairList.add(new Pair<String, Float>(word, score));
                } while (cursor.moveToNext());
            }
        db.close();
        Log.d(TAG, "getKeyWords: " + pairList.size());
        for (Pair p : pairList) {
            Log.d(TAG, "getKeyWords: " + p.first + " " + p.second);
        }
        return pairList;
    }

    private void increaseScore(String word, Float score) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(String.format("Select * from Key_Words Where key_word = \"%s\"", word), null);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst())
                score += cursor.getFloat(cursor.getColumnIndex("score"));
            ContentValues values = new ContentValues();
            values.put("key_word", word);
            values.put("score", score);
            db.update("Key_Words", values, "key_word=?", new String[] {String.valueOf(word)});
            Log.d(TAG, "second increaseScore: key_word " + word + " score " + String.valueOf(score));
        }
        else {
            ContentValues values = new ContentValues();
            values.put("key_word", word);
            values.put("score", score);
            db.insert("Key_Words", null, values);
            Log.d(TAG, "first increaseScore: key_word " + word + " score " + String.valueOf(score));
        }
        db.close();
    }

    public void addKeyWord(String word, Float score) {
        Log.d(TAG, "addKeyWord: " + word + " " + score);
        boolean flag = false;
        for (String word_ : newlyWords)
            if (word_.equals(word))
                flag = true;

        if (! flag) {
            newlyWords.add(0, word);
            Log.d(TAG, "addKeyWord: size" + newlyWords.size());
            if (newlyWords.size() > wordsMaxSize) {
                String word_ = newlyWords.get(wordsMaxSize);
                newlyWords.remove(wordsMaxSize);
                increaseScore(word_, -alpha);
                Log.d(TAG, "addKeyWord: pop" + word_);
            }
            increaseScore(word, score + alpha);
        }
        else
            increaseScore(word, score);
    }

    public void clearKeyWords() {
        for (String word : newlyWords)
            increaseScore(word, - alpha);
    }
}
