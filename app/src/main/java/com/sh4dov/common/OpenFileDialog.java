package com.sh4dov.common;

import android.view.LayoutInflater;
import android.view.View;

import com.sh4dov.carcosts.R;

import java.io.File;

public class OpenFileDialog extends FileDialogBase {

    @Override
    protected View getView(LayoutInflater layoutInflater) {
        return layoutInflater.inflate(R.layout.open_file_dialog, null);
    }

    @Override
    protected void onSelectedChanged(File selected) {
    }

    protected boolean shouldEnablePositiveButton(File selected) {
        return selected != null && selected.isFile();
    }
}

