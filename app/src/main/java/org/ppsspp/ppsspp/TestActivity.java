package org.ppsspp.ppsspp;

import android.app.Activity;
import android.os.Bundle;

import com.xiaolu.psp9100220.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jalen on 2016/8/10.
 */
public class TestActivity extends Activity {
    private CircleProgress circleProgress;
    private Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_test);

        circleProgress = (CircleProgress) this.findViewById(R.id.test_progress);
        circleProgress.setMax(100);
        circleProgress.setProgress(0);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        circleProgress.setProgress(circleProgress.getProgress() + 1);
                    }
                });
            }
        }, 1000, 100);
    }
}
