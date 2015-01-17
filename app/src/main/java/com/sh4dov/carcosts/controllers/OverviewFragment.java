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

import com.sh4dov.carcosts.R;
import com.sh4dov.carcosts.businesslogic.OverviewProvider;
import com.sh4dov.carcosts.controllers.adapters.TotalCostsAdapter;
import com.sh4dov.carcosts.infrastructure.ToastNotificator;
import com.sh4dov.carcosts.repositories.CostRepository;
import com.sh4dov.carcosts.repositories.DbHandler;
import com.sh4dov.carcosts.repositories.FuelRepository;
import com.sh4dov.carcosts.repositories.OilRepository;
import com.sh4dov.common.Notificator;
import com.sh4dov.common.ProgressIndicator;
import com.sh4dov.common.ProgressPointerIndicator;
import com.sh4dov.common.TaskScheduler;
import com.sh4dov.common.ViewHelper;

import java.util.ArrayList;

public class OverviewFragment extends ListFragment {
    private ArrayList<OverviewProvider.TotalCosts> totalCosts;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_total_costs_list, container, false);

        Activity activity = getActivity();
        Notificator notificator = new ToastNotificator(activity);
        DbHandler dbHandler = new DbHandler(activity);
        totalCosts = new ArrayList<OverviewProvider.TotalCosts>();
        final TotalCostsAdapter totalCostsAdapter = new TotalCostsAdapter(activity, totalCosts);
        final OverviewProvider overviewProvider = new OverviewProvider(
                new FuelRepository(dbHandler, notificator),
                new CostRepository(dbHandler, notificator),
                new OilRepository(dbHandler, notificator));

        final ProgressPointerIndicator progressPointer = new ProgressPointerIndicator();
        ProgressIndicator progressIndicator = new ProgressIndicator(activity, ProgressDialog.STYLE_HORIZONTAL, new TaskScheduler(activity)
                .willExecute(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<OverviewProvider.TotalCosts> result = overviewProvider.getTotalCosts(progressPointer);
                        totalCosts.clear();
                        totalCosts.addAll(result);
                    }
                })
                .willExecuteOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        totalCostsAdapter.notifyDataSetChanged();
                    }
                }));
        progressPointer.setProgressPointer(progressIndicator);
        progressIndicator.execute();
        AbsListView listView = new ViewHelper(view).get(android.R.id.list);
        listView.setAdapter(totalCostsAdapter);

        return view;
    }

}
