package com.sh4dov.carcosts.infrastructure;

import android.content.Context;
import android.widget.Toast;

import com.sh4dov.common.Notificator;

/**
 * Created by sh4dov on 2014-12-26.
 */
public class ToastNotificator implements Notificator {
    private Context context;

    public ToastNotificator(Context context){
        this.context = context;
    }

    @Override
    public void showInfo(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showInfo(int id) {
        Toast.makeText(context, context.getText(id), Toast.LENGTH_LONG).show();
    }
}
