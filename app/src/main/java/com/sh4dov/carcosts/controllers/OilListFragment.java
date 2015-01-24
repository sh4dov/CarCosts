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
import com.sh4dov.carcosts.controllers.adapters.OilAdapter;
import com.sh4dov.carcosts.infrastructure.ToastNotificator;
import com.sh4dov.carcosts.model.Oil;
import com.sh4dov.carcosts.repositories.DbHandler;
import com.sh4dov.carcosts.repositories.OilRepository;
import com.sh4dov.common.ProgressIndicator;
import com.sh4dov.common.ProgressPointerIndicator;
import com.sh4dov.common.TaskScheduler;
import com.sh4dov.common.ViewHelper;

import java.util.ArrayList;

public class OilListFragment extends ListFragment {

    private EditOilListener listener;
    private ArrayList<Oil> oil = new ArrayList<Oil>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OilListFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (EditOilListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement " + EditOilListener.class.getName());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_oil_list, container, false);

        Activity activity = getActivity();
        final OilRepository oilRepository = new OilRepository(new DbHandler(activity), new ToastNotificator(activity));
        final ProgressPointerIndicator progressPointer = new ProgressPointerIndicator();
        final OilAdapter oilAdapter = new OilAdapter(activity, oil);
        ProgressIndicator progressIndicator = new ProgressIndicator(activity, ProgressDialog.STYLE_HORIZONTAL, new TaskScheduler(activity)
                .willExecute(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<Oil> result = oilRepository.getOil(progressPointer);
                        oil.clear();
                        oil.addAll(result);
                    }
                })
                .willExecuteOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        oilAdapter.notifyDataSetChanged();
                    }
                }));

        progressPointer.setProgressPointer(progressIndicator);
        progressIndicator.execute();
        AbsListView listView = new ViewHelper(view).get(android.R.id.list);
        listView.setAdapter(oilAdapter);

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        listener.edit(oil.get(position));
    }

    public interface EditOilListener {
        void edit(Oil oil);
    }

}
