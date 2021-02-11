package edu.depaul.tkumar.newsgateway;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ListViewAdapter extends ArrayAdapter <String> {

    private final HashMap<String, ArrayList<String>> topicsData;
    private final HashMap<String, String> colorCodes;
    private final ArrayList<String> items;
    private final Context lContext;
    private final int id;
    private static final String TAG = "ListViewAdapter";

    public ListViewAdapter(Context context, int layoutId , ArrayList<String> lst, HashMap<String, ArrayList<String>> tData, HashMap<String, String> cCodes)
    {
        super(context, layoutId, lst);
        lContext = context;
        id = layoutId;
        items = lst ;
        topicsData = tData;
        colorCodes = cCodes;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent)
    {
        View view = v ;
        if(view == null){
            LayoutInflater layoutInflater = (LayoutInflater) lContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(id, null);
        }

        TextView text = (TextView) view.findViewById(R.id.drawerTextView);
        String item = items.get(position);

        for(String s: topicsData.keySet()){
            if(s.equals("All"))
                continue;
            ArrayList<String> list = topicsData.get(s);
            if(list != null && list.contains(item)){
                String color = colorCodes.get(s);
                //Log.d(TAG, "getView: " + color);
                text.setTextColor(Color.parseColor(color));
                text.setText(items.get(position));
            }
        }
        return view;
    }

}

