package com.example.marc4492.neuralmath;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


class AdapterHome extends ArrayAdapter<HomeRow> {

    AdapterHome(Context context, int resource, ArrayList<HomeRow> row) {
        super(context, resource, row);
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.menu_elements_layout, parent, false);

        //setting the height so that the screen is filled regardless of the number of item in the listView
        int viewSize = parent.getHeight() / (getCount() + 1);
        convertView.getLayoutParams().height = viewSize;

        HomeRow hRow = getItem(position);

        if (hRow != null) {
            ImageView image = (ImageView) convertView.findViewById(R.id.elementPicture);
            TextView text = (TextView) convertView.findViewById(R.id.elementText);

            image.setImageResource(hRow.getImage());
            text.setText(hRow.getText());
            text.setTextSize(viewSize / 10);
        }

        return convertView;
    }
}