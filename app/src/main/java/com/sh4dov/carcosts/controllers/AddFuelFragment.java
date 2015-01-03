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
    private FuelRepository fuelRepository;
    private FragmentOperator fragmentOperator;

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
        return view;
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
    public void onDetach() {
        super.onDetach();
        fragmentOperator = null;
    }

    @Override
    public void onStart() {
        Fuel fuel = fuelRepository.getLastFuel();

        new FuelViewOperator(new ViewHelper(getView())).set(fuel);

        super.onStart();
    }

    private void add() {
        Fuel fuel = new FuelViewOperator(new ViewHelper(getView())).get(null);

        if (fuel.isValid()) {
            fuelRepository.add(fuel);
            fragmentOperator.goToFragment(FragmentFactory.FragmentPosition.RefuelingList);
            fragmentOperator.reload();
        }
    }
}

