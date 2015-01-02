package com.sh4dov.carcosts.controllers.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.sh4dov.carcosts.R;
import com.sh4dov.carcosts.model.Cost;
import com.sh4dov.common.ViewHelper;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by sh4dov on 2014-12-30.
 */
public class CostAdapter extends ArrayAdapter<Cost> {
    public CostAdapter(Context context, ArrayList<Cost> costs) {
        super(context, 0, costs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Cost cost = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_cost_item, parent, false);
        }

        ViewHelper viewHelper = new ViewHelper(convertView);
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
        formatSymbols.setGroupingSeparator(' ');
        DecimalFormat decimalFormat = new DecimalFormat("###,###.00", formatSymbols);

        viewHelper.setText(R.id.date, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(cost.date));
        viewHelper.setText(R.id.cost, decimalFormat.format(cost.cost));
        viewHelper.setText(R.id.comment, cost.comment);

        return convertView;
    }
}
