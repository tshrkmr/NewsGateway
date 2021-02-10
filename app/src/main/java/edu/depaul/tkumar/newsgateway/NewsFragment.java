package edu.depaul.tkumar.newsgateway;

import android.net.ParseException;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NewsFragment extends Fragment {
    private static final String TAG = "NewsFragment";

    public NewsFragment() {
        // Required empty public constructor
    }

    public static NewsFragment newInstance(NewsHeadline newsHeadline, int index, int max) {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle(1);
        args.putSerializable("News_DATA", newsHeadline);
        args.putSerializable("INDEX", index);
        args.putSerializable("Total_COUNT", max);
        fragment.setArguments(args);
        return fragment;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment_layout = inflater.inflate(R.layout.fragment_news, container, false);
        Bundle args = getArguments();
        if (args != null) {
            final NewsHeadline currentNewsHeadline = (NewsHeadline) args.getSerializable("News_DATA");
            if (currentNewsHeadline == null) {
                return null;
            }
            int index = args.getInt("INDEX");
            int total = args.getInt("Total_COUNT");

            TextView newsTitleTextView = fragment_layout.findViewById(R.id.fragmentNewsTitleTextView);
            TextView dateTextView = fragment_layout.findViewById(R.id.fragmentDateTextView);
            TextView authorTextView = fragment_layout.findViewById(R.id.fragmentAuthorTextView);
            TextView descriptionTextView = fragment_layout.findViewById(R.id.fragmentDescriptionTextView);
            TextView pageNumberTextView = fragment_layout.findViewById(R.id.fragmentPagenumberTextView);
            ImageView imageView = fragment_layout.findViewById(R.id.fragmentImageView);

            newsTitleTextView.setText(currentNewsHeadline.getTitle());

            String publishedAt = currentNewsHeadline.getPublishedAt();
            if (publishedAt.equals("") || publishedAt.equals("no value returned")) {
                dateTextView.setVisibility(View.INVISIBLE);
            } else {
//                DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
//                LocalDateTime dateTime = LocalDateTime.parse(publishedAt, formatter);

                LocalDateTime dateTime = convertDates(publishedAt);
                DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("MMM dd, y H:mm");
                String convertedDateTime = formatter1.format(dateTime);
                //Log.d(TAG, "onCreateView: " + convertedDateTime);
                dateTextView.setText(convertedDateTime);
            }
            String author = currentNewsHeadline.getAuthor();
            if (author.equals("") || author.equals("no value returned")) {
                authorTextView.setVisibility(View.INVISIBLE);
            } else {
                authorTextView.setText(currentNewsHeadline.getAuthor());
            }
            descriptionTextView.setText(currentNewsHeadline.getDescription());
            pageNumberTextView.setText(String.format(Locale.US, "%d of %d", index, total));
            String urlToImage = currentNewsHeadline.getUrlToImage();
            if (!urlToImage.equals("no value returned") && !urlToImage.equals("")) {
                Picasso.get().load(currentNewsHeadline.getUrlToImage())
                        //.resize(width, height)
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.brokenimage)
                        .into(imageView);
            }

            return fragment_layout;
        } else {
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private LocalDateTime convertDates(String unformattedDate) {
        ArrayList<DateTimeFormatter> knownFormats = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("y-MM-dd'T'H:mm:ss'Z'");
        knownFormats.add(formatter);
        DateTimeFormatter formatter1 = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        knownFormats.add(formatter1);

        for (DateTimeFormatter dtf : knownFormats) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(unformattedDate, dtf);
                return dateTime;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}