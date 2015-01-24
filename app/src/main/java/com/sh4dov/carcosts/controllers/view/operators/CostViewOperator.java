package com.sh4dov.carcosts.controllers.view.operators;

import com.sh4dov.carcosts.R;
import com.sh4dov.carcosts.model.Cost;
import com.sh4dov.common.ViewHelper;
import com.sh4dov.common.ViewOperator;

public class CostViewOperator extends ViewOperator<Cost> {

    public CostViewOperator(ViewHelper viewHelper) {
        super(viewHelper);
    }

    @Override
    public Cost get(Cost instance) {
        Cost cost = instance != null ? instance : new Cost();
        viewHelper.clearFocus();
        cost.date = viewHelper.getDate(R.id.date);
        cost.cost = getNumber(R.id.cost1, R.id.cost2, 100);
        cost.comment = viewHelper.getText(R.id.comment);

        return cost;
    }

    @Override
    public void set(Cost cost) {
        setTwoDigitNumbers(cost.cost, R.id.cost1, R.id.cost2, 1000000);
        setText(cost.comment, R.id.comment);
    }
}

