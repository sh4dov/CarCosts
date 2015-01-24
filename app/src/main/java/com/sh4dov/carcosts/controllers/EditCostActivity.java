package com.sh4dov.carcosts.controllers;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.sh4dov.carcosts.R;
import com.sh4dov.carcosts.controllers.view.operators.CostViewOperator;
import com.sh4dov.carcosts.infrastructure.ToastNotificator;
import com.sh4dov.carcosts.model.Cost;
import com.sh4dov.carcosts.repositories.CostRepository;
import com.sh4dov.carcosts.repositories.DbHandler;
import com.sh4dov.common.ViewHelper;


public class EditCostActivity extends Activity {
    public static final String EditCostKey = "EditCostKey";
    private Cost cost;
    private CostRepository costRepository;

    @Override
    public void onStart() {
        cost = (Cost) getIntent().getSerializableExtra(EditCostKey);
        ViewHelper viewHelper = new ViewHelper(this);
        new CostViewOperator(viewHelper).set(cost);
        viewHelper.setDate(R.id.date, cost.date);

        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cost);
        costRepository = new CostRepository(new DbHandler(this), new ToastNotificator(this));
        final ViewHelper viewHelper = new ViewHelper(this);

        viewHelper.get(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cost updated = new CostViewOperator(viewHelper).get(cost);
                if (updated.isValid()) {
                    costRepository.update(updated);
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        viewHelper.get(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                costRepository.delete(cost);
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
