package me.codego.library.delegate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Activity 封装，统一跳转
 * Created by mengxn on 16-5-27.
 */
public class ActivityDelegate {

    private Context context;
    private Class cls;
    private Bundle bundle;

    private int enterAnim = android.R.anim.fade_in;
    private int exitAnim = android.R.anim.fade_out;

    public ActivityDelegate(Context context) {
        this.context = context;
    }

    private ActivityDelegate(Class cls) {
        this.cls = cls;
    }

    private ActivityDelegate(Class cls, Bundle bundle) {
        this.cls = cls;
        this.bundle = bundle;
    }

    public static ActivityDelegate create(Context context) {
        return new ActivityDelegate(context);
    }

    public static ActivityDelegate create(Class cls) {
        return new ActivityDelegate(cls);
    }

    public static ActivityDelegate create(Class cls, Bundle bundle) {
        return new ActivityDelegate(cls, bundle);
    }

    public ActivityDelegate putString(String key, String value) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putString(key, value);
        return this;
    }

    public ActivityDelegate putInt(String key, int value) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putInt(key, value);
        return this;
    }

    public ActivityDelegate putBoolean(String key, boolean value) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putBoolean(key, value);
        return this;
    }

    public ActivityDelegate putAll(String key, Bundle bundle) {
        if (this.bundle == null) {
            this.bundle = new Bundle();
        }
        this.bundle.putAll(bundle);
        return this;
    }

    public ActivityDelegate transition(int enterAnim, int exitAnim) {
        this.enterAnim = enterAnim;
        this.exitAnim = exitAnim;
        return this;
    }

    public void open(Context context) {
        Intent intent = new Intent(context, cls);
        if (bundle != null && !bundle.isEmpty()) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(enterAnim, exitAnim);
        }
    }

    public void close() {
        if (context != null && context instanceof Activity) {
            Activity activity = (Activity) this.context;
            activity.finish();
            activity.overridePendingTransition(enterAnim, exitAnim);
        }
    }

}
