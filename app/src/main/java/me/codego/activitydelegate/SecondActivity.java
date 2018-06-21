package me.codego.activitydelegate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import me.codego.utils.delegate.PIntent;


public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PIntent.applyScene(this);
        setContentView(R.layout.activity_second);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.size() > 0) {
            ((TextView) findViewById(R.id.text)).setText(extras.getString("key"));
        }
    }

    public void closeWithAnimation(View view) {
        setResult(RESULT_OK, new Intent().putExtra("text", "something with second"));
        PIntent.from(this).finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                PIntent.from(this).finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                PIntent.from(this).finish();
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
