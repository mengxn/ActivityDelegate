package me.codego.activitydelegate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import me.codego.adapter.SingleAdapter;
import me.codego.adapter.ViewHolder;
import me.codego.delegate.IRequest;
import me.codego.delegate.IShareViewSelector;
import me.codego.delegate.PIntent;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_OPEN_SECOND = 1;

    private ImageView mIconView;
    private Button mOpenWithAnimView;
    private RecyclerView mImageRv;
    private SingleAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIconView = findViewById(R.id.icon_img);
        mOpenWithAnimView = findViewById(R.id.share_btn);

        initImageList();
    }

    public void openSecond(View view) {
        PIntent.from(this)
                .to(SecondActivity.class);
    }

    /**
     * 打开新界面，附带参数
     */
    public void openSecondWithExtra(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("key", "hello world from last view");
        PIntent.from(this)
                .with("num", 1)
                .with(bundle)
                .to(SecondActivity.class);
    }

    /**
     * 通过Action打开新界面
     */
    public void openSecondWithAction(View view) {
        PIntent.from(this)
                .to("me.codego.activitydelegate.action.SECOND");
    }

    /**
     * 打开新界面，过渡动画
     */
    public void openSecondWithAnimation(View view) {
        PIntent.from(this)
                .transition(R.anim.slide_in_right, R.anim.fade_out)
                .to(SecondActivity.class);
    }

    /**
     * 打开新界面，元素场景动画
     */
    public void openSecondWithShareView(View view) {
        PIntent.from(this)
                .with("key", "open with share view")
                .share(view, "button")
                .to(SecondActivity.class);
    }

    /**
     * 打开新界面，处理回调数据
     */
    public void openSecondWithCallback(View view) {
        PIntent.from(this)
                .to(SecondActivity.class, REQUEST_OPEN_SECOND)
                .result(new IRequest.Callback() {
                    @Override
                    public void onResult(Intent data) {
                        Toast.makeText(MainActivity.this, data.getStringExtra("text"), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 初始化图片列表
     */
    private void initImageList() {
        mImageRv = findViewById(R.id.image_rv);
        mImageRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new SingleAdapter<>(R.layout.item_image, new Function1<ViewHolder, Unit>() {
            @Override
            public Unit invoke(ViewHolder holder) {
                ImageView imageIv = (ImageView) holder.getView(R.id.image_iv);
                ViewCompat.setTransitionName(imageIv, "image" + holder.getAdapterPosition());
                Glide.with(imageIv).load(holder.getData()).into(imageIv);
                imageIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openImage(v, holder.getAdapterPosition());
                    }
                });
                return Unit.INSTANCE;
            }
        });
        List<String> urls = new ArrayList<>();
        urls.add("https://img1.baidu.com/it/u=1806802992,3589095425&fm=26&fmt=auto&gp=0.jpg");
        urls.add("https://img0.baidu.com/it/u=3696076445,722539973&fm=26&fmt=auto&gp=0.jpg");
        urls.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fww3.sinaimg.cn%2Fmw690%2F006mOIKmgy1gshcm7iax3j30sf0m2att.jpg&refer=http%3A%2F%2Fwww.sina.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1631952514&t=cf2285389fcd75d2d3bf5ea57dfd2e58");
        urls.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fsc01.alicdn.com%2Fkf%2FHTB1qE0_PVXXXXXBXXXXq6xXFXXXv%2FWild-animal-design-resin-panda-statue-for.jpg_300x300.jpg&refer=http%3A%2F%2Fsc01.alicdn.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1631952514&t=f182b6887eb4efb83f22c60be1add23b");
        mAdapter.setData(urls);
        mImageRv.setAdapter(mAdapter);
    }
    /**
     * 使用某一元素开启场景动画
     */
    private void openImage(View view, int position) {
        PIntent.from(this)
                .with("image_list", mAdapter.getData())
                .with("position", position)
                .share(view, "image")
                .to(ImagePreviewActivity.class);
    }

    /**
     * 处理回调元素动画
     */
    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        PIntent.onActivityReenter(this, resultCode, data, new IShareViewSelector() {
            @Override
            public View[] selectShareView(List<String> transitionNameList) {
                View[] views = new View[transitionNameList.size()];
                for (int i = 0; i < transitionNameList.size(); i++) {
                    switch (transitionNameList.get(i)) {
                        case "icon":
                            views[i] = mIconView;
                            break;
                        case "button":
                            views[i] = mOpenWithAnimView;
                            break;
                        case "image":
                            int position = data.getIntExtra("position", 0);
                            views[i] = mImageRv.getChildAt(position);
                            break;
                        default:
                            break;
                    }
                }
                return views;
            }
        });
    }

}
