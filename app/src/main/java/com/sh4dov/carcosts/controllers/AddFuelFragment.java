package com.sh4dov.carcosts.controllers;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sh4dov.carcosts.R;
import com.sh4dov.carcosts.controllers.view.operators.FuelViewOperator;
import com.sh4dov.carcosts.infrastructure.FragmentFactory;
import com.sh4dov.carcosts.infrastructure.FragmentOperator;
import com.sh4dov.carcosts.infrastructure.ToastNotificator;
import com.sh4dov.carcosts.model.Fuel;
import com.sh4dov.carcosts.repositories.DbHandler;
import com.sh4dov.carcosts.repositories.FuelRepository;
import com.sh4dov.common.ViewHelper;

public class AddFuelFragment extends Fragment {
    public static final String SAVED_FUEL_KEY = "SavedFuel";
    private Fuel defaultFuel;
    private FragmentOperator fragmentOperator;
    private FuelRepository fuelRepository;
    private boolean saveState = true;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Fuel fuel = null;
        if (savedInstanceState != null) {
            fuel = (Fuel) savedInstanceState.getSerializable(SAVED_FUEL_KEY);
        }

        defaultFuel = fuelRepository.getLastFuel();
        if (fuel == null) {
            fuel = defaultFuel;
        }

        FuelViewOperator operator = new FuelViewOperator(new ViewHelper(getView()));
        operator.setMileageMinMax(defaultFuel.mileage, Fuel.MAX_MILEAGE);
        operator.set(fuel);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            fragmentOperator = (FragmentOperator) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " + FragmentOperator.class.getName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_fuel, container, false);

        Activity activity = getActivity();
        fuelRepository = new FuelRepository(new DbHandler(activity), new ToastNotificator(activity));
        ViewHelper viewHelper = new ViewHelper(view);
        Button button = viewHelper.get(R.id.add_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
            }
        });
        saveState = true;
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentOperator = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (saveState) {
            Fuel fuel = new FuelViewOperator(new ViewHelper(getView())).get(null);
            if (fuel != null && defaultFuel != null && fuel.valuesEquals(defaultFuel)) {
                outState.remove(SAVED_FUEL_KEY);
                return;
            }
            outState.putSerializable(SAVED_FUEL_KEY, fuel);
        } else {
            outState.remove(SAVED_FUEL_KEY);
        }
    }

    private void add() {
        Fuel fuel = new FuelViewOperator(new ViewHelper(getView())).get(null);

        if (fuel.isValid()) {
            saveState = false;
            fuelRepository.add(fuel);
            fragmentOperator.goToFragment(FragmentFactory.FragmentPosition.RefuelingList);
            fragmentOperator.reload();
        }
    }
}

