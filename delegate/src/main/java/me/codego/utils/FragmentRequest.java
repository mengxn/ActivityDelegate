package me.codego.utils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by mengxn on 2017/6/20.
 */
public class FragmentRequest extends IntentRequest {

    private Fragment mFragment;

    FragmentRequest(Fragment fragment) {
        super(fragment.getContext());
        mFragment = fragment;
    }

    @Override
    ActivityResponse startActivityForResult(Intent intent, int requestCode, Bundle options) {
        mFragment.startActivityForResult(intent, requestCode, options);
        return ActivityResponse.createWithCode(requestCode);
    }

    @Override
    Bundle makeSceneTransitionAnimation(View sharedElement, String sharedElementName) {
        return ActivityOptionsCompat.makeSceneTransitionAnimation(mFragment.getActivity(), sharedElement, sharedElementName).toBundle();
    }

    @Override
    public void finish() {
        ActivityCompat.finishAfterTransition(mFragment.getActivity());
    }
}
