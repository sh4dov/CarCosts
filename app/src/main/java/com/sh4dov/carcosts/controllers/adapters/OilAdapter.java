package com.sh4dov.carcosts.controllers.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.sh4dov.carcosts.R;
import com.sh4dov.carcosts.model.Oil;
import com.sh4dov.common.ViewHelper;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by sh4dov on 2014-12-30.
 */
public class OilAdapter extends ArrayAdapter<Oil> {
    public OilAdapter(Context context, ArrayList<Oil> oil) {
        super(context, 0, oil);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Oil oil = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_oil_item, parent, false);
        }

        ViewHelper viewHelper = new ViewHelper(convertView);
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
        formatSymbols.setGroupingSeparator(' ');
        DecimalFormat decimalFormat = new DecimalFormat("###,###.##", formatSymbols);

        viewHelper.setText(R.id.date, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(oil.date));
        viewHelper.setText(R.id.liters, decimalFormat.format(oil.liters));
        viewHelper.setText(R.id.comment, oil.comment);

        return convertView;
    }
}
