package com.sh4dov.common;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.sh4dov.carcosts.R;

import java.io.File;

public class SaveFileDialog extends FileDialogBase {
    private String fileName;
    private ViewHelper viewHelper;

    public SaveFileDialog addFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    @Override
    protected void onSelectedChanged(File selected) {
        if (selected != null && selected.isFile()) {
            fileName = selected.getName();
            updateFileName();
        }
    }

    @Override
    protected File getFinalSelection() {
        File file = super.getFinalSelection();
        return new File(file, fileName);
    }

    @Override
    protected boolean shouldEnablePositiveButton(File selected) {
        return selected != null && !fileName.isEmpty();
    }

    @Override
    protected View getView(LayoutInflater layoutInflater) {
        View view = layoutInflater.inflate(R.layout.save_file_dialog, null);

        viewHelper = new ViewHelper(view);
        if (fileName != null && !fileName.isEmpty()) {
            EditText editText = updateFileName();
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    fileName = editable.toString();
                    calculatePossitiveButtonState();
                    detachFile();
                }
            });
        }

        return view;
    }

    private EditText updateFileName() {
        EditText editText = viewHelper.get(R.id.file_name);
        editText.setText(fileName);
        return editText;
    }
}
