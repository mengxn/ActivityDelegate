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
import android.support.v4.app.SharedElementCallback;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.view.View;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author mengxn 2017/6/20
 */

public class IntentRequest implements IRequest {

    private Intent mIntent;
    private boolean mIsKeep = true;
    /**
     * 页面过渡动画参数
     */
    private Bundle mCustomOptions;
    /**
     * 场景元素动画
     */
    private List<Pair<View, String>> mShareViewList;
    private Activity mActivity;
    private DelegateFragment mDelegateFragment;
    /**
     * 默认配置信息
     */
    static PIntent.Config mConfig;

    private static final String TAG = "IntentRequest";
    static final String ANIMATION_SCENE = "transition";

    IntentRequest(FragmentActivity activity) {
        mActivity = activity;
        mIntent = new Intent();
        mDelegateFragment = getDelegateFragment(activity);
    }

    private DelegateFragment getDelegateFragment(FragmentActivity activity) {
        if (activity == null
                || activity.isFinishing()
                || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed())) {
            // Activity 不可用，直接返回 null
            return null;
        }
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
        mCustomOptions = activityOptionsCompat.toBundle();
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
        ViewCompat.setTransitionName(view, name);
        if (mShareViewList == null) {
            mShareViewList = new ArrayList<>();
        }
        mShareViewList.add(new Pair<View, String>(view, name));
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
        to(mIntent, -1, getOptions());
    }

    @Override
    public IRequest to(String action, int requestCode) {
        mIntent.setAction(action);
        to(mIntent, requestCode, getOptions());
        return this;
    }

    @Override
    public void to(Class cls) {
        to(cls, -1);
    }

    @Override
    public IRequest to(Class cls, int requestCode) {
        mIntent.setComponent(new ComponentName(mActivity, cls));
        to(mIntent, requestCode, getOptions());
        return this;
    }

    private void to(Intent intent, int requestCode, Bundle options) {
        if (mDelegateFragment == null || !mDelegateFragment.isAdded()) {
            return;
        }
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

    /**
     * 获取过渡动画参数
     */
    private Bundle getOptions() {
        if (mShareViewList != null && mShareViewList.size() > 0) {
            return ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, mShareViewList.toArray(new Pair[mShareViewList.size()])).toBundle();
        }
        if (mCustomOptions != null) {
            return mCustomOptions;
        }
        if (mConfig != null && mConfig.openAnimOptions != null) {
            return new Bundle(mConfig.openAnimOptions);
        }
        return null;
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
        if (mShareViewList != null && mShareViewList.size() > 0) {
            // 1.有共享元素
            ActivityCompat.setEnterSharedElementCallback(mActivity, new SharedElementCallback() {
                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                    names.clear();
                    sharedElements.clear();
                    // 重新设置共享元素信息
                    for (Pair<View, String> pair : mShareViewList) {
                        names.add(pair.second);
                        sharedElements.put(pair.second, pair.first);
                    }
                }
            });
            mIntent.putExtra(PIntent.KEY_TRANSITION, true);
            Bundle options = getOptions();
            if (options != null) {
                mIntent.putExtras(options);
            }
            mActivity.setResult(Activity.RESULT_OK, mIntent);
            ActivityCompat.finishAfterTransition(mActivity);
        } else if (mCustomOptions != null) {
            // 2.有过渡动画
            String enterKey = getEnterAnimKey();
            String exitKey = getExitAnimKey();
            mActivity.finish();
            if (mCustomOptions.containsKey(enterKey) || mCustomOptions.containsKey(exitKey)) {
                mActivity.overridePendingTransition(mCustomOptions.getInt(enterKey, 0), mCustomOptions.getInt(exitKey, 0));
            }
        } else if (mConfig != null && mConfig.closeAnimOptions != null) {
            // 3.有默认过渡动画
            int enterAnim = mConfig.closeAnimOptions.getInt(getEnterAnimKey(), 0);
            int exitAnim = mConfig.closeAnimOptions.getInt(getExitAnimKey(), 0);
            mActivity.finish();
            mActivity.overridePendingTransition(enterAnim, exitAnim);
        } else {
            // 4.默认关闭
            mActivity.finish();
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
