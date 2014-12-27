package com.sh4dov.common;

/**
 * Created by sh4dov on 2014-12-26.
 */
public class ProgressPointerOperator {
    private ProgressPointer pointer;
    private int progress = 0;

    public ProgressPointerOperator(ProgressPointer pointer) {
        this.pointer = pointer != null ? pointer : new NullProgressPointer();
    }

    public void addProgress() {
        pointer.setProgress(++progress);
    }

    public void setMax(int max) {
        progress = 0;
        pointer.setMax(max);
    }
}

