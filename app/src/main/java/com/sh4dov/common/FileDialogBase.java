package com.sh4dov.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sh4dov.carcosts.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public abstract class FileDialogBase extends DialogFragment {
    private static final String PARENT = "..";
    private AlertDialog dialog;
    private File file = null;
    private ArrayList<String> items = new ArrayList<String>();
    private ListenerList<DialogListener> listeners = new ListenerList<DialogListener>();
    private File path = null;
    private ViewHelper viewHelper;

    public FileDialogBase addListeners(DialogListener listener) {
        listeners.add(listener);
        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        final Activity activity = getActivity();
        if (path == null) {
            path = Environment.getRootDirectory();
        }
        listDirectory(path);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View view = getView(layoutInflater);

        viewHelper = new ViewHelper(view);
        final ListView listView = viewHelper.get(R.id.file_list);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AbsListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                File selected = getSelected(i);
                if (selected.isDirectory()) {
                    path = selected;
                    listDirectory(path);
                    adapter.notifyDataSetChanged();
                    listView.setSelectionAfterHeaderView();
                    file = null;
                }

                if (selected.isFile()) {
                    file = selected;
                }
                onSelectedChanged(getSelected());
                displayPath();
                calculatePossitiveButtonState();
            }
        });

        dialog = builder.setView(view)
                .setPositiveButton(R.id.select, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (shouldEnablePositiveButton(getSelected())) {
                            onSelected(getFinalSelection());
                        }
                    }
                })
                .setNegativeButton(R.id.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FileDialogBase.this.getDialog().cancel();
                    }
                })
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                calculatePossitiveButtonState();
            }
        });

        displayPath();
        return dialog;
    }

    public FileDialogBase setPath(File path) {
        this.path = path;
        return this;
    }

    protected void calculatePossitiveButtonState() {
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(shouldEnablePositiveButton(getSelected()));
    }

    protected void detachFile() {
        if (file != null) {
            file = null;
            onSelectedChanged(getSelected());
            displayPath();
        }
    }

    protected File getFinalSelection() {
        return getSelected();
    }

    protected File getSelected() {
        return file != null ? file : path;
    }

    protected abstract View getView(LayoutInflater layoutInflater);

    protected abstract void onSelectedChanged(File selected);

    protected abstract boolean shouldEnablePositiveButton(File selected);

    private void displayPath() {
        TextView textView = viewHelper.get(R.id.path);
        textView.setText(file != null ? file.getAbsolutePath() : path.getAbsolutePath());
    }

    private File getSelected(int i) {
        String item = items.get(i);
        if (item.equalsIgnoreCase(PARENT)) {
            return path.getParentFile();
        }

        return new File(path, item);
    }

    private void listDirectory(File path) {
        items.clear();
        if (path.getParentFile() != null) {
            items.add(PARENT);
        }
        String[] list = path.list();
        if (list == null) {
            return;
        }

        Collections.addAll(items, list);
    }

    private void onSelected(final File file) {
        listeners.fireEvent(new ListenerList.FireHandler<DialogListener>() {
            @Override
            public void fireEvent(DialogListener listener) {
                listener.selected(file);
            }
        });
    }

    public interface DialogListener {
        void selected(File file);
    }
}
