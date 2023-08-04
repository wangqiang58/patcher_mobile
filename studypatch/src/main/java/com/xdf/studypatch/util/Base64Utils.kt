package com.xdf.studypatch.util

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import android.util.Base64

object Base64Utils {
    private const val DEFAULT_FLAG = Base64.DEFAULT or Base64.NO_WRAP

    @NonNull
    fun encodeBase64(bytes: ByteArray?): String {
        return encodeBase64(bytes, DEFAULT_FLAG)
    }

    @NonNull
    fun encodeBase64(bytes: ByteArray?, flag: Int): String {
        if (bytes == null || bytes.isEmpty()) {
            return ""
        }
        return String(Base64.encode(bytes, flag))
    }

    @NonNull
    fun encodeBase64(str: String?, flag: Int): String {
        return encodeBase64(str?.toByteArray(), flag)
    }

    @NonNull
    fun encodeBase64(str: String?): String {
        return encodeBase64(str, DEFAULT_FLAG)
    }

    @NonNull
    fun decodeBase64(str: String?): String {
        return decodeBase64(str, DEFAULT_FLAG)
    }

    @NonNull
    fun decodeBase64(str: String?, flag: Int): String {
        if (str == null || str.isEmpty()) {
            return ""
        }
        return String(Base64.decode(str, flag))
    }

    @Nullable
    fun decodeBase64ToBytes(str: String?): ByteArray? {
        return decodeBase64ToBytes(str, DEFAULT_FLAG)
    }

    @Nullable
    fun decodeBase64ToBytes(str: String?, flag: Int): ByteArray? {
        if (str == null || str.isEmpty()) {
            return null
        }
        return Base64.decode(str, flag)
    }
}