package me.codego.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;

/**
 * Created by mengxn on 2017/6/20.
 */

public class ActivityRequest extends IntentRequest {

    private Activity mActivity;

    private static final String KEY_ANIM_ENTER = "android:activity.animEnterRes";
    private static final String KEY_ANIM_EXIT = "android:activity.animExitRes";

    public ActivityRequest(Activity activity) {
        super(activity);
        mActivity = activity;
    }

    @Override
    ActivityResponse startActivityForResult(Intent intent, int requestCode, Bundle options) {
        ActivityCompat.startActivityForResult(mActivity, intent, requestCode, options);

        //16以下没有动画，需要使用overridePendingTransition
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            if (options != null) {
                mActivity.overridePendingTransition(options.getInt(KEY_ANIM_ENTER), options.getInt(KEY_ANIM_EXIT));
            }
        }
        return ActivityResponse.createWithCode(requestCode);
    }

    @Override
    Bundle makeSceneTransitionAnimation(Pair<View, String>... sharedElements) {
        return ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, sharedElements).toBundle();
    }

    @Override
    public void finish() {
        ActivityCompat.finishAfterTransition(mActivity);
    }
}
