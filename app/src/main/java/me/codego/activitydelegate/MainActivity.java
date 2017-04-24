package me.codego.activitydelegate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import me.codego.utils.ActivityDelegate;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openSecond(View view) {
        ActivityDelegate.from(this)
                .to(SecondActivity.class);
    }

    public void openSecondWithExtra(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("key", "hello world from last view");
        ActivityDelegate.from(this)
                .with(bundle)
                .to(SecondActivity.class);
    }

    public void openSecondWithAnimation(View view) {
        ActivityDelegate.from(this)
                .transition(R.anim.slide_in_right, R.anim.slide_out_left)
                .to(SecondActivity.class);
    }
}
