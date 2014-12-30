package com.sh4dov.carcosts.controllers;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;

import com.sh4dov.carcosts.R;
import com.sh4dov.carcosts.controllers.view.operators.FuelViewOperator;
import com.sh4dov.carcosts.infrastructure.ToastNotificator;
import com.sh4dov.carcosts.model.Fuel;
import com.sh4dov.carcosts.repositories.DbHandler;
import com.sh4dov.carcosts.repositories.FuelRepository;
import com.sh4dov.common.ViewHelper;

import java.util.Calendar;


public class EditFuelActivity extends Activity {
    public final static String EditFuelKey = "EditFuelKey";
    private Fuel fuel;
    private FuelRepository fuelRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_fuel);
        fuelRepository = new FuelRepository(new DbHandler(this), new ToastNotificator(this));
        final ViewHelper viewHelper = new ViewHelper(this);

        viewHelper.get(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fuel updated = new FuelViewOperator(viewHelper).get(fuel);
                if (updated.isValid()) {
                    fuelRepository.update(updated);
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        viewHelper.get(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fuelRepository.delete(fuel);
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    @Override
    public void onStart() {
        fuel = (Fuel) getIntent().getSerializableExtra(EditFuelKey);
        Fuel previous = fuelRepository.getPrevious(fuel);
        Fuel next = fuelRepository.getNext(fuel);
        int maxMileage = next.mileage < fuel.mileage ? Fuel.MAX_MILEAGE : next.mileage;
        ViewHelper viewHelper = new ViewHelper(this);
        new FuelViewOperator(viewHelper).set(fuel);

        viewHelper.setDate(R.id.datePicker, fuel.date);

        NumberPicker mileage = viewHelper.get(R.id.mileage);
        mileage.setMinValue(previous.mileage);
        mileage.setMaxValue(maxMileage);
        mileage.setValue(fuel.mileage);

        super.onStart();
    }
}
