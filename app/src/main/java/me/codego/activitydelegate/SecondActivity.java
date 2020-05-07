package me.codego.activitydelegate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import me.codego.delegate.IShareView;
import me.codego.delegate.PIntent;


public class SecondActivity extends AppCompatActivity implements IShareView {

    private Button mCloseBtn;
    boolean isReturn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // PIntent.applyScene(this);
        PIntent.applyShare(this, this);

        setContentView(R.layout.activity_second);
        mCloseBtn = findViewById(R.id.close_btn);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.size() > 0) {
            ((TextView) findViewById(R.id.text)).setText(extras.getString("key"));
        }
    }

    @Override
    public void finishAfterTransition() {
        isReturn = true;
        PIntent.finishAfterTransition(this, this);
        super.finishAfterTransition();
    }

    @Override
    public View[] getShareViews() {
        String transitionName = "share";
        if (isReturn) {
            transitionName = "icon";
        }
        ViewCompat.setTransitionName(mCloseBtn, transitionName);

        return new View[]{mCloseBtn};
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
                ActivityCompat.finishAfterTransition(this);
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
