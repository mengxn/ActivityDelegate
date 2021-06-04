package me.codego.activitydelegate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import me.codego.delegate.IRequest;
import me.codego.delegate.IShareSelector;
import me.codego.delegate.IShareView;
import me.codego.delegate.PIntent;


public class MainActivity extends AppCompatActivity implements IShareView {

    private static final int REQUEST_OPEN_SECOND = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openSecond(View view) {
        PIntent.from(this)
                .to(SecondActivity.class);
    }

    public void openSecondWithExtra(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("key", "hello world from last view");
        PIntent.from(this)
                .with("num", 1)
                .with(bundle)
                .to(SecondActivity.class);
    }

    public void openSecondWithAnimation(View view) {
        PIntent.from(this)
                .transition(R.anim.slide_in_right, R.anim.slide_out_left)
                .to(SecondActivity.class);
    }

    public void openSecondWithAction(View view) {
        PIntent.from(this)
                .transition(R.anim.slide_in_right, R.anim.slide_out_left)
                .to("me.codego.activitydelegate.action.SECOND");
    }

    private boolean isReturn = false;

    public void openSecondWithShareView(View view) {
        // isReturn = false;
        isReturn = false;

        PIntent.from(this)
                .with("key", "open with share view")
                .share(view, "")
                .to(SecondActivity.class);
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);

        PIntent.onActivityReenter(this, resultCode, data, new IShareSelector() {
            @Override
            public View[] onShare(List<String> shareList) {
                if ("icon".equals(shareList.get(0))) {
                    isReturn = true;
                } else {
                    isReturn = false;
                }
                return new View[0];
            }
        });
    }

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

    @Override
    public View[] getShareViews() {
        if (isReturn) {
            return new View[]{findViewById(R.id.icon_img)};
        }
        return new View[]{findViewById(R.id.share_btn)};
    }
}
