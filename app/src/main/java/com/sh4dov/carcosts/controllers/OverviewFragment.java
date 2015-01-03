package com.sh4dov.carcosts.controllers;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sh4dov.carcosts.R;
import com.sh4dov.carcosts.businesslogic.OverviewProvider;
import com.sh4dov.carcosts.infrastructure.ToastNotificator;
import com.sh4dov.carcosts.repositories.CostRepository;
import com.sh4dov.carcosts.repositories.DbHandler;
import com.sh4dov.carcosts.repositories.FuelRepository;
import com.sh4dov.common.Notificator;
import com.sh4dov.common.ProgressIndicator;
import com.sh4dov.common.ProgressPointerIndicator;
import com.sh4dov.common.TaskScheduler;
import com.sh4dov.common.ViewHelper;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class OverviewFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_overview, container, false);

        Activity activity = getActivity();
        Notificator notificator = new ToastNotificator(activity);
        DbHandler dbHandler = new DbHandler(activity);
        final OverviewProvider.TotalCosts[] totalCosts = new OverviewProvider.TotalCosts[1];
        final OverviewProvider overviewProvider = new OverviewProvider(new FuelRepository(dbHandler, notificator), new CostRepository(dbHandler, notificator));
        final ProgressPointerIndicator progressPointer = new ProgressPointerIndicator();
        ProgressIndicator progressIndicator = new ProgressIndicator(activity, ProgressDialog.STYLE_HORIZONTAL, new TaskScheduler(activity)
                .willExecute(new Runnable() {
                    @Override
                    public void run() {
                        totalCosts[0] = overviewProvider.getTotalCosts(progressPointer);
                    }
                })
                .willExecuteOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setTotalCosts(totalCosts[0]);
                    }
                }));
        progressPointer.setProgressPointer(progressIndicator);
        progressIndicator.execute();

        return rootView;
    }

    private void setTotalCosts(OverviewProvider.TotalCosts totalCosts) {
        ViewHelper viewHelper = new ViewHelper(getView());
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
        formatSymbols.setGroupingSeparator(' ');
        DecimalFormat decimalFormat = new DecimalFormat("###,##0.00", formatSymbols);

        viewHelper.setText(R.id.total_cost, decimalFormat.format(totalCosts.getTotal()) + " zł");
        viewHelper.setText(R.id.fuel_cost, decimalFormat.format(totalCosts.fuel) + " zł");
        viewHelper.setText(R.id.other_cost, decimalFormat.format(totalCosts.other) + " zł");
    }
}
