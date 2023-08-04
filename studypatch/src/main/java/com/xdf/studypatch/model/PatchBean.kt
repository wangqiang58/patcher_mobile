package com.xdf.studypatch.model

import com.google.gson.annotations.SerializedName

/**
@author: wangqiang
@date: 2023/8/4
@desc: 热修复补丁包的数据结构
 */
data class PatchBean (/**
                   * 补丁版本号
                   *
                   * 是补丁包的唯一标识
                   */
                  @SerializedName("name")
                  val patchVersion: String?,

                      /**
                   * MD5校验签名
                   */
                  @SerializedName("md5")
                  val signed: String?,
                      /**
                   * patch的下载地址
                   */
                  @SerializedName("cosUrl")
                  val patchUrl: String? = null
)