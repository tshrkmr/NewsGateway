package edu.depaul.tkumar.newsgateway;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;

class SourceDataContainer implements Runnable{

    private static final String TAG = "SourceDataContainer";
    private MainActivity context;
    private HashMap<String, String> countryCodes = new HashMap<>();
    private HashMap<String, String> languageCodes = new HashMap<>();

    public SourceDataContainer(MainActivity context) {
        this.context = context;
    }

    @Override
    public void run() {
        try {
            readCodesFiles(context);
            JSONObject jsonObject = loadJSONData(context);
            //HashMap<String, HashSet<String>> sourcesMap = parseJSON(jsonObject);
            parseJSON(jsonObject);
//            if (sourcesMap != null) {
//                context.runOnUiThread(() -> context.setUpSources(sourcesMap));
//                //context.setUpSources(sourcesMap);
//            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    //private static HashMap<String, HashSet<String>> parseJSON(JSONObject jsonObject) {
    private void parseJSON(JSONObject jsonObject) {
        HashMap<String, HashSet<String>> topicMap = new HashMap<>();
        HashMap<String, HashSet<String>> countryMap = new HashMap<>();
        HashMap<String, HashSet<String>> languageMap = new HashMap<>();
        try {
            //JSONObject jsonObject = new JSONObject(s);
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
                    category =
                            sources.getString("category");
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

                HashSet<String> tSet = topicMap.get(category);
                if (tSet != null) {
                    tSet.add(name);
                }

                if (!languageMap.containsKey(language)) {
                    //Log.d(TAG, "parseJSON: " + language + "  " + languageCodes.get(language.toUpperCase()));
                    languageMap.put(languageCodes.get(language.toUpperCase()), new HashSet<>());
                }

                HashSet<String> lSet = languageMap.get(language);
                if (lSet != null) {
                    lSet.add(name);
                }

                if (!countryMap.containsKey(country))
                    countryMap.put(countryCodes.get(country.toUpperCase()).toString(), new HashSet<>());

                HashSet<String> cSet = topicMap.get(country);
                if (cSet != null) {
                    cSet.add(name);
                }
            }
            if (topicMap != null && languageMap != null && countryMap != null) {
                context.runOnUiThread(() -> context.setUpSources(topicMap, languageMap, countryMap));
                //context.setUpSources(sourcesMap);
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    private static JSONObject loadJSONData(Context context) throws IOException, JSONException {
        InputStream is = context.getResources().openRawResource(R.raw.sources_data);

        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

        String line = reader.readLine();
        while (line != null) {
            sb.append(line);
            line = reader.readLine();
        }
        reader.close();
        Log.d(TAG, "loadJSONData: " + sb.toString());
        return new JSONObject(sb.toString());
    }

    private void readCodesFiles(Context context) throws IOException{
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
            Log.d(TAG, "parseCountryCodes: " + countryCodes);
        } catch (
                Exception e) {
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
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }
}

