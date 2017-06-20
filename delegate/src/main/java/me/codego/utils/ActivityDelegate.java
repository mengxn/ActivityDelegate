package me.codego.utils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.TransitionRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Pair;
import android.view.View;
import android.view.Window;

import java.io.Serializable;

/**
 * activity 切换代理
 * Created by mengxn on 2017/4/11.
 */
public class ActivityDelegate {

    private Context mContext;
    private boolean mIsKeep = true;//是否保留上一界面，默认保留
    private Bundle mOptions;//动画参数

    //默认配置
    private static Config defaultConfig = Config.getInstance();

    private Intent intent;

    private static final String KEY_ANIM_ENTER = "android:activity.animEnterRes";
    private static final String KEY_ANIM_EXIT = "android:activity.animExitRes";

    private ActivityDelegate(Context context) {
        mContext = context;
        intent = new Intent();
    }

    public static ActivityDelegate from(Context context) {
        return new ActivityDelegate(context);
    }

    public static FragmentRequest from(Fragment fragment) {
        return new FragmentRequest(fragment);
    }

    public ActivityDelegate with(String key, Object value) {
        if (value == null) {
            return this;
        }
        if (value instanceof Serializable) {
            intent.putExtra(key, (Serializable) value);
        } else if (value instanceof Parcelable) {
            intent.putExtra(key, (Parcelable) value);
        }
        return this;
    }

    public ActivityDelegate with(Bundle bundle) {
        if (bundle == null) {
            return this;
        }
        intent.putExtras(bundle);
        return this;
    }

    /**
     * 是否保留当前界面
     * @param isKeep
     * @return
     */
    public ActivityDelegate keep(boolean isKeep) {
        mIsKeep = isKeep;
        return this;
    }

    /**
     * 场景转换动画
     * @param enterResId
     * @param exitResId
     * @return
     */
    public ActivityDelegate transition(int enterResId, int exitResId) {
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeCustomAnimation(mContext, enterResId, exitResId);
        mOptions = activityOptionsCompat.toBundle();
        return this;
    }

    /**
     * 场景转换动画
     * @param resource
     * @param sharedElements
     * @return
     */
    public ActivityDelegate scene(@TransitionRes int resource, Pair<View, String>... sharedElements) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            with("transition", resource);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(((Activity) mContext), sharedElements);
            mOptions = options.toBundle();
        }
        return this;
    }

    /**
     * 增加flag标识
     * @param flag
     * @return
     */
    public ActivityDelegate flag(int flag) {
        intent.addFlags(flag);
        return this;
    }

    /**
     * 跳转到指定界面
     * @param cls
     */
    public void to(Class cls) {
        intent.setComponent(new ComponentName(mContext, cls));
        startActivityForResult(intent, -1);
    }

    /**
     * 通过action打开指定activity
     * @param action
     */
    public void to(String action) {
        intent.setAction(action);
        startActivityForResult(intent, -1);
    }

    /**
     * 跳转到指定界面
     * @param cls
     * @param requestCode
     * @return
     */
    public ActivityResponse to(Class cls, int requestCode) {
        intent.setComponent(new ComponentName(mContext, cls));
        startActivityForResult(intent, requestCode);
        return ActivityResponse.createWithCode(requestCode);
    }

    /**
     * 启动目标activity
     */
    private void startActivityForResult(Intent intent, int requestCode){
        if (mOptions == null && defaultConfig != null) {
            mOptions = new Bundle(defaultConfig.options);
        }
        if (mContext instanceof Activity) {
            ActivityCompat.startActivityForResult((Activity) mContext, intent, requestCode, mOptions);
        } else {
            ActivityCompat.startActivity(mContext, intent, mOptions);
        }
        //16以下没有动画，需要使用overridePendingTransition
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            if (mContext instanceof Activity && mOptions != null) {
                Activity activity = (Activity) mContext;
                activity.overridePendingTransition(mOptions.getInt(KEY_ANIM_ENTER), mOptions.getInt(KEY_ANIM_EXIT));
            }
        }

        if (!mIsKeep) {
            finish();
        }
    }

    private void finish() {
        if (mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.finishAfterTransition();
            } else {
                activity.finish();
            }
        }
    }

    /**
     * 执行当前界面场景动画
     * @param activity
     */
    public static void applyScene(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = activity.getIntent();
            if (intent != null && intent.hasExtra("transition")) {
                Window window = activity.getWindow();
                window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
                int resId = intent.getIntExtra("transition", 0);
                if (resId > 0) {
                    Transition transition = TransitionInflater.from(activity).inflateTransition(resId);
                    window.setEnterTransition(transition);
                }
            }
        }
    }

    public static class Config {

        private int enterResId, exitResId;
        //动画参数
        private Bundle options;

        private Config() {
        }

        public static Config getInstance() {
            return new Config();
        }

        public void reset() {
            this.enterResId = 0;
            this.exitResId = 0;
        }

        public Config transition(int enterResId, int exitResId) {
            this.enterResId = enterResId;
            this.exitResId = exitResId;
            return this;
        }

        public void apply(Context context) {
            ActivityDelegate.defaultConfig = this;
            options = ActivityOptionsCompat.makeCustomAnimation(context, enterResId, exitResId).toBundle();
        }
    }

}
