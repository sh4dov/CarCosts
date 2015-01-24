package com.sh4dov.carcosts.controllers;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sh4dov.carcosts.R;
import com.sh4dov.carcosts.controllers.view.operators.OilViewOperator;
import com.sh4dov.carcosts.infrastructure.FragmentFactory;
import com.sh4dov.carcosts.infrastructure.FragmentOperator;
import com.sh4dov.carcosts.infrastructure.ToastNotificator;
import com.sh4dov.carcosts.model.Oil;
import com.sh4dov.carcosts.repositories.DbHandler;
import com.sh4dov.carcosts.repositories.OilRepository;
import com.sh4dov.common.ViewHelper;

public class AddOilFragment extends Fragment {
    private static final String SAVED_OIL_KEY = "SavedOil";
    private FragmentOperator fragmentOperator;
    private boolean saveState = true;

    public AddOilFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Oil oil = new Oil();
        if (savedInstanceState != null) {
            oil = (Oil) savedInstanceState.getSerializable(SAVED_OIL_KEY);
        }

        if (oil == null) {
            oil = new Oil();
        }

        new OilViewOperator(new ViewHelper(getView())).set(oil);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_oil, container, false);
        ViewHelper viewHelper = new ViewHelper(view);
        Button addButton = viewHelper.get(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
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
            Oil oil = new OilViewOperator(new ViewHelper(getView())).get(null);
            outState.putSerializable(SAVED_OIL_KEY, oil);
        } else {
            outState.remove(SAVED_OIL_KEY);
        }
    }

    private void add() {
        Oil oil = new OilViewOperator(new ViewHelper(getView())).get(null);

        if (oil.isValid()) {
            saveState = false;
            Activity activity = getActivity();
            OilRepository oilRepository = new OilRepository(new DbHandler(activity), new ToastNotificator(activity));
            oilRepository.add(oil);
            fragmentOperator.goToFragment(FragmentFactory.FragmentPosition.OilList);
            fragmentOperator.reload();
        }
    }
}
