package com.sh4dov.common;

import android.widget.EditText;
import android.widget.NumberPicker;

import com.sh4dov.common.ViewHelper;

public abstract class ViewOperator<T>{
    protected final NumberPicker.Formatter twoDigitsFormatter;
    protected ViewHelper viewHelper;

    public ViewOperator(ViewHelper viewHelper) {
        twoDigitsFormatter = new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        };
        this.viewHelper = viewHelper;
    }

    public abstract T get(T instance);
    public abstract void set(T instance);

    protected void setOneDigitNumbers(double originalValue, int idPar1, int idPart2, int maxValue) {
        NumberPicker numberPicker1 = viewHelper.get(idPar1);
        NumberPicker numberPicker2 = viewHelper.get(idPart2);
        int part1 = (int) Math.floor(originalValue);
        int part2 = (int) Math.floor((originalValue * 10) - (part1 * 10));
        numberPicker1.setMinValue(0);
        numberPicker2.setMinValue(0);
        numberPicker1.setMaxValue(maxValue);
        numberPicker2.setMaxValue(9);
        numberPicker1.setValue(part1);
        numberPicker2.setValue(part2);
    }

    protected void setTwoDigitNumbers(double originalValue, int idPart1, int idPart2, int maxValue) {
        NumberPicker numberPicker1 = viewHelper.get(idPart1);
        NumberPicker numberPicker2 = viewHelper.get(idPart2);
        int part1 = (int) Math.floor(originalValue);
        int part2 = (int) Math.floor((originalValue * 100) - (part1 * 100));
        numberPicker1.setMinValue(0);
        numberPicker2.setMinValue(0);
        numberPicker1.setMaxValue(maxValue);
        numberPicker2.setMaxValue(99);
        numberPicker2.setFormatter(twoDigitsFormatter);
        numberPicker1.setValue(part1);
        numberPicker2.setValue(part2);
    }

    protected double getNumber(int idPart1, int idPart2, int divider) {
        NumberPicker part1 = viewHelper.get(idPart1);
        NumberPicker part2 = viewHelper.get(idPart2);
        return part1.getValue() + ((double) part2.getValue() / divider);
    }

    protected void setText(String text, int id) {
        EditText editText = viewHelper.get(id);
        editText.setText(text);
    }
}
