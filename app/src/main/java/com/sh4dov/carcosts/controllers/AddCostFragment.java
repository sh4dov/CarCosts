package com.sh4dov.carcosts.controllers;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sh4dov.carcosts.R;
import com.sh4dov.carcosts.controllers.view.operators.CostViewOperator;
import com.sh4dov.carcosts.model.Cost;
import com.sh4dov.carcosts.repositories.CostRepository;
import com.sh4dov.common.ListenerList;
import com.sh4dov.common.ViewHelper;


public class AddCostFragment extends Fragment {
    private ListenerList<AddedListener> addedListeners = new ListenerList<AddedListener>();
    private CostRepository costRepository;

    public AddCostFragment() {
        // Required empty public constructor
    }

    public void addAddedListeners(AddedListener addedListeners) {
        this.addedListeners.add(addedListeners);
    }

    public void setCostRepository(CostRepository costRepository) {
        this.costRepository = costRepository;
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

    private void add() {
        Cost cost = new CostViewOperator(new ViewHelper(getView())).get(null);

        if (cost.isValid()) {
            costRepository.add(cost);
            addedListeners.fireEvent(new ListenerList.FireHandler<AddedListener>() {
                @Override
                public void fireEvent(AddedListener listener) {
                    listener.Added();
                }
            });
        }
    }

    public interface AddedListener {
        void Added();
    }
}
