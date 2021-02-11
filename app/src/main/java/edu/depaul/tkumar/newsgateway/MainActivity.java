package edu.depaul.tkumar.newsgateway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final HashMap<String, ArrayList<String>> topicsData = new HashMap<>();
    private final HashMap<String, ArrayList<String>> languageData = new HashMap<>();
    private final HashMap<String, ArrayList<String>> countryData = new HashMap<>();
    private final HashMap<String, String> colorCodes = new HashMap<>();
    private final HashMap<String, String> nameIDMap = new HashMap<>();
    private final ArrayList<String> newsSourcesDisplayed = new ArrayList<>();
    private final ArrayList<String> allNewsOutlets =new ArrayList<>();
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private List<Fragment> fragments;
    private ProgressBar progressBar;
    private ListView listView;
    private MyPageAdapter pageAdapter;
    private ViewPager pager;
    private Menu menu;
    private String currentNewsSource;
    private String prev;
    private final String stringTopics = "Topics";
    private final String stringCountries = "Countries";
    private final String stringLanguages = "Languages";
    private String topic = "All";
    private String language = "All";
    private String country = "All";
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeLayout();
        if(topicsData.isEmpty())
            progressBar.setVisibility(View.VISIBLE);
            new Thread(new SourceDownloaderRunnable(this)).start();
    }

    private void initializeLayout(){
        drawerLayout = findViewById(R.id.mainDrawerlayout);
        listView = findViewById(R.id.mainDrawerList);
        pager = findViewById(R.id.mainViewpager);
        progressBar = findViewById(R.id.mainProgressBar);

        // Set up the drawer item click callback method
        listView.setOnItemClickListener(
                (parent, view, position, id) -> {
                    selectItem(position);
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

        fragments = new ArrayList<>();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        pager.setAdapter(pageAdapter);
    }

    // You need this to set up the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        return true;
    }

    public void setUpColorMap(String category, String color) {
        if (!colorCodes.containsKey(category.toUpperCase()))
            colorCodes.put(category.toUpperCase(), color);
    }

    public void updateIdNameMap(String id, String name){
        allNewsOutlets.add(name);
        nameIDMap.put(name, id);
    }

    public void setUpSources(HashMap<String, HashSet<String>> topicMapIn, HashMap<String, HashSet<String>> languageMapIn, HashMap<String, HashSet<String>> countryMapIn){
        topicsData.clear();
        languageData.clear();
        countryData.clear();
        newsSourcesDisplayed.clear();
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
            //Log.d(TAG, "setUpSources: " + s + " " +  subTopics);
        }
        Collections.sort(allTopics);
        topicsData.put("All", allTopics);
        ArrayList<String> tempTopicList = new ArrayList<>(topicsData.keySet());
        Collections.sort(tempTopicList);
        SubMenu topicsMenu = menu.addSubMenu(stringTopics);
        int i = 0;
        for (String s : tempTopicList) {
            topicsMenu.add(s);
            if(i==0){
                i++;
                continue;
            }
            final MenuItem menuItem = topicsMenu.getItem(i);
            i++;
            SpannableString spannableString = new SpannableString(menuItem.getTitle().toString());
            String color = colorCodes.get(spannableString.toString());
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor(color)), 0, spannableString.length(), 0);
            menuItem.setTitle(spannableString);
        }


        for (String s : countryMapIn.keySet()) {
            HashSet<String> cSet = countryMapIn.get(s);
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
        SubMenu countryMenu = menu.addSubMenu(stringCountries);
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
        SubMenu languageMenu = menu.addSubMenu(stringLanguages);
        for (String s : tempLanguageList)
            languageMenu.add(s);

        newsSourcesDisplayed.addAll(allNewsOutlets);
        Collections.sort(newsSourcesDisplayed);
        setTitle(String.format(Locale.getDefault(),"News Gateway (%d)", newsSourcesDisplayed.size() ));
        //listView.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_item, newsSourcesDisplayed));

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.drawer_item,R.id.drawerTextView, newsSourcesDisplayed){
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent){
//                View view = super.getView(position, convertView, parent);
//                TextView ListItemShow = (TextView) view.findViewById(R.id.drawerTextView);
//                for(int i=0;i<newsSourcesDisplayed.size();i++){
//
//                }
//                ListItemShow.setTextColor(Color.parseColor("#fe00fb"));
//                return view;
//            }
//        };

        progressBar.setVisibility(View.GONE);

        ArrayAdapter listAdapter = new ListViewAdapter(this , R.layout.drawer_item , newsSourcesDisplayed, topicsData, colorCodes);
        listView.setAdapter(listAdapter);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
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

        if(title.equals(stringTopics) || title.equals(stringCountries) || title.equals(stringLanguages)) {
            prev = title;
            return true;
        }

        if(topicsData.containsKey(title) && prev.equals(stringTopics)) {
            topic = title;
        }
        if(countryData.containsKey(title) && prev.equals(stringCountries)) {
            country = title;
        }
        if(languageData.containsKey(title) && prev.equals(stringLanguages)) {
            language = title;
        }

        ArrayList<String> finalList = new ArrayList<>();
        newsSourcesDisplayed.clear();
        //List<List<String>> lists = new ArrayList<>();
        ArrayList<String> tLst = topicsData.get(topic);
        //lists.add(tLst);
        ArrayList<String> lLst = languageData.get(language);
        //lists.add(lLst);
        ArrayList<String> cLst = countryData.get(country);
        //lists.add(cLst);

        if(tLst != null)
            finalList.addAll(tLst);
        if(lLst != null)
            finalList.retainAll(lLst);
        if(cLst != null)
            finalList.retainAll(cLst);

        newsSourcesDisplayed.addAll(finalList);
        if(newsSourcesDisplayed.size() == 0){
            noNewsSourceDialog(topic, country, language);
        }
        setTitle(String.format(Locale.getDefault(),"News Gateway (%d)", newsSourcesDisplayed.size() ));
        ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }

