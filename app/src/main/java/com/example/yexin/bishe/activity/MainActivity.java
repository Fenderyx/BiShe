package com.example.yexin.bishe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.yexin.bishe.R;
import com.example.yexin.bishe.base.BasePager;
import com.example.yexin.bishe.fragment.ContentFragment;
import com.example.yexin.bishe.pagers.MusicPager;
import com.example.yexin.bishe.pagers.NetMusicPager;
import com.example.yexin.bishe.pagers.NetVideoPager;
import com.example.yexin.bishe.pagers.VideoPager;

import java.util.ArrayList;

import static com.example.yexin.bishe.pagers.MusicPager.REQUESTCODE_PERMISSION_READ_EXTERNAL_STORAGE;

public class MainActivity extends FragmentActivity {
    private RadioGroup rg_tab;
    private static final String TAG = "MainActivity";
    private static final String APPQ = "com.example.yexin.bishe.appquit";
    //子页面的集合
    private ArrayList<BasePager> basePagers;

    public int position = 0;
    private FragmentManager fm;
    private boolean isExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fm = getSupportFragmentManager();

        rg_tab = (RadioGroup) findViewById(R.id.rg_tab);

        basePagers = new ArrayList<>();
        basePagers.add(new MusicPager(this));

        basePagers.add(new VideoPager(this));
        basePagers.add(new NetVideoPager(this));
        basePagers.add(new NetMusicPager(this));

        //设置RadioGroup监听  切换页面
        rg_tab.setOnCheckedChangeListener(new RadioGroupListener());
        //设置默认页面
        rg_tab.check(R.id.radio_bdyy);
    }


    private class RadioGroupListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            switch (checkedId) {
                default:
                    position = 0;
                    break;
                case R.id.radio_bdsp:
                    position = 1;
                    break;
                case R.id.radio_wlsp:
                    position = 2;
                    break;
                case R.id.radio_wlyy:
                    position = 3;
                    break;
            }

            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fl_main, new ContentFragment(basePagers, position));
            ft.commit();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (position != 0) {
                position = 0;
                rg_tab.check(R.id.radio_bdyy);
                return true;
            } else if (!isExit) {
                isExit = true;
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                }, 2000);

                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 处理请求权限结果
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUESTCODE_PERMISSION_READ_EXTERNAL_STORAGE) {
            Log.d(TAG, "onActivityResult: " + grantResults.toString());

        }
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(APPQ);
        sendBroadcast(intent);
        super.onDestroy();
    }
}
