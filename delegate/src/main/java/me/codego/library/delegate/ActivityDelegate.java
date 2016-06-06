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

    private int enterAnim;
    private int exitAnim;

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

    public ActivityDelegate transition(int enterAnim, int exitAnim) {
        this.enterAnim = enterAnim;
        this.exitAnim = exitAnim;
        return this;
    }

    public void open(Context context) {
        Intent intent = new Intent(context, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
        if (context instanceof Activity && enterAnim > 0 && exitAnim > 0) {
            ((Activity) context).overridePendingTransition(enterAnim, exitAnim);
        }
    }

    public void close() {
        if (context != null && context instanceof Activity) {
            Activity activity = (Activity) this.context;
            activity.finish();
            if (enterAnim > 0 && exitAnim > 0) {
                activity.overridePendingTransition(enterAnim, exitAnim);
            }
        }
    }

}
