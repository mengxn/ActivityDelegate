package me.codego.PIntent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import me.codego.activitydelegate.R;
import me.codego.activitydelegate.SecondActivity;
import me.codego.utils.PIntent;


public class MainActivity extends AppCompatActivity {

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
}
