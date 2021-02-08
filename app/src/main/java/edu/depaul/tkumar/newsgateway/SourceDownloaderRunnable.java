package edu.depaul.tkumar.newsgateway;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;

import javax.net.ssl.HttpsURLConnection;

import static java.net.HttpURLConnection.HTTP_OK;

public class SourceDownloaderRunnable implements Runnable{
    private final MainActivity mainActivity;
    private final HashMap<String, String> countryCodes = new HashMap<>();
    private final HashMap<String, String> languageCodes = new HashMap<>();
    private static final String dataURL = "https://newsapi.org/v2/sources";
    private String apiValue = "38f6b24dd9c94683bc4fd821d1bba0f9";
    private String apiValue2 = "9f195d1f01764b9598c8b2d29108e2bd";
    private static final String TAG = "SourceDownloaderRunnable";

    public SourceDownloaderRunnable(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {

        Uri.Builder builderURL = Uri.parse(dataURL).buildUpon();
        builderURL.appendQueryParameter("apiKey", apiValue);

//        Uri uri = Uri.parse(dataURL);
//        String urlToUse = uri.toString();
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
                Log.d(TAG, "run: " + sb.toString());
                parseJSON(sb.toString());
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

    //private HashMap<String, HashSet<String>> parseJSON(String s) {
    private void parseJSON(String s) {
        try {
            readCodesFiles(mainActivity);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HashMap<String, HashSet<String>> countryMap = new HashMap<>();
        HashMap<String, HashSet<String>> languageMap = new HashMap<>();
        try {
            HashMap<String, HashSet<String>> topicMap = new HashMap<>();
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray = jsonObject.getJSONArray("sources");

            // Here we only want to regions and subregions
            for (int i = 0; i < jsonArray.length(); i++) {
                String id = "no value returned";
                String name = "no value returned";
                String category = "no value returned";
                String language = "no value returned";
                String country = "no value returned";
                JSONObject sources = (JSONObject) jsonArray.get(i);
                if(sources.has("id")){
                    id = sources.getString("id");
                    //Log.d(TAG, "parseJSON: " + id);
                }
                if(sources.has("name")){
                    name = sources.getString("name");
                    //Log.d(TAG, "parseJSON: " + name);
                }
                if(sources.has("category")){
                    category = sources.getString("category");
                    //Log.d(TAG, "parseJSON: " + category);
                }
                if(sources.has("language")){
                    language = sources.getString("language");
                    //Log.d(TAG, "parseJSON: " + language);
                }
                if(sources.has("country")){
                    country = sources.getString("country");
                    //Log.d(TAG, "parseJSON: " + country);
                }

                //NewsSources newsSources = new NewsSources(id, name, category, language, country);
                if (!topicMap.containsKey(category))
                    topicMap.put(category.toUpperCase(), new HashSet<>());

                HashSet<String> tSet = topicMap.get(category.toUpperCase());
                if(tSet != null){
                    tSet.add(name);
                }

                if (!languageMap.containsKey(language)) {
                    //Log.d(TAG, "parseJSON: " + language + "  " + languageCodes.get(language.toUpperCase()));
                    languageMap.put(languageCodes.get(language.toUpperCase()).toString(), new HashSet<>());
                }

                HashSet<String> lSet = languageMap.get(languageCodes.get(language.toUpperCase()).toString());
                if (lSet != null) {
                    lSet.add(name);
                    //Log.d(TAG, "parseJSON: " + name);
                }

                if (!countryMap.containsKey(country))
                    countryMap.put(countryCodes.get(country.toUpperCase()).toString(), new HashSet<>());

                HashSet<String> cSet = countryMap.get(countryCodes.get(country.toUpperCase()).toString());
                if (cSet != null) {
                    cSet.add(name);
                    //Log.d(TAG, "parseJSON: " + name);
                }
            }
            if (topicMap != null && languageMap != null && countryMap != null) {
                mainActivity.runOnUiThread(() -> mainActivity.setUpSources(topicMap, languageMap, countryMap));
                //context.setUpSources(sourcesMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readCodesFiles(Context context) throws IOException {
        InputStream is = context.getResources().openRawResource(R.raw.country_codes);
        InputStream is1 = context.getResources().openRawResource(R.raw.language_codes);
        StringBuilder sb = new StringBuilder();
        StringBuilder sb1 = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        BufferedReader reader1 = new BufferedReader(new InputStreamReader(is1, StandardCharsets.UTF_8));

        String line = reader.readLine();
        while (line != null) {
            sb.append(line);
            line = reader.readLine();
        }
        reader.close();

        String line1 = reader1.readLine();
        while (line1 != null) {
            sb1.append(line1);
            line1 = reader1.readLine();
        }
        reader1.close();

        parseCountryCodes(sb.toString());
        parseLanguageCodes(sb1.toString());
    }

    private void parseCountryCodes(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray = jsonObject.getJSONArray("countries");

            // Here we only want to regions and subregions
            for (int i = 0; i < jsonArray.length(); i++) {
                String code = "no value returned";
                String name = "no value returned";
                JSONObject sources = (JSONObject) jsonArray.get(i);
                if(sources.has("code")){
                    code = sources.getString("code");
                    //Log.d(TAG, "parseJSON: " + code);
                }
                if(sources.has("name")){
                    name = sources.getString("name");
                    //Log.d(TAG, "parseJSON: " + name);
                }
                //NewsSources newsSources = new NewsSources(id, name, category, language, country);
                if (!countryCodes.containsKey(code))
                    countryCodes.put(code, name);
            }
            //Log.d(TAG, "parseCountryCodes: " + countryCodes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseLanguageCodes(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray = jsonObject.getJSONArray("languages");

            // Here we only want to regions and subregions
            for (int i = 0; i < jsonArray.length(); i++) {
                String code = "no value returned";
                String name = "no value returned";
                JSONObject sources = (JSONObject) jsonArray.get(i);
                if(sources.has("code")){
                    code = sources.getString("code");
                    //Log.d(TAG, "parseJSON: " + id);
                }
                if(sources.has("name")){
                    name = sources.getString("name");
                    //Log.d(TAG, "parseJSON: " + name);
                }
                //NewsSources newsSources = new NewsSources(id, name, category, language, country);
                if (!languageCodes.containsKey(code))
                    languageCodes.put(code, name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
