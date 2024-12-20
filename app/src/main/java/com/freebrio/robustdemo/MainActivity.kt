package com.freebrio.robustdemo

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.meituan.robust.Patch
import com.meituan.robust.PatchExecutor
import com.meituan.robust.RobustCallBack
import com.meituan.robust.patch.RobustModify
import com.meituan.robust.patch.annotaion.Add
import com.meituan.robust.patch.annotaion.Modify

class MainActivity : AppCompatActivity() {
    lateinit var textView: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.content_tv)

        findViewById<Button>(R.id.load_pathch).setOnClickListener {
            loadPatch()
        }

        findViewById<Button>(R.id.btn).setOnClickListener { setText() }
    }

    @Modify
    private fun setText() {
        findViewById<Button>(R.id.btn).text = "修改后1"
    }

    private fun loadPatch() {
        PatchExecutor(this, PatchManipulateImp(), object : RobustCallBack {
            override fun onPatchListFetched(
                result: Boolean,
                isNet: Boolean,
                patches: MutableList<Patch>?
            ) {

            }

            override fun onPatchFetched(result: Boolean, isNet: Boolean, patch: Patch?) {
                Log.d("swt", "onPatchFetched")
            }

            override fun onPatchApplied(result: Boolean, patch: Patch?) {
                Log.d("swt", "onPatchApplied")
            }

            override fun logNotify(log: String?, where: String?) {
                Log.d("swt", "logNotify")

            }

            override fun exceptionNotify(throwable: Throwable?, where: String?) {
                Log.d("swt", "exceptionNotify")

            }

        }).start()
    }


}