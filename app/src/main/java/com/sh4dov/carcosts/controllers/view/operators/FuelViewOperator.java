package com.sh4dov.carcosts.controllers.view.operators;

import android.widget.NumberPicker;

import com.sh4dov.carcosts.R;
import com.sh4dov.carcosts.model.Fuel;
import com.sh4dov.common.ViewHelper;
import com.sh4dov.common.ViewOperator;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class FuelViewOperator extends ViewOperator<Fuel> {

    public FuelViewOperator(ViewHelper viewHelper) {
        super(viewHelper);
    }

    @Override
    public void set(Fuel fuel) {
        setMileage(fuel.mileage, fuel.mileage + (int) fuel.distance);
        setTwoDigitNumbers(fuel.liters, R.id.liters1, R.id.liters2, 100);
        setTwoDigitNumbers(fuel.cost, R.id.cost1, R.id.cost2, 1000);
        setTwoDigitNumbers(fuel.literCost, R.id.literCost1, R.id.literCost2, 10);
        setOneDigitNumbers(fuel.averageFuel, R.id.averageFuel1, R.id.averageFuel2, 100);
        setOneDigitNumbers(fuel.distance, R.id.distance1, R.id.distance2, 9999);
        setText(fuel.fuelType, R.id.fuelType);
    }

    @Override
    public Fuel get(Fuel instance) {
        Fuel fuel = instance != null ? instance : new Fuel();

        fuel.date = viewHelper.getDate(R.id.datePicker);

        NumberPicker mileage = viewHelper.get(R.id.mileage);
        fuel.mileage = mileage.getValue();

        fuel.liters = getNumber(R.id.liters1, R.id.liters2, 100);
        fuel.literCost = getNumber(R.id.literCost1, R.id.literCost2, 100);
        fuel.cost = getNumber(R.id.cost1, R.id.cost2, 100);
        fuel.averageFuel = getNumber(R.id.averageFuel1, R.id.averageFuel2, 10);
        fuel.distance = getNumber(R.id.distance1, R.id.distance2, 10);
        fuel.fuelType = viewHelper.getText(R.id.fuelType);

        return fuel;
    }

    private void setMileage(int min, int value) {
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
        formatSymbols.setGroupingSeparator(' ');
        final DecimalFormat decimalFormat = new DecimalFormat("###,###.##", formatSymbols);

        NumberPicker numberPicker = viewHelper.get(R.id.mileage);
        numberPicker.setMinValue(min);
        numberPicker.setMaxValue(Fuel.MAX_MILEAGE);
        numberPicker.setValue(value);
        numberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return decimalFormat.format(i);
            }
        });
    }

}
