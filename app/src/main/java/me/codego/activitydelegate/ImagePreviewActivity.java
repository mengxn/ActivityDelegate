package me.codego.activitydelegate;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import me.codego.delegate.IShareViewSelector;
import me.codego.delegate.PIntent;

/**
 * @author mengxn
 * @date 2021/8/19
 */
public class ImagePreviewActivity extends AppCompatActivity implements IShareViewSelector {

    private ViewPager mImagePager;
    private ImageAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 延迟执行场景动画
        PIntent.postStartTransition(this, this);

        setContentView(R.layout.activity_image_preview);
        mImagePager = findViewById(R.id.image_pager);
        List<String> imageList = getIntent().getStringArrayListExtra("image_list");
        mAdapter = new ImageAdapter(getSupportFragmentManager(), imageList);
        mImagePager.setAdapter(mAdapter);
        int position = getIntent().getIntExtra("position", 0);
        mImagePager.setCurrentItem(position);
    }

    @Override
    public View[] selectShareView(List<String> transitionNameList) {
        return new View[]{mImagePager};
    }

    @Override
    public void onBackPressed() {
        PIntent.from(this)
                .with("position", mImagePager.getCurrentItem())
                .share(mImagePager, "image")
                .finish();
    }

    public static class ImageAdapter extends FragmentPagerAdapter {

        private List<String> mImageList;

        public ImageAdapter(FragmentManager fm, List<String> imageList) {
            super(fm);
            mImageList = new ArrayList<>();
            mImageList.addAll(imageList);
        }

        @Override
        public Fragment getItem(int i) {
            return ImageFragment.create(mImageList.get(i));
        }


        @Override
        public int getCount() {
            return mImageList.size();
        }
    }

    public static class ImageFragment extends Fragment {

        public static ImageFragment create(String url) {
            ImageFragment fragment = new ImageFragment();
            Bundle bundle = new Bundle();
            bundle.putString("url", url);
            fragment.setArguments(bundle);
            return fragment;
        }

        private String mUrl;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return new ImageView(container.getContext());
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mUrl = getArguments().getString("url");
            Glide.with(view).load(mUrl).into((ImageView) view);
        }
    }
}
