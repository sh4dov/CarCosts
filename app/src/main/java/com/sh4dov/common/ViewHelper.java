package com.sh4dov.common;

import android.view.View;

public class ViewHelper{
    private View view;

    public ViewHelper(View view){
        this.view = view;
    }

    public <T extends View> T get(int id){
        return (T)view.findViewById(id);
    }
}
