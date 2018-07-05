package com.example.yexin.bishe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.example.yexin.bishe.R;

    public class LauncherActivity extends Activity {

    private Handler launcherActivityHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        launcherActivityHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goToMainActivity();
            }
        }, 3000);
    }

    public void toMainActivity(View view) {
        if (view.getId() == R.id.launcher_skip) {
            goToMainActivity();
        }
    }

    /**
     * 打开初始页面，并且关闭启动页面
     */
    private void goToMainActivity() {
        Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        launcherActivityHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
