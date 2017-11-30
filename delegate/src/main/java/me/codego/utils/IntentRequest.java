package me.codego.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;

import java.io.Serializable;

/**
 * Created by mengxn on 2017/6/20.
 */

public abstract class IntentRequest implements IRequest {

    private Intent mIntent;
    private boolean mIsKeep = true;
    private Bundle mOptions;
    private Context mContext;

    static PIntent.Config defaultConfig;

    static final String ANIMATION_SCENE = "transition";

    IntentRequest(Context context) {
        mContext = context;
        mIntent = new Intent();
    }

    @Override
    public IRequest with(String key, Object value) {
        if (value == null) {
            return this;
        }
        if (value instanceof Serializable) {
            mIntent.putExtra(key, (Serializable) value);
        } else if (value instanceof Parcelable) {
            mIntent.putExtra(key, (Parcelable) value);
        }
        return this;
    }

    @Override
    public IRequest with(Bundle bundle) {
        if (bundle == null) {
            return this;
        }
        mIntent.putExtras(bundle);
        return this;
    }

    @Override
    public IRequest transition(int enterResId, int exitResId) {
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeCustomAnimation(mContext, enterResId, exitResId);
        mOptions = activityOptionsCompat.toBundle();
        return this;
    }

    @Override
    public IRequest scene(int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            with(ANIMATION_SCENE, resId);
        }
        return this;
    }

    @Override
    public IRequest share(View view, String name) {
        if (mOptions == null) {
            mOptions = new Bundle();
        }
        mOptions.putAll(makeSceneTransitionAnimation(view, name));
        return this;
    }

    /**
     * Add additional flags to the intent
     * @param flag
     * @return
     */
    @Override
    public IRequest flag(int flag) {
        mIntent.addFlags(flag);
        return this;
    }

    @Override
    public void to(String action) {
        mIntent.setAction(action);
        startActivityForResult(mIntent, -1, null);
    }

    @Override
    public void to(Class cls) {
        to(cls, -1);
    }

    @Override
    public ActivityResponse to(Class cls, int requestCode) {
        mIntent.setComponent(new ComponentName(mContext, cls));
        if (mOptions == null && defaultConfig != null) {
            mOptions = new Bundle(defaultConfig.options);
        }
        ActivityResponse response = startActivityForResult(mIntent, requestCode, mOptions);

        if (!mIsKeep) {
            finish();
        }

        return response;
    }

    @Override
    public IRequest keep(boolean isKeep) {
        mIsKeep = isKeep;
        return this;
    }

    /**
     * 打开 Activity
     * @param intent
     * @param requestCode
     * @param options
     * @return
     */
    abstract ActivityResponse startActivityForResult(Intent intent, int requestCode, Bundle options);

    /**
     * 生产共享元素
     * @param sharedElement
     * @param sharedElementName
     * @return
     */
    abstract Bundle makeSceneTransitionAnimation(View sharedElement, String sharedElementName);

}
