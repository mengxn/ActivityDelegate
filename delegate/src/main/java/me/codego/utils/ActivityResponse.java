package me.codego.utils;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by mengxn on 2017/6/1.
 */

public class ActivityResponse {

    private Callback mCallback;
    private int mRequestCode;

    private ActivityResponse(int requestCode) {
        mRequestCode = requestCode;
    }

    public static ActivityResponse createWithCode(int requestCode) {
        return new ActivityResponse(requestCode);
    }

    public ActivityResponse result(Callback callback) {
        mCallback = callback;
        return this;
    }

    public void apply(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == mRequestCode) {
                mCallback.onResult(data);
            }
        }
    }

    public interface Callback {
        void onResult(Intent data);
    }

}
