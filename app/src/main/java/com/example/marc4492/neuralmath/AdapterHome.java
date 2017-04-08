package com.example.marc4492.neuralmath;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class AdapterHome extends ArrayAdapter<HomeRow> {


    public AdapterHome(Context context, int resource, ArrayList<HomeRow> row) {
        super(context, resource, row);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.menu_elements_layout, parent, false);
        }

        //setting the height so that the screen is filled regardless of the number of item in the listView
        int viewsize = parent.getHeight() / (getCount() + 1);
        convertView.getLayoutParams().height = viewsize;

        HomeRow hRow = getItem(position);

        if (hRow != null) {
            ImageView image = (ImageView) convertView.findViewById(R.id.elementPicture);
            TextView text = (TextView) convertView.findViewById(R.id.elementText);

            image.setImageResource(hRow.getImage());
            text.setText(hRow.getText());
            text.setTextSize(viewsize / 10);
        }

        return convertView;
    }
}