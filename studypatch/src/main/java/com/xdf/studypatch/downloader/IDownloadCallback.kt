package com.xdf.studypatch.downloader

/**
@author: wangqiang
@date: 2023/8/5
@desc: 下载回调
 */
interface IDownloadCallback {

    /**
     * 开始下载
     */
    fun onDownloadStart()

    /**
     * 下载成功
     */
    fun onDownloadSuccess(patchPath: String)

    /**
     * 下载失败
     */
    fun onDownloadError()
}