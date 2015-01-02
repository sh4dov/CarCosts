package com.sh4dov.common;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sh4dov.carcosts.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by sh4dov on 2014-12-30.
 */
public class FileDialog extends DialogFragment {
    private static final String PARENT = "..";
    private ListenerList<DialogListener> listeners = new ListenerList<DialogListener>();
    private File path = null;
    private File file = null;
    private ArrayList<String> items = new ArrayList<String>();
    private AlertDialog dialog;

    public FileDialog addListeners(DialogListener listener) {
        listeners.add(listener);
        return this;
    }

    private void onSelected(final File file){
        listeners.fireEvent(new ListenerList.FireHandler<DialogListener>() {
            @Override
            public void fireEvent(DialogListener listener) {
                listener.selected(file);
            }
        });
    }

    public interface DialogListener{
        void selected(File file);
    }

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        final Activity activity = getActivity();
        if(path == null){
            path = Environment.getRootDirectory();
        }
        listDirectory(path);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.file_dialog, null);

        final ViewHelper viewHelper = new ViewHelper(view);
        final ListView listView = viewHelper.get(R.id.file_list);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AbsListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                File selected = getSelected(i);
                if(selected.isDirectory()){
                    path = selected;
                    listDirectory(path);
                    adapter.notifyDataSetChanged();
                    listView.setSelectionAfterHeaderView();
                    file = null;
                }

                if(selected.isFile()){
                    file = selected;
                }

                displayPath(viewHelper);
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(file != null);
            }
        });

        dialog = builder.setView(view)
                .setPositiveButton(R.id.select, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (file != null) {
                            onSelected(file);
                        }
                    }
                })
                .setNegativeButton(R.id.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FileDialog.this.getDialog().cancel();
                    }
                })
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(file != null);
            }
        });

        displayPath(viewHelper);
        return dialog;
    }

    private File getSelected(int i){
        String item = items.get(i);
        if(item == PARENT){
            return path.getParentFile();
        }

        return new File(path, item);
    }

    private void listDirectory(File path) {
        items.clear();
        if(path.getParentFile() != null){
            items.add(PARENT);
        }
        String[] list = path.list();
        if(list == null){
            return;
        }

        for(String item: list){
            items.add(item);
        }
    }

    private void displayPath(ViewHelper viewHelper){
        TextView textView = viewHelper.get(R.id.path);
        textView.setText(file != null ? file.getAbsolutePath() : path.getAbsolutePath());
    }
}
