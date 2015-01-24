package com.sh4dov.common;

public class ProgressPointerIndicator implements ProgressPointer {
    private ProgressPointer progressPointer;

    @Override
    public void setMax(int max) {
        if (progressPointer != null) {
            progressPointer.setMax(max);
        }
    }

    @Override
    public void setProgress(int progress) {
        if (progressPointer != null) {
            progressPointer.setProgress(progress);
        }
    }

    public void setProgressPointer(ProgressPointer progressPointer) {

        this.progressPointer = progressPointer;
    }
}
