package com.xiaoxiaoying.spider.http

import com.xiaoxiaoying.spider.utils.gson.GsonUtil.toJson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/**
 * create time 2022/11/18
 * @author xiaoxiaoying
 */
object OkHttpHelper {
    private const val READ_TIMEOUT = 10L
    private const val WRITE_TIMEOUT = 10L
    private const val CONNECT_TIMEOUT = 10L
    val jsonMediaType = "application/json; charset=utf-8".toMediaType()
    fun getOkHttpClientBuilder(): OkHttpClient.Builder = OkHttpClient.Builder()
        .addInterceptor {
            Interceptor.interceptor(it)
        }
        .sslSocketFactory(
            SSLSocketFactoryCompat(),
            SSLSocketFactoryCompat.xManager
        )
        .hostnameVerifier(SSLSocketFactoryCompat.getHostnameVerifier())
        .readTimeout(READ_TIMEOUT, TimeUnit.MINUTES)
        .writeTimeout(WRITE_TIMEOUT, TimeUnit.MINUTES)
        .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MINUTES)


    @JvmStatic
    fun String.getRequest(
        method: Int = Method.POST,
        mediaType: MediaType = jsonMediaType,
        headers: HashMap<String, Any> = hashMapOf(),
        paramsMap: HashMap<String, Any> = hashMapOf()
    ): Request {
        val requestBuilder = Request.Builder()
        when (method) {
            Method.GET -> {
                requestBuilder.url(getUrl(paramsMap))
            }
            else -> {
                requestBuilder.post(mediaType.getRequestBody(paramsMap))
                requestBuilder.url(this)
            }
        }
        headers.keys.forEach {
            val value = headers[it]
            if (value != null) {
                requestBuilder.addHeader(it, value.toString())
            }

        }
        return requestBuilder.build()
    }

    @JvmStatic
    private fun MediaType.getRequestBody(paramsMap: HashMap<String, Any> = hashMapOf()): RequestBody {
        return when (this) {
            jsonMediaType -> {
                paramsMap.toJson().toRequestBody(this)
            }

            else -> {
                val formBody = FormBody.Builder()
                paramsMap.keys.forEach {
                    val value = paramsMap[it]
                    formBody.add(it, value?.toString() ?: "")
                }
                formBody.build()
            }
        }
    }

    @JvmStatic
    private fun String.getUrl(paramsMap: HashMap<String, Any> = hashMapOf()): String {
        val sb = StringBuilder()
        sb.append(this)
        if (!contains("?")) {
            sb.append("?")
        }
        sb.append(paramsMap.getUrl())
        return sb.toString()
    }

    @JvmStatic
    private fun HashMap<String, Any>.getUrl(): String {
        val sb = StringBuilder()
        keys.forEach {
            val value = get(it)
            sb.append(it)
            sb.append("=")
            sb.append(value?.toString() ?: "")
            sb.append("&")
        }
        sb.delete(sb.length - 1, sb.length)
        return sb.toString()
    }

    object Method {
        const val GET = 0
        const val POST = 1
    }

}