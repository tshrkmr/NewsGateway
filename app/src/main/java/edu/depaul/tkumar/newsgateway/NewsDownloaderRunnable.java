package edu.depaul.tkumar.newsgateway;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import static java.net.HttpURLConnection.HTTP_OK;

public class NewsDownloaderRunnable implements Runnable{

    private static final String dataURL = "https://newsapi.org/v2/top-headlines";
    private final MainActivity mainActivity;
    private final String source;
    private static final String TAG = "NewsDownloaderRunnable";

    public NewsDownloaderRunnable(MainActivity mainActivity, String source) {
        this.mainActivity = mainActivity;
        this.source = source;
    }

    @Override
    public void run() {
        Uri.Builder builderURL = Uri.parse(dataURL).buildUpon();
        builderURL.appendQueryParameter("sources", source);
        //String apiValue1 = "38f6b24dd9c94683bc4fd821d1bba0f9";
        String apiValue = "9f195d1f01764b9598c8b2d29108e2bd";
        builderURL.appendQueryParameter("apiKey", apiValue);
        String urlToUse = builderURL.build().toString();
        try {
            URL url = new URL(urlToUse);
            Log.d(TAG, "run: " + urlToUse);

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent","");
            conn.connect();

            StringBuilder sb = new StringBuilder();
            String line;

            if (conn.getResponseCode() == HTTP_OK) {
                BufferedReader reader =
                        new BufferedReader((new InputStreamReader(conn.getInputStream())));

                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                conn.disconnect();
                parseJSON(sb.toString());
                Log.d(TAG, "run: " + sb.toString());
            } else {
                BufferedReader reader =
                        new BufferedReader((new InputStreamReader(conn.getErrorStream())));

                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                conn.disconnect();
                mainActivity.runOnUiThread(()->mainActivity.showError(sb.toString()));
                Log.d(TAG, "run: " + sb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseJSON(String s) {
        ArrayList<NewsHeadline> newsHeadlineArrayList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray = jsonObject.getJSONArray("articles");

            for (int i = 0; i < jsonArray.length(); i++) {
                String author = "no value returned";
                String title = "no value returned";
                String description = "no value returned";
                String url = "no value returned";
                String urlToImage = "no value returned";
                String publishedAt = "no value returned";
                JSONObject sources = (JSONObject) jsonArray.get(i);
                if(sources.has("author")){
                    author = sources.getString("author");
                    //Log.d(TAG, "parseJSON: " + author);
                }
                if(sources.has("title")){
                    title = sources.getString("title");
                    //Log.d(TAG, "parseJSON: " + title);
                }
                if(sources.has("description")){
                    description = sources.getString("description");
                    Log.d(TAG, "parseJSON: " + description);
                }
                if(sources.has("url")){
                    url = sources.getString("url");
                    //Log.d(TAG, "parseJSON: " + url);
                }
                if(sources.has("urlToImage")){
                    urlToImage = sources.getString("urlToImage");
                    //Log.d(TAG, "parseJSON: " + urlToImage);
                }
                if(sources.has("publishedAt")){
                    publishedAt = sources.getString("publishedAt");
                    //Log.d(TAG, "parseJSON: " + publishedAt);
                }
                NewsHeadline newsHeadline = new NewsHeadline(author, title, description, url, urlToImage, publishedAt);
                newsHeadlineArrayList.add(newsHeadline);
            }
            mainActivity.runOnUiThread(()->mainActivity.setTopHeadlines(newsHeadlineArrayList));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
