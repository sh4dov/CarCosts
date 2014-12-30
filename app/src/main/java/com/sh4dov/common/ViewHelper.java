package com.sh4dov.common;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class ViewHelper{
    private FindView findView;

    public ViewHelper(View view){
        findView = new ViewFindView(view);
    }

    public ViewHelper(Activity activity){
        findView = new ActivityFindView(activity);
    }

    public <T extends View> T get(int id){
        return (T) findView.findViewById(id);
    }

    public Date getDate(int id){
        DatePicker date = get(id);
        Calendar calendar = Calendar.getInstance();
        calendar.set(date.getYear(), date.getMonth(), date.getDayOfMonth());
        return calendar.getTime();
    }

    public String getText(int id){
        EditText editText = get(id);
        return editText.getText().toString();
    }

    public void setText(int id, String text){
        TextView textView = get(id);
        textView.setText(text);
    }

    public void setDate(int id, Date date) {
        DatePicker datePicker = get(id);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    private interface FindView{
        View findViewById(int id);
    }

    private class ViewFindView implements FindView{
        private View view;

        public ViewFindView(View view){
            this.view = view;
        }
        @Override
        public View findViewById(int id) {
            return view.findViewById(id);
        }
    }

    private class ActivityFindView implements FindView{
        private Activity activity;

        public ActivityFindView(Activity activity){
            this.activity = activity;
        }

        @Override
        public View findViewById(int id) {
            return activity.findViewById(id);
        }
    }
}
