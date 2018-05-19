package com.example.android.afterguardian;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ArticleAdapter extends ArrayAdapter {

    ArticleAdapter(Context context, List<Article> articles) {
        super(context, 0, articles);
    }

    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat SIMPLEDATE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.article_list_item, parent, false);
        }

        Article currentArticle = (Article) getItem(position);

        // Category
        ImageView iconView = listItemView.findViewById(R.id.icon_category);
        String icon = currentArticle.getPillarName();
        int imgId = getContext().getResources().getIdentifier("drawable/ic_" + icon, "id", getContext().getPackageName());
        iconView.setImageResource(imgId);
        iconView.setContentDescription(icon);

        // Title
        TextView titleView = listItemView.findViewById(R.id.title);

        titleView.setText("("+ currentArticle.getAuthor()+") " + currentArticle.getTitleArticle());
        titleView.setLines(2);

        // Publication Date/time
        TextView dateView = listItemView.findViewById(R.id.date);
        dateView.setText(formatDate(currentArticle.getPublicationDate(), "date"));

        TextView timeView = listItemView.findViewById(R.id.time);
        timeView.setText(formatDate(currentArticle.getPublicationDate(), "time"));

        return listItemView;
    }

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") or (i.e. "3:06PM") from a Date object.
     */
    @SuppressLint("SimpleDateFormat")
    private String formatDate(String date, String format) {
        Date dateFormat = null;
        String pattern = null;

        if (format == "date") {
            pattern = "LLL dd, yyyy";
        } else if (format == "time" || format == null) {
            pattern = "h:mm a";
        }

        try {
            dateFormat = SIMPLEDATE.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new SimpleDateFormat(pattern).format(dateFormat);
    }

}