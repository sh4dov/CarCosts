package com.sh4dov.carcosts.controllers;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sh4dov.carcosts.R;
import com.sh4dov.carcosts.controllers.view.operators.CostViewOperator;
import com.sh4dov.carcosts.infrastructure.FragmentFactory;
import com.sh4dov.carcosts.infrastructure.FragmentOperator;
import com.sh4dov.carcosts.infrastructure.ToastNotificator;
import com.sh4dov.carcosts.model.Cost;
import com.sh4dov.carcosts.repositories.CostRepository;
import com.sh4dov.carcosts.repositories.DbHandler;
import com.sh4dov.common.ViewHelper;


public class AddCostFragment extends Fragment {
    private FragmentOperator fragmentOperator;

    public AddCostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_cost, container, false);
        ViewHelper viewHelper = new ViewHelper(view);
        Button addButton = viewHelper.get(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        new CostViewOperator(new ViewHelper(getView())).set(new Cost());

        super.onStart();
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

    private void add() {
        Cost cost = new CostViewOperator(new ViewHelper(getView())).get(null);

        if (cost.isValid()) {
            Activity activity = getActivity();
            CostRepository costRepository = new CostRepository(new DbHandler(activity), new ToastNotificator(activity));
            costRepository.add(cost);
            fragmentOperator.goToFragment(FragmentFactory.FragmentPosition.CostsList);
            fragmentOperator.reload();
        }
    }
}
