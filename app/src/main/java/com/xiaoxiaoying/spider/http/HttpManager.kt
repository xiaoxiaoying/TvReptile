package com.xiaoxiaoying.spider.http

import com.xiaoxiaoying.spider.http.OkHttpHelper.getRequest
import com.xiaoxiaoying.spider.http.callback.BaseCallback
import kotlinx.coroutines.launch
import okhttp3.MediaType

/**
 * create time 2022/11/18
 * @author xiaoxiaoying
 */
class HttpManager private constructor() {
    private val defaultClient = OkHttpHelper.getOkHttpClientBuilder()
        .retryOnConnectionFailure(true)
        .build()

    private val noRedirectClient = OkHttpHelper.getOkHttpClientBuilder()
        .followRedirects(false)
        .followSslRedirects(false)
        .retryOnConnectionFailure(true)
        .build()

    companion object {
        val instance: HttpManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            HttpManager()
        }

        @JvmStatic
        fun HashMap<String, List<String>>.getRedirectLocation(): String? {
            if (containsKey("location")) {
                val location = get("location")
                if (location?.isNotEmpty() == true) {
                    return location.first()
                }
                return null
            }

            if (containsKey("Location")) {
                val location = get("Location")
                if (location?.isNotEmpty() == true) {
                    return location.first()
                }
                return null
            }

            return null
        }
    }

    fun <T> client(
        url: String,
        method: Int = OkHttpHelper.Method.POST,
        mediaType: MediaType = OkHttpHelper.jsonMediaType,
        headers: HashMap<String, Any> = hashMapOf(),
        paramsMap: HashMap<String, Any> = hashMapOf(),
        callback: BaseCallback<T>,
        clazz: Class<T>
    ) {
        callback.job = callback.scope.launch(callback.exceptionHandler) {
            val request = url.getRequest(method, mediaType, headers, paramsMap)
            val call = defaultClient.newCall(request)
            val response = call.execute()
            callback.call = call
            callback.respHeaders = response.headers.toMultimap()
            callback.onResponseNext(response, clazz)
        }
    }

    fun <T> clientNoRedirect(
        url: String,
        method: Int = OkHttpHelper.Method.POST,
        mediaType: MediaType = OkHttpHelper.jsonMediaType,
        headers: HashMap<String, Any> = hashMapOf(),
        paramsMap: HashMap<String, Any> = hashMapOf(),
        callback: BaseCallback<T>,
        clazz: Class<T>
    ) {
        callback.job = callback.scope.launch(callback.exceptionHandler) {
            val request = url.getRequest(method, mediaType, headers, paramsMap)
            val call = noRedirectClient.newCall(request)
            val response = call.execute()
            callback.call = call
            callback.respHeaders = response.headers.toMultimap()
            callback.onResponseNext(response, clazz)
        }
    }
}