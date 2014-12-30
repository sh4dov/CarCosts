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

public class AddFuelFragment extends Fragment {
    private FuelRepository fuelRepository;
    private ListenerList<AddedListener> addedListeners = new ListenerList<AddedListener>();

    public void addAddedListener(AddedListener listener) {
        addedListeners.add(listener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_fuel, container, false);
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

        new FuelViewOperator(new ViewHelper(getView())).set(fuel);

        super.onStart();
    }


    public void setFuelRepository(FuelRepository fuelRepository) {
        this.fuelRepository = fuelRepository;
    }

    private void add() {
        Fuel fuel = new FuelViewOperator(new ViewHelper(getView())).get(null);

        if (fuel.isValid()) {
            fuelRepository.add(fuel);
            addedListeners.fireEvent(new ListenerList.FireHandler<AddedListener>() {
                @Override
                public void fireEvent(AddedListener listener) {
                    listener.added();
                }
            });
        }
    }

    public interface AddedListener {
        void added();
    }
}

