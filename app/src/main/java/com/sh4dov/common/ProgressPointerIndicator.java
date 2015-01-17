package com.sh4dov.common;

public class ProgressPointerIndicator implements ProgressPointer {
    private ProgressPointer progressPointer;

    public void setProgressPointer(ProgressPointer progressPointer) {

        this.progressPointer = progressPointer;
    }

    @Override
    public void setProgress(int progress) {
        if (progressPointer != null) {
            progressPointer.setProgress(progress);
        }
    }

    @Override
    public void setMax(int max) {
        if (progressPointer != null) {
            progressPointer.setMax(max);
        }
    }
}
