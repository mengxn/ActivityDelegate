package me.codego.utils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.View;

/**
 * Created by mengxn on 2017/6/20.
 */
public class FragmentRequest extends IntentRequest {

    private Fragment mFragment;

    public FragmentRequest(Fragment fragment) {
        super(fragment.getContext());
        mFragment = fragment;
    }

    @Override
    ActivityResponse startActivityForResult(Intent intent, int requestCode, Bundle options) {
        mFragment.startActivityForResult(intent, requestCode, options);
        return ActivityResponse.createWithCode(requestCode);
    }

    @Override
    Bundle makeSceneTransitionAnimation(Pair<View, String>... sharedElements) {
        return ActivityOptionsCompat.makeSceneTransitionAnimation(mFragment.getActivity(), sharedElements).toBundle();
    }

    @Override
    public void finish() {
        ActivityCompat.finishAfterTransition(mFragment.getActivity());
    }
}