//    public static <T> Set<T> getCommonElements(Collection<? extends Collection<T>> collections) {
//        Set<T> common = new LinkedHashSet<T>();
//        if (!collections.isEmpty()) {
//            Iterator<? extends Collection<T>> iterator = collections.iterator();
//            common.addAll(iterator.next());
//            while (iterator.hasNext()) {
//                common.retainAll(iterator.next());
//            }
//        }
//        return common;
//    }

    private void noNewsSourceDialog(String topic, String country, String language){
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View view = layoutInflater.inflate(R.layout.dialog_no_news_sources, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Sources");
        builder.setMessage("No News Sources match your criteria:");
        builder.setView(view);
        TextView topicTextview = view.findViewById(R.id.dialogTopicTextView);
        TextView countryTextview = view.findViewById(R.id.dialogCountryTextView);
        TextView languageTextView = view.findViewById(R.id.dialogLanguageTextView);
        topicTextview.setText(String.format("Topic: %s", topic));
        countryTextview.setText(String.format("Country: %s", country));
        languageTextView.setText(String.format("Language: %s", language));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void selectItem(int position) {
        pager.setBackground(null);
        currentNewsSource = newsSourcesDisplayed.get(position);
        progressBar.setVisibility(View.VISIBLE);
        new Thread(new NewsDownloaderRunnable(this, nameIDMap.get(currentNewsSource))).start();
        drawerLayout.closeDrawer(listView);
    }

    public void setTopHeadlines(ArrayList<NewsHeadline> newsHeadlineArrayList){
        progressBar.setVisibility(View.GONE);
        setTitle(currentNewsSource);

        for (int i = 0; i < pageAdapter.getCount(); i++)
            pageAdapter.notifyChangeInPosition(i);
        fragments.clear();

        for (int i = 0; i < newsHeadlineArrayList.size(); i++) {
            fragments.add(
                    NewsFragment.newInstance(newsHeadlineArrayList.get(i), i+1, newsHeadlineArrayList.size()));
        }

        pageAdapter.notifyDataSetChanged();
        pager.setCurrentItem(0);
    }

    public void showError(String issue){
        progressBar.setVisibility(View.GONE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Data Download Failed");
        builder.setMessage(issue);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //////////////////////////////////////////////////////////////////////////////
     //Standard adapter code here
    private class MyPageAdapter extends FragmentPagerAdapter {
        private long baseId = 0;


        MyPageAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            // give an ID different from position when position has been changed
            return baseId + position;
        }

        /**
         * Notify that the position of a fragment has been changed.
         * Create a new ID for each position to force recreation of the fragment
         * @param n number of items which have been changed
         */
        void notifyChangeInPosition(int n) {
            // shift the ID returned by getItemId outside the range of all previous fragments
            baseId += getCount() + n;
        }
    }
}