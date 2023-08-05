package com.xdf.studypatch.downloader

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Environment


/**
@author: wangqiang
@date: 2023/8/5
@desc:
 */
class StudyDownloader(val context: Context, val callback: IDownloadCallback) {

    private val downloadManager: DownloadManager =
        context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager

    private val filter: IntentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)


    // 监听下载结束，启用BroadcastReceiver
    var receiver: BroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            val dm = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val action: String? = intent.action

            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
                val downloadId: Long = intent.getLongExtra(
                    DownloadManager.EXTRA_DOWNLOAD_ID, 0
                )
                // 查询
                val query = DownloadManager.Query()
                query.setFilterById(downloadId)

                val c: Cursor = dm.query(query)
                if (c.moveToFirst()) {
                    val columnIndex: Int = c
                        .getColumnIndex(DownloadManager.COLUMN_STATUS)
                    if (DownloadManager.STATUS_SUCCESSFUL == c
                            .getInt(columnIndex)
                    ) {
                        //下载成功
                        callback.onDownloadSuccess("")
                    } else {
                        //下载失败
                        callback.onDownloadError()
                    }
                }
            }
        }
    }

    init {
        context.registerReceiver(receiver, filter)
    }


    fun startDownload(url: String, destPath: String) {
        val request = DownloadManager.Request(
            Uri.parse(url)
        )

        request.setAllowedNetworkTypes(
            DownloadManager.Request.NETWORK_MOBILE
                    or DownloadManager.Request.NETWORK_WIFI
        )
            .setAllowedOverRoaming(false) // 缺省是true
            .setTitle("更新") // 用于信息查看
            .setDescription("下载patch") // 用于信息查看
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS, "patch.jar"

            )
        val mDownloadId = downloadManager.enqueue(request) // 加入下载队列
    }
}