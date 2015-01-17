package com.sh4dov.carcosts.controllers.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.sh4dov.carcosts.R;
import com.sh4dov.carcosts.businesslogic.OverviewProvider;
import com.sh4dov.common.ViewHelper;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

public class TotalCostsAdapter extends ArrayAdapter<OverviewProvider.TotalCosts> {
    public TotalCostsAdapter(Context context, ArrayList<OverviewProvider.TotalCosts> totalCostsList) {
        super(context, 0, totalCostsList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OverviewProvider.TotalCosts totalCosts = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_overview, parent, false);
        }

        ViewHelper viewHelper = new ViewHelper(convertView);
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
        formatSymbols.setGroupingSeparator(' ');
        DecimalFormat decimalFormat = new DecimalFormat("###,##0.00", formatSymbols);

        viewHelper.setText(R.id.total_cost, decimalFormat.format(totalCosts.getTotal()) + " zł");
        viewHelper.setText(R.id.fuel_cost, decimalFormat.format(totalCosts.fuel) + " zł");
        viewHelper.setText(R.id.other_cost, decimalFormat.format(totalCosts.other) + " zł");
        viewHelper.setText(R.id.average_fuel_car, decimalFormat.format(totalCosts.averageFuelCar) + " l");
        viewHelper.setText(R.id.average_fuel_calculated, decimalFormat.format(totalCosts.averageFuelCalculation) + " l");
        viewHelper.setText(R.id.oil, decimalFormat.format(totalCosts.oil) + " l");
        viewHelper.setText(R.id.distance, decimalFormat.format(totalCosts.distance) + " km");
        viewHelper.setText(R.id.title, totalCosts.title != null ? totalCosts.title : getContext().getString(R.string.total));

        return convertView;
    }
}
