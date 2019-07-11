package com.test.util;

import android.content.Context;

public class SyncHelper {

    private Context context;

    public SyncHelper(Context context) {
        this.context = context;
    }

    public boolean sync() {
        return false;
    }

    private boolean sendPreference() {
        return false;
    }

    private boolean sendData() {
        return false;
    }

    private boolean receivePreference() {
        return false;
    }

    private boolean receiveData() {
        return false;
    }
}
