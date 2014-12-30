package com.sh4dov.common;

import android.app.Activity;
import android.view.View;

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
