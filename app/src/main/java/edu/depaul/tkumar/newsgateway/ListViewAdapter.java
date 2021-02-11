package edu.depaul.tkumar.newsgateway;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ListViewAdapter extends ArrayAdapter <String> {

    private final Context mContext;
    private final int id;
    private final ArrayList<String> items;
    private final HashMap<String, ArrayList<String>> topicsData;
    private final HashMap<String, String> colorCodes;
    private static final String TAG = "ListViewAdapter";

    public ListViewAdapter(Context context, int textViewResourceId , ArrayList<String> lst, HashMap<String, ArrayList<String>> tData, HashMap<String, String> cCodes)
    {
        super(context, textViewResourceId, lst);
        mContext = context;
        id = textViewResourceId;
        items = lst ;
        topicsData = tData;
        colorCodes = cCodes;
    }

//    public void addFilters(HashMap<String, ArrayList<String>> tData, HashMap<String, String> cCodes){
//        topicsData.clear();
//        colorCodes.clear();
//        topicsData = tData;
//        colorCodes = cCodes;
//    }

    @Override
    public View getView(int position, View v, ViewGroup parent)
    {
        View mView = v ;
        if(mView == null){
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView = vi.inflate(id, null);
        }

        TextView text = (TextView) mView.findViewById(R.id.drawerTextView);
        String item = items.get(position);

        for(String s: topicsData.keySet()){
            if(s.equals("All"))
                continue;
            ArrayList<String> list = topicsData.get(s);
            if(list.contains(item)){
                String color = colorCodes.get(s);
                //Log.d(TAG, "getView: " + color);
                text.setTextColor(Color.parseColor(color));
                text.setText(items.get(position));
            }
        }
        return mView;
    }

}

