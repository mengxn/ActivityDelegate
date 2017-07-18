package me.codego.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
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


    public IntentRequest(Context context) {
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
            with("transition", resId);
        }
        return this;
    }

    @Override
    public IRequest share(View view, String name) {
        final Bundle bundle = makeSceneTransitionAnimation(Pair.create(view, name));
        if (mOptions == null) {
            mOptions = bundle;
        } else {
            mOptions.putAll(bundle);
        }
        return this;
    }

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

    abstract ActivityResponse startActivityForResult(Intent intent, int requestCode, Bundle options);

    abstract Bundle makeSceneTransitionAnimation(Pair<View, String>... sharedElements);

}
