package me.codego.activitydelegate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import me.codego.delegate.IRequest;
import me.codego.delegate.PIntent;


public class MainActivity extends AppCompatActivity {

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

    public void openSecondWithShareView(View view) {
        PIntent.from(this)
                .with("key", "open with share view")
                .share(view, "share")
                .to(SecondActivity.class);
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
