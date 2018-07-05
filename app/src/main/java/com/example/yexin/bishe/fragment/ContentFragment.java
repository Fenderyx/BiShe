package com.example.yexin.bishe.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yexin.bishe.base.BasePager;
import com.example.yexin.bishe.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by yexin on 2018/5/2.
 */

public class ContentFragment extends android.support.v4.app.Fragment {

    private ArrayList<BasePager> basePagers;
    private int position;

    public ContentFragment() {
    }

    @SuppressLint("ValidFragment")
    public ContentFragment(ArrayList<BasePager> basePagers, int position) {
        this.basePagers = basePagers;
        this.position = position;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        BasePager basePager = basePagers.get(position);
        if (basePager != null) {
            if (!basePager.isInitData) {
                basePager.initData();
                if (position != 3) {
                    basePager.isInitData = true;
                }
            }
            return basePager.rootView;
        }
        return null;
    }

}
