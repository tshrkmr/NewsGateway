package edu.depaul.tkumar.newsgateway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //API Key - 38f6b24dd9c94683bc4fd821d1bba0f9
    private final HashMap<String, ArrayList<String>> topicsData = new HashMap<>();
    private final HashMap<String, ArrayList<String>> languageData = new HashMap<>();
    private final HashMap<String, ArrayList<String>> countryData = new HashMap<>();
    private final ArrayList<String> newsOutletsDisplayed = new ArrayList<>();
    private final ArrayList<String> subLanguageDisplayed = new ArrayList<>();
    private final ArrayList<String> subCountryDisplayed = new ArrayList<>();
    private DrawerLayout drawerLayout;
    private ListView listView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Menu menu;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeLayout();
        if(topicsData.isEmpty())
            new Thread(new SourceDownloaderRunnable(this)).start();
            //new Thread(new SourceDataContainer(this));
        //new Thread(new NewsDownloaderRunnable(this)).start();
    }

    private void initializeLayout(){
        drawerLayout = findViewById(R.id.mainDrawerlayout);
        listView = findViewById(R.id.mainDrawerList);

        // Set up the drawer item click callback method
        listView.setOnItemClickListener(
                (parent, view, position, id) -> {
                    //selectItem(position);
                    drawerLayout.closeDrawer(listView);
                }
        );

        // Create the drawer toggle
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );
    }

    // You need this to set up the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        return true;
    }

    public void setUpSources(HashMap<String, HashSet<String>> topicMapIn, HashMap<String, HashSet<String>> languageMapIn, HashMap<String, HashSet<String>> coountryMapIn){
        topicsData.clear();
        languageData.clear();
        countryData.clear();

        for (String s : topicMapIn.keySet()) {
            HashSet<String> tSet = topicMapIn.get(s);
            if (tSet == null)
                continue;
            ArrayList<String> subTopics = new ArrayList<>(tSet);
            Collections.sort(subTopics);
            topicsData.put(s, subTopics);
            Log.d(TAG, "setUpSources: " + s + " " +  subTopics);
        }
        ArrayList<String> tempTopicList = new ArrayList<>(topicsData.keySet());
        Collections.sort(tempTopicList);
        SubMenu topicsMenu = menu.addSubMenu("Topics");
        for (String s : tempTopicList)
            topicsMenu.add(s);


        for (String s : languageMapIn.keySet()) {
            HashSet<String> lSet = languageMapIn.get(s);
            if (lSet == null)
                continue;
            ArrayList<String> subLanguages = new ArrayList<>(lSet);
            Collections.sort(subLanguages);
            languageData.put(s, subLanguages);
            //Log.d(TAG, "setUpSources: " + topicsData);
        }
        ArrayList<String> tempLanguageList = new ArrayList<>(languageData.keySet());
        Collections.sort(tempLanguageList);
        SubMenu languageMenu = menu.addSubMenu("Language");
        for (String s : tempLanguageList)
            languageMenu.add(s);

        for (String s : coountryMapIn.keySet()) {
            HashSet<String> cSet = coountryMapIn.get(s);
            if (cSet == null)
                continue;
            ArrayList<String> subCountries = new ArrayList<>(cSet);
            Collections.sort(subCountries);
            countryData.put(s, subCountries);
            //Log.d(TAG, "setUpSources: " + topicsData);
        }
        ArrayList<String> tempCountryList = new ArrayList<>(countryData.keySet());
        Collections.sort(tempCountryList);
        SubMenu countryMenu = menu.addSubMenu("Country");
        for (String s : tempCountryList)
            countryMenu.add(s);
    }

    // You need the 2 below to make the drawer-toggle work properly:

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    // You need the below to open the drawer when the toggle is clicked
    // Same method is called when an options menu item is selected.

    public boolean onOptionsItemSelected(MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }

        setTitle(item.getTitle());

        newsOutletsDisplayed.clear();
//        ArrayList<String> lst = regionData.get(item.getTitle().toString());
//        if (lst != null) {
//            subRegionDisplayed.addAll(lst);
//        }
//
//        ((ArrayAdapter) mDrawerList.getAdapter()).notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }
}