package com.freebrio.robustdemo;

import android.view.View;
import android.widget.Toast;

import com.meituan.robust.patch.annotaion.Add;

/**
 * @author: wangqiang
 * @date: 2024/12/20
 * @desc:
 */
@Add
public class FixOnClick implements View.OnClickListener {


    @Override
    public void onClick(View v) {
        Toast.makeText(v.getContext(), "Haha", Toast.LENGTH_SHORT).show();
    }
}
