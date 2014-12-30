package com.sh4dov.carcosts.controllers.view.operators;

import com.sh4dov.carcosts.R;
import com.sh4dov.carcosts.model.Oil;
import com.sh4dov.common.ViewHelper;
import com.sh4dov.common.ViewOperator;

public class OilViewOperator extends ViewOperator<Oil> {

    public OilViewOperator(ViewHelper viewHelper) {
        super(viewHelper);
    }

    @Override
    public Oil get(Oil instance) {
        Oil oil = instance != null ? instance : new Oil();

        oil.date = viewHelper.getDate(R.id.date);
        oil.liters = getNumber(R.id.oil1, R.id.oil2, 100);
        oil.comment = viewHelper.getText(R.id.comment);

        return oil;
    }

    @Override
    public void set(Oil oil) {
        setTwoDigitNumbers(oil.liters, R.id.oil1, R.id.oil2, 100);
        setText(oil.comment, R.id.comment);
    }
}
