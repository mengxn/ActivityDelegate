package me.codego.delegate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author mengxn 2017/6/20
 */

public class PIntent {

    private static final boolean isSupportShare = Build.VERSION.SDK_INT >= 21;
    private static final String KEY_TRANSITION_NAME = "key_transition_name";

    public static IRequest from(Context context) {
        if (context instanceof FragmentActivity) {
            return new IntentRequest((FragmentActivity) context);
        }
        throw new IllegalArgumentException("context should extend FragmentActivity");
    }

    /**
     * 执行当前界面场景动画
     *
     * @param activity activity
     */
    public static void applyScene(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = activity.getIntent();
            if (intent != null && intent.hasExtra(IntentRequest.ANIMATION_SCENE)) {
                Window window = activity.getWindow();
                window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
                int resId = intent.getIntExtra(IntentRequest.ANIMATION_SCENE, 0);
                if (resId > 0) {
                    Transition transition = TransitionInflater.from(activity).inflateTransition(resId);
                    window.setEnterTransition(transition);
                }
            }
        }
    }

    public static void applyShare(final Activity activity, final IShareView shareView) {
        if (!isSupportShare) {
            return;
        }
        activity.getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        // ActivityCompat.setEnterSharedElementCallback(activity, new SharedElementCallback() {
        //     @Override
        //     public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
        //         names.clear();
        //         sharedElements.clear();
        //
        //         final View[] shareViews = shareView.getShareViews();
        //         for (View view : shareViews) {
        //             final String transitionName = ViewCompat.getTransitionName(view);
        //             if (!TextUtils.isEmpty(transitionName)) {
        //                 names.add(transitionName);
        //                 sharedElements.put(transitionName, view);
        //             }
        //         }
        //     }
        // });
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

    public static void finishAfterTransition(final Activity activity, final IShareView shareView) {
        if (!isSupportShare) {
            return;
        }
        ActivityCompat.setEnterSharedElementCallback(activity, new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                names.clear();
                sharedElements.clear();

                final View[] shareViews = shareView.getShareViews();
                for (View view : shareViews) {
                    final String transitionName = ViewCompat.getTransitionName(view);
                    if (!TextUtils.isEmpty(transitionName)) {
                        names.add(transitionName);
                        sharedElements.put(transitionName, view);
                    }
                }
            }
        });
        View[] shareElements = shareView == null ? null : shareView.getShareViews();
        if (shareElements != null && shareElements.length > 0) {
            ArrayList<String> list = new ArrayList<>();
            for (View view : shareElements) {
                list.add(ViewCompat.getTransitionName(view));
            }
            Intent intent = new Intent();
            intent.putStringArrayListExtra(KEY_TRANSITION_NAME, list);
            activity.setResult(Activity.RESULT_OK, intent);
        }
    }

    public static void onActivityReenter(final Activity activity, int resultCode, Intent data, final IShareSelector selector) {
        if (!isSupportShare) {
            return;
        }
        if (resultCode == Activity.RESULT_OK && data.hasExtra(KEY_TRANSITION_NAME)) {

            // postpone transition
            ActivityCompat.postponeEnterTransition(activity);

            // fetch transition name from last page
            final ArrayList<String> shareElementsList = data.getStringArrayListExtra(KEY_TRANSITION_NAME);

            // set listener and map share elements
            ActivityCompat.setExitSharedElementCallback(activity, new SharedElementCallback() {
                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                    final View[] shareViews = selector.onShare(shareElementsList);
                    if (shareViews != null && shareViews.length > 0) {
                        names.clear();
                        sharedElements.clear();
                        for (View view : shareViews) {
                            final String transitionName = ViewCompat.getTransitionName(view);
                            if (!TextUtils.isEmpty(transitionName)) {
                                names.add(transitionName);
                                sharedElements.put(transitionName, view);
                            }
                        }
                    }
                }
            });

            activity.getWindow().getDecorView().getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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

        private int enterResId, exitResId;

        /**
         * 动画参数
         */
        Bundle options;

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
            IntentRequest.defaultConfig = this;
            options = ActivityOptionsCompat.makeCustomAnimation(context, enterResId, exitResId).toBundle();
        }
    }

}
