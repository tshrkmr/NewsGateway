package edu.depaul.tkumar.newsgateway;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;

import javax.net.ssl.HttpsURLConnection;

import static java.net.HttpURLConnection.HTTP_OK;

public class NewsDownloaderRunnable implements Runnable{

    private static final String TAG = "NewsDownloaderRunnable";
    private static final String dataURL = "https://newsapi.org/v2/sources?apiKey=9f195d1f01764b9598c8b2d29108e2bd";
    private MainActivity mainActivity;

    public NewsDownloaderRunnable(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        Uri uri = Uri.parse(dataURL);
        String urlToUse = uri.toString();

        //String urlToUse = builderURL.build().toString();
        try {


            URL url = new URL(urlToUse);
            Log.d(TAG, "run: " + urlToUse);

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
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
                Log.d(TAG, "run: " + sb.toString());
//                HashMap<String, HashSet<String>> sourcesMap = parseJSON(sb.toString());
//                if (sourcesMap != null) {
//                    //mainActivity.runOnUiThread(() -> mainActivity.setUpSources(sourcesMap));
//                }
            } else {
                BufferedReader reader =
                        new BufferedReader((new InputStreamReader(conn.getErrorStream())));

                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                conn.disconnect();
                Log.d(TAG, "run: " + sb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
