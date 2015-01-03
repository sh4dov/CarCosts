package com.sh4dov.carcosts.controllers;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.sh4dov.carcosts.R;
import com.sh4dov.carcosts.controllers.view.operators.OilViewOperator;
import com.sh4dov.carcosts.infrastructure.ToastNotificator;
import com.sh4dov.carcosts.model.Oil;
import com.sh4dov.carcosts.repositories.DbHandler;
import com.sh4dov.carcosts.repositories.OilRepository;
import com.sh4dov.common.ViewHelper;


public class EditOilActivity extends Activity {
    public static final String EditOilKey = "EditOilKey";
    private Oil oil;
    private OilRepository oilRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_oil);
        oilRepository = new OilRepository(new DbHandler(this), new ToastNotificator(this));
        final ViewHelper viewHelper = new ViewHelper(this);

        viewHelper.get(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Oil updated = new OilViewOperator(viewHelper).get(oil);
                if (updated.isValid()) {
                    oilRepository.update(updated);
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        viewHelper.get(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oilRepository.delete(oil);
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    @Override
    public void onStart() {
        oil = (Oil) getIntent().getSerializableExtra(EditOilKey);
        ViewHelper viewHelper = new ViewHelper(this);
        new OilViewOperator(viewHelper).set(oil);
        viewHelper.setDate(R.id.date, oil.date);

        super.onStart();
    }
}
