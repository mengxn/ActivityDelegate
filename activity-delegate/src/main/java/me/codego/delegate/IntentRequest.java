package me.codego.delegate;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * @author mengxn 2017/6/20
 */

public class IntentRequest implements IRequest {

    private Intent mIntent;
    private boolean mIsKeep = true;
    private Bundle mOptions;
    private Activity mActivity;
    private DelegateFragment mDelegateFragment;

    static PIntent.Config mConfig;

    private static final String TAG = "IntentRequest";
    static final String ANIMATION_SCENE = "transition";

    IntentRequest(FragmentActivity activity) {
        mActivity = activity;
        mIntent = new Intent();
        mDelegateFragment = getDelegateFragment(activity);
    }

    private DelegateFragment getDelegateFragment(FragmentActivity activity) {
        DelegateFragment fragment = findDelegateFragment(activity);
        if (fragment == null) {
            fragment = new DelegateFragment();
            final FragmentManager fragmentManager = activity.getSupportFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .add(fragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return fragment;
    }

    private DelegateFragment findDelegateFragment(FragmentActivity activity) {
        return (DelegateFragment) activity.getSupportFragmentManager().findFragmentByTag(TAG);
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
        } else if (value instanceof CharSequence) {
            mIntent.putExtra(key, ((CharSequence) value));
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
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeCustomAnimation(mActivity, enterResId, exitResId);
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
        final Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, view, name).toBundle();
        if (bundle != null) {
            mOptions.putAll(bundle);
        }
        return this;
    }

    /**
     * Add additional flags to the intent
     *
     * @param flag 标识
     * @return IRequest
     */
    @Override
    public IRequest flag(int flag) {
        mIntent.addFlags(flag);
        return this;
    }

    @Override
    public void to(String action) {
        mIntent.setAction(action);
        to(mIntent, -1, mOptions);
    }

    @Override
    public IRequest to(String action, int requestCode) {
        mIntent.setAction(action);
        to(mIntent, requestCode, mOptions);
        return this;
    }

    @Override
    public void to(Class cls) {
        to(cls, -1);
    }

    @Override
    public IRequest to(Class cls, int requestCode) {
        mIntent.setComponent(new ComponentName(mActivity, cls));
        if (mOptions == null && mConfig != null) {
            mOptions = new Bundle();
            if (mConfig.openAnimOptions != null) {
                mOptions.putAll(mConfig.openAnimOptions);
            }
        }
        to(mIntent, requestCode, mOptions);
        return this;
    }

    private void to(Intent intent, int requestCode, Bundle options) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (requestCode == -1) {
                mDelegateFragment.startActivity(intent, options);
            } else {
                mDelegateFragment.startActivityForResult(intent, requestCode, options);
            }
        } else {
            if (requestCode == -1) {
                mDelegateFragment.startActivity(intent);
            } else {
                mDelegateFragment.startActivityForResult(intent, requestCode);
            }

            //16以下没有动画，需要使用overridePendingTransition
            if (options != null) {
                mActivity.overridePendingTransition(options.getInt(getEnterAnimKey()), options.getInt(getExitAnimKey()));
            }
        }

        if (!mIsKeep) {
            finish();
        }
    }

    @Override
    public void result(IRequest.Callback callback) {
        if (mDelegateFragment != null) {
            mDelegateFragment.setCallback(callback);
        }
    }

    @Override
    public IRequest keep(boolean isKeep) {
        mIsKeep = isKeep;
        return this;
    }

    @Override
    public void finish() {
        ActivityCompat.finishAfterTransition(mActivity);
        String enterKey = getEnterAnimKey();
        String exitKey = getExitAnimKey();
        if (mOptions.containsKey(enterKey) || mOptions.containsKey(exitKey)) {
            mActivity.overridePendingTransition(mOptions.getInt(enterKey, 0), mOptions.getInt(exitKey, 0));
        } else if (mConfig != null && mConfig.closeAnimOptions != null) {
            mActivity.overridePendingTransition(mConfig.closeAnimOptions.getInt(enterKey, 0), mConfig.closeAnimOptions.getInt(exitKey, 0));
        }
    }

    private String getEnterAnimKey() {
        return getAnimKey("KEY_ANIM_ENTER_RES_ID", "android:activity.animEnterRes");
    }

    private String getExitAnimKey() {
        return getAnimKey("KEY_ANIM_EXIT_RES_ID", "android:activity.animExitRes");
    }

    private String getAnimKey(String key, String defaultValue) {
        try {
            Class<?> clazz = Class.forName("ActivityOptions");
            Field field = clazz.getField(key);
            return (String)field.get(null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

}
