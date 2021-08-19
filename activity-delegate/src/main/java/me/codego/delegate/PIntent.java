package me.codego.delegate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.ViewCompat;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;

import java.util.List;
import java.util.Map;

/**
 * @author mengxn 2017/6/20
 */

public class PIntent {

    protected static final String KEY_TRANSITION = "key_transition";

    public static IRequest from(Context context) {
        if (context instanceof FragmentActivity) {
            return new IntentRequest((FragmentActivity) context);
        }
        throw new IllegalArgumentException("context should extend FragmentActivity");
    }

    /**
     * 是否支持场景动画
     */
    private static boolean isSupportShare() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * 执行当前界面场景动画
     *
     * @param activity activity
     */
    public static void startTransition(Activity activity) {
        if (!isSupportShare()) {
            return;
        }
        Intent intent = activity.getIntent();
        if (intent != null && intent.hasExtra(IntentRequest.ANIMATION_SCENE)) {
            Window window = activity.getWindow();
            window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            int resId = intent.getIntExtra(IntentRequest.ANIMATION_SCENE, 0);
            if (resId != 0) {
                Transition transition = TransitionInflater.from(activity).inflateTransition(resId);
                window.setEnterTransition(transition);
            }
        }
    }

    /**
     * 执行当前界面场景动画
     *
     * @param activity
     * @param selector 选择动画元素
     */
    public static void startTransition(final Activity activity, final IShareViewSelector selector) {
        if (!isSupportShare()) {
            return;
        }
        activity.getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        ActivityCompat.setEnterSharedElementCallback(activity, new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                sharedElements.clear();

                final View[] views = selector.selectShareView(names);
                for (int i = 0; i < names.size(); i++) {
                    sharedElements.put(names.get(i), views[i]);
                }
            }
        });
    }

    /**
     * 延迟执行当前界面场景动画
     *
     * @param activity
     * @param selector 选择动画元素
     */
    public static void postStartTransition(final Activity activity, final IShareViewSelector selector) {
        if (!isSupportShare()) {
            return;
        }
        activity.getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        ActivityCompat.setEnterSharedElementCallback(activity, new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                final View[] views = selector.selectShareView(names);
                if (views == null || views.length == 0) {
                    return;
                }
                sharedElements.clear();
                for (int i = 0; i < names.size() && i < views.length; i++) {
                    if (views[i] == null) {
                        continue;
                    }
                    ViewCompat.setTransitionName(views[i], names.get(i));
                    sharedElements.put(names.get(i), views[i]);
                }
            }
        });
        ActivityCompat.postponeEnterTransition(activity);
        final View decor = activity.getWindow().getDecorView();
        decor.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                decor.getViewTreeObserver().removeOnPreDrawListener(this);
                ActivityCompat.startPostponedEnterTransition(activity);
                return true;
            }
        });
    }

    /**
     * 页面重新加入动画
     *
     * @param activity
     * @param resultCode
     * @param data
     * @param selector
     */
    public static void onActivityReenter(final Activity activity, int resultCode, Intent data, final IShareViewSelector selector) {
        if (!isSupportShare()) {
            return;
        }
        if (resultCode == Activity.RESULT_OK && data.getBooleanExtra(KEY_TRANSITION, false)) {

            // postpone transition
            ActivityCompat.postponeEnterTransition(activity);

            // set listener and map share elements
            ActivityCompat.setExitSharedElementCallback(activity, new SharedElementCallback() {
                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                    final View[] views = selector.selectShareView(names);
                    if (views == null || views.length == 0) {
                        return;
                    }
                    sharedElements.clear();
                    for (int i = 0; i < names.size() && i < views.length; i++) {
                        if (views[i] == null) {
                            continue;
                        }
                        ViewCompat.setTransitionName(views[i], names.get(i));
                        sharedElements.put(names.get(i), views[i]);
                    }
                }
            });

            activity.getWindow().getDecorView().getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    activity.getWindow().getDecorView().getViewTreeObserver().removeOnPreDrawListener(this);
                    ActivityCompat.startPostponedEnterTransition(activity);
                    // reset listener
                    ActivityCompat.setExitSharedElementCallback(activity, null);
                    return false;
                }
            });
        }
    }

    public static class Config {

        private Context context;

        /**
         * 动画参数
         */
        Bundle openAnimOptions;
        Bundle closeAnimOptions;

        private Config(Context context) {
            this.context = context.getApplicationContext();
        }

        public static Config newConfig(Context context) {
            return new Config(context);
        }

        public Config transition(int openEnterResId, int openExitResId, int closeEnterResId, int closeExitResId) {
            openAnimOptions = ActivityOptionsCompat.makeCustomAnimation(context, openEnterResId, openExitResId).toBundle();
            closeAnimOptions = ActivityOptionsCompat.makeCustomAnimation(context, closeEnterResId, closeExitResId).toBundle();
            return this;
        }

        /**
         * apply config
         */
        public void apply() {
            IntentRequest.mConfig = this;
        }
    }

}
