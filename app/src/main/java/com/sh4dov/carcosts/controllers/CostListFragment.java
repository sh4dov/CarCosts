package com.sh4dov.carcosts.controllers;

import android.app.Activity;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.sh4dov.carcosts.R;
import com.sh4dov.carcosts.controllers.adapters.CostAdapter;
import com.sh4dov.carcosts.infrastructure.ToastNotificator;
import com.sh4dov.carcosts.model.Cost;
import com.sh4dov.carcosts.repositories.CostRepository;
import com.sh4dov.carcosts.repositories.DbHandler;
import com.sh4dov.common.ProgressIndicator;
import com.sh4dov.common.ProgressPointerIndicator;
import com.sh4dov.common.TaskScheduler;
import com.sh4dov.common.ViewHelper;

import java.util.ArrayList;

public class CostListFragment extends ListFragment {

    private ArrayList<Cost> costs = new ArrayList<Cost>();
    private EditCostListener listener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CostListFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (EditCostListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement " + EditCostListener.class.getName());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cost_list, container, false);

        Activity activity = getActivity();
        final CostRepository costRepository = new CostRepository(new DbHandler(activity), new ToastNotificator(activity));
        final ProgressPointerIndicator progressPointer = new ProgressPointerIndicator();
        final CostAdapter costAdapter = new CostAdapter(activity, costs);
        ProgressIndicator progressIndicator = new ProgressIndicator(activity, ProgressDialog.STYLE_HORIZONTAL, new TaskScheduler(activity)
                .willExecute(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<Cost> result = costRepository.getCosts(progressPointer);
                        costs.clear();
                        costs.addAll(result);
                    }
                })
                .willExecuteOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        costAdapter.notifyDataSetChanged();
                    }
                }));

        progressPointer.setProgressPointer(progressIndicator);
        progressIndicator.execute();
        AbsListView listView = new ViewHelper(view).get(android.R.id.list);
        listView.setAdapter(costAdapter);

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        listener.edit(costs.get(position));
    }

    public interface EditCostListener {
        void edit(Cost cost);
    }

}
