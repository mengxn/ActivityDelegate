package me.codego.activitydelegate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

import me.codego.delegate.IRequest;
import me.codego.delegate.IShareViewSelector;
import me.codego.delegate.PIntent;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_OPEN_SECOND = 1;

    private ImageView mIconView;
    private Button mOpenWithAnimView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIconView = findViewById(R.id.icon_img);
        mOpenWithAnimView = findViewById(R.id.share_btn);

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
                .transition(R.anim.slide_in_right, R.anim.fade_out)
                .to(SecondActivity.class);
    }

    public void openSecondWithAction(View view) {
        PIntent.from(this)
                .transition(R.anim.slide_in_right, R.anim.fade_out)
                .to("me.codego.activitydelegate.action.SECOND");
    }

    private boolean isReturn = false;

    public void openSecondWithShareView(View view) {
        // isReturn = false;
        isReturn = false;

        PIntent.from(this)
                .with("key", "open with share view")
                .share(view, "button")
                .to(SecondActivity.class);
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);

        PIntent.onActivityReenter(this, resultCode, data, new IShareViewSelector() {
            @Override
            public View[] selectShareView(List<String> shareList) {
                View[] views = new View[shareList.size()];
                for (int i = 0; i < shareList.size(); i++) {
                    switch (shareList.get(i)) {
                        case "icon":
                            views[i] = mIconView;
                            break;
                        case "button":
                            views[i] = mOpenWithAnimView;
                        default:
                            break;
                    }
                }
                return views;
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

}
