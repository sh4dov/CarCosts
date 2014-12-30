package com.sh4dov.carcosts.controllers.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sh4dov.carcosts.R;
import com.sh4dov.carcosts.model.Fuel;
import com.sh4dov.common.ViewHelper;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by sh4dov on 2014-12-29.
 */
public class FuelsAdapter extends ArrayAdapter<Fuel> {
    public FuelsAdapter(Context context, ArrayList<Fuel> fuels) {
        super(context, 0, fuels);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Fuel fuel = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_fuel_item, parent, false);
        }

        ViewHelper viewHelper = new ViewHelper(convertView);
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
        formatSymbols.setGroupingSeparator(' ');
        DecimalFormat decimalFormat = new DecimalFormat("###,###.##", formatSymbols);

        TextView date = viewHelper.get(R.id.date);
        date.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(fuel.date));

        TextView mileage = viewHelper.get(R.id.mileage);
        mileage.setText(decimalFormat.format(fuel.mileage) + " km");

        TextView distance = viewHelper.get(R.id.distance);
        distance.setText(decimalFormat.format(fuel.distance) + " km");

        TextView liters = viewHelper.get(R.id.liters);
        liters.setText(decimalFormat.format(fuel.liters) + " l");

        TextView averageFuel = viewHelper.get(R.id.averageFuel);
        averageFuel.setText(decimalFormat.format(fuel.averageFuel) + " l");

        TextView cost = viewHelper.get(R.id.cost);
        cost.setText(decimalFormat.format(fuel.cost) + " zł");

        TextView literCost = viewHelper.get(R.id.literCost);
        literCost.setText(decimalFormat.format(fuel.literCost) + " zł");

        TextView fuelType = viewHelper.get(R.id.fuelType);
        fuelType.setText(fuel.fuelType);

        return convertView;
    }
}
