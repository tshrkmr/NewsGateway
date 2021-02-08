package edu.depaul.tkumar.newsgateway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    //API Key - 38f6b24dd9c94683bc4fd821d1bba0f9
    private final HashMap<String, ArrayList<String>> topicsData = new HashMap<>();
    private final HashMap<String, ArrayList<String>> languageData = new HashMap<>();
    private final HashMap<String, ArrayList<String>> countryData = new HashMap<>();
    private final ArrayList<String> newsOutletsDisplayed = new ArrayList<>();
    private String prev;
    String topic = "All";
    String language = "All";
    String country = "All";
    private String stringTopics = "Topics";
    private String stringCountries = "Countries";
    private String stringLanguages = "Languages";
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

    public void setUpSources(HashMap<String, HashSet<String>> topicMapIn, HashMap<String, HashSet<String>> languageMapIn, HashMap<String, HashSet<String>> coountryMapIn, ArrayList<String> allNewsOutlets){
        topicsData.clear();
        languageData.clear();
        countryData.clear();
        newsOutletsDisplayed.clear();
        ArrayList<String> allTopics = new ArrayList<>();
        ArrayList<String> allLanguages = new ArrayList<>();
        ArrayList<String> allCountries = new ArrayList<>();
        for (String s : topicMapIn.keySet()) {
            HashSet<String> tSet = topicMapIn.get(s);
            if (tSet == null)
                continue;
            ArrayList<String> subTopics = new ArrayList<>(tSet);
            Collections.sort(subTopics);
            topicsData.put(s, subTopics);
            allTopics.addAll(subTopics);
            //allNewsOutlets1.addAll(subTopics);
            Log.d(TAG, "setUpSources: " + s + " " +  subTopics);
        }
        Collections.sort(allTopics);
        topicsData.put("All", allTopics);
        ArrayList<String> tempTopicList = new ArrayList<>(topicsData.keySet());
        Collections.sort(tempTopicList);
        SubMenu topicsMenu = menu.addSubMenu(R.string.topics);
        for (String s : tempTopicList)
            topicsMenu.add(s);


        for (String s : coountryMapIn.keySet()) {
            HashSet<String> cSet = coountryMapIn.get(s);
            if (cSet == null)
                continue;
            ArrayList<String> subCountries = new ArrayList<>(cSet);
            Collections.sort(subCountries);
            countryData.put(s, subCountries);
            allCountries.addAll(subCountries);
            //Log.d(TAG, "setUpSources: " + topicsData);
        }
        Collections.sort(allCountries);
        countryData.put("All", allCountries);
        ArrayList<String> tempCountryList = new ArrayList<>(countryData.keySet());
        Collections.sort(tempCountryList);
        SubMenu countryMenu = menu.addSubMenu(R.string.countries);
        for (String s : tempCountryList)
            countryMenu.add(s);


        for (String s : languageMapIn.keySet()) {
            HashSet<String> lSet = languageMapIn.get(s);
            if (lSet == null)
                continue;
            ArrayList<String> subLanguages = new ArrayList<>(lSet);
            Collections.sort(subLanguages);
            languageData.put(s, subLanguages);
            allLanguages.addAll(subLanguages);
            //Log.d(TAG, "setUpSources: " + topicsData);
        }
        Collections.sort(allLanguages);
        languageData.put("All", allLanguages);
        ArrayList<String> tempLanguageList = new ArrayList<>(languageData.keySet());
        Collections.sort(tempLanguageList);
        SubMenu languageMenu = menu.addSubMenu(R.string.languages);
        for (String s : tempLanguageList)
            languageMenu.add(s);


        newsOutletsDisplayed.addAll(allNewsOutlets);
        Collections.sort(newsOutletsDisplayed);
        setTitle(String.format(Locale.getDefault(),"News Gateway (%d)", newsOutletsDisplayed.size() ));
        listView.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_item, newsOutletsDisplayed));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
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
        String title = item.getTitle().toString();
        if(title.equals(stringTopics) || title.equals(stringCountries) || title.equals(stringLanguages))
            prev = title;


        if(topicsData.containsKey(title) && prev.equals(stringTopics)) {
            topic = title;
//            newsOutletsDisplayed.clear();
//            ArrayList<String> lst = topicsData.get(item.getTitle().toString());
//            if (lst != null) {
//                newsOutletsDisplayed.addAll(lst);
//            }
//            setTitle(String.format(Locale.getDefault(),"News Gateway (%d)", newsOutletsDisplayed.size() ));
//
//            ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
        }

        if(languageData.containsKey(title) && prev.equals(stringLanguages)) {
            language = title;
//            newsOutletsDisplayed.clear();
//            ArrayList<String> lst = languageData.get(item.getTitle().toString());
//            if (lst != null) {
//                newsOutletsDisplayed.addAll(lst);
//            }
//            setTitle(String.format(Locale.getDefault(),"News Gateway (%d)", newsOutletsDisplayed.size() ));
//
//            ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
        }
        if(countryData.containsKey(title) && prev.equals(stringCountries)) {
            country = title;
//            newsOutletsDisplayed.clear();
//            ArrayList<String> lst = countryData.get(item.getTitle().toString());
//            if (lst != null) {
//                newsOutletsDisplayed.addAll(lst);
//            }
//            setTitle(String.format(Locale.getDefault(),"News Gateway (%d)", newsOutletsDisplayed.size() ));
//
//            ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
        }
        prev = title;
        newsOutletsDisplayed.clear();
        List<List<String>> lists = new ArrayList<List<String>>();
        ArrayList<String> tLst = topicsData.get(topic);
        lists.add(tLst);
        ArrayList<String> lLst = languageData.get(language);
        lists.add(lLst);
        ArrayList<String> cLst = countryData.get(country);
        lists.add(cLst);
        newsOutletsDisplayed.addAll(getCommonElements(lists));
        Log.d(TAG, "onOptionsItemSelected: " + getCommonElements(lists));

        setTitle(String.format(Locale.getDefault(),"News Gateway (%d)", newsOutletsDisplayed.size() ));

        ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }

    public static <T> Set<T> getCommonElements(Collection<? extends Collection<T>> collections) {
        Set<T> common = new LinkedHashSet<T>();
        if (!collections.isEmpty()) {
            Iterator<? extends Collection<T>> iterator = collections.iterator();
            common.addAll(iterator.next());
            while (iterator.hasNext()) {
                common.retainAll(iterator.next());
            }
        }
        return common;
    }
}