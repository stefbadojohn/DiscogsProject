package com.example.stefbadojohn.discogsproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class ResultsAdapter extends ArrayAdapter<DiscogsResult> {

    private Context mContext;
    private List<DiscogsResult> resultsList = new ArrayList<>();

    public ResultsAdapter(@NonNull Context context, int resource, @NonNull List<DiscogsResult> objects) {
        super(context, resource, objects);
        mContext = context;
        resultsList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);

        DiscogsResult currentResult = resultsList.get(position);

        ImageView imageViewThumb = listItem.findViewById(R.id.imageView_thumb);
        TextView textViewTitle = listItem.findViewById(R.id.textView_title);
        TextView textViewYear = listItem.findViewById(R.id.textView_year);
        TextView textViewStyle = listItem.findViewById(R.id.textView_style);


        List<String> styleList = currentResult.getStyle();
        String thumbUrl = currentResult.getThumbUrl();
        String year = null;
        String style = null;

        if (thumbUrl != null) NetworkUtils.loadImageByInternetUrl(thumbUrl, imageViewThumb);
        if (styleList.size() > 0) style = styleList.get(0);
        if (style != null && !style.equals("")) textViewStyle.setText(style);

        textViewTitle.setText(currentResult.getTitle());
        textViewYear.setText(currentResult.getYear());

        return listItem;
    }


}
