package me.codego.activitydelegate;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.codego.delegate.IShareViewSelector;
import me.codego.delegate.PIntent;


public class SecondActivity extends AppCompatActivity implements IShareViewSelector {

    private ImageView mIconIv;
    private Button mCloseBtn;
    boolean isReturn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // PIntent.applyScene(this);
        // PIntent.applyShare(this, this);
        PIntent.postStartTransition(this, this);

        setContentView(R.layout.activity_second);
        mCloseBtn = findViewById(R.id.close_btn);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.size() > 0) {
            ((TextView) findViewById(R.id.text)).setText(extras.getString("key"));
        }
    }

    @Override
    public View[] selectShareView(List<String> transitionNameList) {
        View[] views = new View[transitionNameList.size()];
        for (int i = 0; i < transitionNameList.size(); i++) {
            switch (transitionNameList.get(i)) {
                case "button":
                    views[i] = mCloseBtn;
                    break;
                case "icon":
                    views[i] = mIconIv;
                    break;
                default:
                    break;
            }
        }
        return views;
    }

    public void closeWithAnimation(View view) {
        PIntent.from(this)
                .share(view, "button")
                .finish();
    }

    public void closeIconWithAnim(View view) {
        PIntent.from(this)
                .share(view, "icon")
                .finish();
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
