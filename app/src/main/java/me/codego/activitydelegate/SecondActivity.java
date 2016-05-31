package me.codego.activitydelegate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import me.codego.library.delegate.ActivityDelegate;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.size() > 0) {
            ((TextView) findViewById(R.id.text)).setText(extras.getString("key"));
        }
    }

    public void closeWithAnimation(View view) {
        ActivityDelegate.create(this).transition(R.anim.slide_in_left, R.anim.slide_out_right).close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ActivityDelegate.create(this).close();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                ActivityDelegate.create(this).close();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
