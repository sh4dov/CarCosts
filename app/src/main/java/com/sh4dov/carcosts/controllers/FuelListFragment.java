package com.sh4dov.carcosts.controllers;

import android.app.Activity;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.sh4dov.carcosts.R;
import com.sh4dov.carcosts.adapters.FuelsAdapter;
import com.sh4dov.carcosts.model.Fuel;
import com.sh4dov.carcosts.repositories.FuelRepository;
import com.sh4dov.common.ProgressIndicator;
import com.sh4dov.common.ProgressPointerIndicator;
import com.sh4dov.common.ViewHelper;

import java.util.ArrayList;


public class FuelListFragment extends ListFragment {
    private ArrayList<Fuel> fuels = new ArrayList<Fuel>();
    private FuelRepository fuelRepository;
    private FuelsAdapter fuelsAdapter;
    private EditFuelListener listener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FuelListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fuel_list, container, false);

        final Activity activity = getActivity();
        final ProgressPointerIndicator progressPointer = new ProgressPointerIndicator();
        ProgressIndicator progressIndicator = new ProgressIndicator(activity, ProgressDialog.STYLE_HORIZONTAL, new Runnable() {
            @Override
            public void run() {
                ArrayList<Fuel> refueling = fuelRepository.getRefueling(progressPointer);
                fuels.clear();
                fuels.addAll(refueling);
                fuelsAdapter.notifyDataSetChanged();
            }
        });
        progressPointer.setProgressPointer(progressIndicator);
        progressIndicator.execute();
        fuelsAdapter = new FuelsAdapter(activity, fuels);
        AbsListView listView = new ViewHelper(view).get(android.R.id.list);
        listView.setAdapter(fuelsAdapter);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (EditFuelListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " + EditFuelListener.class.getName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setFuelRepository(FuelRepository fuelRepository) {
        this.fuelRepository = fuelRepository;
    }

    @Override
    public void onListItemClick(android.widget.ListView l, android.view.View v, final int position, long id) {
        listener.edit(fuels.get(position));
    }

    public interface EditFuelListener {
        void edit(Fuel fuel);
    }
}
