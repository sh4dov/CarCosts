package com.sh4dov.carcosts.controllers;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.sh4dov.carcosts.R;
import com.sh4dov.carcosts.model.Fuel;
import com.sh4dov.carcosts.repositories.FuelRepository;
import com.sh4dov.common.ListenerList;
import com.sh4dov.common.ViewHelper;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;

public class AddRefuelingFragment extends Fragment {
    private FuelRepository fuelRepository;
    private ListenerList<AddedListener> addedListeners = new ListenerList<AddedListener>();

    public interface AddedListener{
        void added();
    }

    public void addAddedListener(AddedListener listener) {addedListeners.add(listener);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_refueling, container, false);
        ViewHelper viewHelper = new ViewHelper(view);
        Button button = viewHelper.get(R.id.addButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        Fuel fuel = fuelRepository.getLastFuel();

        ViewHelper viewHelper = new ViewHelper(getView());
        NumberPicker.Formatter formatter = new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        };

        setMileage(viewHelper, fuel.mileage, fuel.mileage + (int) fuel.distance);
        setTwoDigitNumbers(viewHelper, formatter, fuel.liters, R.id.liters1, R.id.liters2, 100);
        setTwoDigitNumbers(viewHelper, formatter, fuel.literCost, R.id.literCost1, R.id.literCost2, 10);
        setTwoDigitNumbers(viewHelper, formatter, fuel.cost, R.id.cost1, R.id.cost2, 1000);
        setOneDigitNumbers(viewHelper, fuel.averageFuel, R.id.averageFuel1, R.id.averageFuel2, 100);
        setOneDigitNumbers(viewHelper, fuel.distance, R.id.distance1, R.id.distance2, 9999);
        setFuelType(viewHelper, fuel.fuelType);

        super.onStart();
    }

    public void setFuelRepository(FuelRepository fuelRepository) {
        this.fuelRepository = fuelRepository;
    }

    private void add() {
        Fuel fuel = new Fuel();
        ViewHelper viewHelper = new ViewHelper(getView());

        DatePicker date = viewHelper.get(R.id.datePicker);
        Calendar calendar = Calendar.getInstance();
        calendar.set(date.getYear(), date.getMonth(), date.getDayOfMonth());
        fuel.date = calendar.getTime();

        NumberPicker mileage = viewHelper.get(R.id.mileage);
        fuel.mileage = mileage.getValue();

        fuel.liters = getNumber(viewHelper, R.id.liters1, R.id.liters2, 100);
        fuel.literCost = getNumber(viewHelper, R.id.literCost1, R.id.literCost2, 100);
        fuel.cost = getNumber(viewHelper, R.id.cost1, R.id.cost2, 100);
        fuel.averageFuel = getNumber(viewHelper, R.id.averageFuel1, R.id.averageFuel2, 10);
        fuel.distance = getNumber(viewHelper, R.id.distance1, R.id.distance2, 10);
        EditText fuelType = viewHelper.get(R.id.fuelType);
        fuel.fuelType = fuelType.getText().toString();

        if(fuel.isValid()){
            fuelRepository.add(fuel);
            addedListeners.fireEvent(new ListenerList.FireHandler<AddedListener>() {
                @Override
                public void fireEvent(AddedListener listener) {
                    listener.added();
                }
            });
        }
    }

    private double getNumber(ViewHelper viewHelper, int idPart1, int idPart2, int divider){
        NumberPicker part1 = viewHelper.get(idPart1);
        NumberPicker part2 = viewHelper.get(idPart2);
        return part1.getValue() + ((double)part2.getValue() / divider);
    }

    private void setFuelType(ViewHelper viewHelper, String fuelType) {
        EditText editText = viewHelper.get(R.id.fuelType);
        editText.setText(fuelType);
    }

    private void setOneDigitNumbers(ViewHelper viewHelper, double originalValue, int idPar1, int idPart2, int maxValue) {
        NumberPicker numberPicker1 = viewHelper.get(idPar1);
        NumberPicker numberPicker2 = viewHelper.get(idPart2);
        int part1 = (int) Math.floor(originalValue);
        int part2 = (int) Math.floor((originalValue * 10) - (part1 * 10));
        numberPicker1.setMinValue(0);
        numberPicker2.setMinValue(0);
        numberPicker1.setMaxValue(maxValue);
        numberPicker2.setMaxValue(9);
        numberPicker1.setValue(part1);
        numberPicker2.setValue(part2);
    }

    private void setTwoDigitNumbers(ViewHelper viewHelper, NumberPicker.Formatter formatter, double originalValue, int idPart1, int idPart2, int maxValue) {
        NumberPicker numberPicker1 = viewHelper.get(idPart1);
        NumberPicker numberPicker2 = viewHelper.get(idPart2);
        int part1 = (int) Math.floor(originalValue);
        int part2 = (int) Math.floor((originalValue * 100) - (part1 * 100));
        numberPicker1.setMinValue(0);
        numberPicker2.setMinValue(0);
        numberPicker1.setMaxValue(maxValue);
        numberPicker2.setMaxValue(99);
        numberPicker2.setFormatter(formatter);
        numberPicker1.setValue(part1);
        numberPicker2.setValue(part2);
    }

    private void setMileage(ViewHelper viewHelper, int min, int value) {
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
        formatSymbols.setGroupingSeparator(' ');
        final DecimalFormat decimalFormat = new DecimalFormat("###,###.##", formatSymbols);

        NumberPicker numberPicker = viewHelper.get(R.id.mileage);
        numberPicker.setMinValue(min);
        numberPicker.setMaxValue(1000000);
        numberPicker.setValue(value);
        numberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return decimalFormat.format(i);
            }
        });
    }
}

