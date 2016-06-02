package me.codego.activitydelegate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import me.codego.library.delegate.ActivityDelegate;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openSecond(View view) {
        ActivityDelegate.create(SecondActivity.class).open(this);
    }

    public void openSecondWithExtra(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("key", "hello world from last view");
        ActivityDelegate.create(SecondActivity.class, bundle).open(this);

        ActivityDelegate.create(SecondActivity.class)
                .putString("key", "some value")
                .open(this);
    }

    public void openSecondWithAnimation(View view) {
        ActivityDelegate.create(SecondActivity.class)
                .transition(R.anim.slide_in_right, R.anim.slide_out_left)
                .open(this);
    }
}
