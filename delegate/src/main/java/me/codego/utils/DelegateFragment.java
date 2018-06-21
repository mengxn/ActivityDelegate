package me.codego.utils;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

/**
 * 界面跳转代理类
 *
 * @author mengxn 2018-06-21
 */
public class DelegateFragment extends Fragment {

    private int mRequestCode;
    private IRequest.Callback mCallback;

    public void setCallback(IRequest.Callback callback) {
        mCallback = callback;
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        mRequestCode = requestCode;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mCallback != null && requestCode == mRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                mCallback.onResult(data);
            } else {
                if (mCallback instanceof IRequest.BiCallback) {
                    ((IRequest.BiCallback) mCallback).onCancel();
                }
            }
        }
    }
}
