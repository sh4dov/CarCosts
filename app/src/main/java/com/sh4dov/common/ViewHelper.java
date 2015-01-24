package com.sh4dov.common;

import android.app.Activity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class ViewHelper {
    private ViewAdapter viewAdapter;

    public ViewHelper(View view) {
        viewAdapter = new ViewViewAdapter(view);
    }

    public ViewHelper(Activity activity) {
        viewAdapter = new ActivityViewAdapter(activity);
    }

    public void clearFocus() {
        viewAdapter.clearFocus();
    }

    public <T extends View> T get(int id) {
        return (T) viewAdapter.findViewById(id);
    }

    public Date getDate(int id) {
        DatePicker date = get(id);
        Calendar calendar = Calendar.getInstance();
        calendar.set(date.getYear(), date.getMonth(), date.getDayOfMonth());
        return calendar.getTime();
    }

    public String getText(int id) {
        EditText editText = get(id);
        return editText.getText().toString();
    }

    public void setDate(int id, Date date) {
        DatePicker datePicker = get(id);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    public void setText(int id, String text) {
        TextView textView = get(id);
        textView.setText(text);
    }

    private interface ViewAdapter {
        void clearFocus();

        View findViewById(int id);
    }

    private class ViewViewAdapter implements ViewAdapter {
        private View view;

        public ViewViewAdapter(View view) {
            this.view = view;
        }

        @Override
        public void clearFocus() {
            view.clearFocus();
        }

        @Override
        public View findViewById(int id) {
            return view.findViewById(id);
        }
    }

    private class ActivityViewAdapter implements ViewAdapter {
        private Activity activity;

        public ActivityViewAdapter(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void clearFocus() {
            View focus = activity.getCurrentFocus();
            if (focus != null) {
                focus.clearFocus();
            }
        }

        @Override
        public View findViewById(int id) {
            return activity.findViewById(id);
        }
    }
}
