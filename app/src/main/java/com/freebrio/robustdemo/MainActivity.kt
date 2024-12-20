package com.freebrio.robustdemo

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.meituan.robust.patch.RobustModify
import com.meituan.robust.patch.annotaion.Add
import com.meituan.robust.patch.annotaion.Modify

class MainActivity : AppCompatActivity() {
    lateinit var textView: TextView


    @Modify
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.content_tv)
        findViewById<Button>(R.id.btn).setOnClickListener(FixOnclick())
    }

    @Add
     class FixOnclick():OnClickListener{
        override fun onClick(v: View?) {
            System.out.println("xxxxx11111")
        }

    }

    public fun setText() {
        findViewById<Button>(R.id.btn).text = "修改前1"
    }
}