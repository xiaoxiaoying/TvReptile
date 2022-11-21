package com.xiaoxiaoying.spider.http.callback

import com.google.gson.internal.Primitives
import com.xiaoxiaoying.spider.http.OkHttpHelper
import com.xiaoxiaoying.spider.http.exception.ApiException
import com.xiaoxiaoying.spider.utils.gson.GsonUtil
import kotlinx.coroutines.*
import okhttp3.Call
import okhttp3.Response

/**
 * create time 2022/11/18
 * @author xiaoxiaoying
 */
abstract class BaseCallback<T>(open val scope: CoroutineScope) {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError(ApiException(400, throwable.message))
    }

    /**
     * 线程作业
     */
    var job: Job? = null

    var call: Call? = null

    var respHeaders: Map<String, List<String>> = hashMapOf()

    open fun onSuccess(t: T?) {

    }

    open fun onFinish() {

    }

    open fun cancel() {
        scope.launch {
            if (job?.isCancelled == true ||
                job?.isCompleted == true
            )
                return@launch
            if (call?.isCanceled() == false) {
                call?.cancel()
            }

            job?.cancelAndJoin()
        }

    }

    open fun onError(exception: ApiException) {

    }

    suspend fun onResponseNext(response: Response, clazz: Class<T>) {
        val isSuccessful = response.isSuccessful

        if (!isSuccessful) {
            withContext(Dispatchers.Main)
            {
                onError(ApiException(response.code, response.message))
            }
            return
        }
        val body = response.body
        if (body == null) {
            withContext(Dispatchers.Main)
            {
                onError(ApiException(response.code, "body is null"))
            }
            return
        }
        val contentType = body.contentType()
        val json = body.source().readString(Charsets.UTF_8)
        if (contentType == OkHttpHelper.jsonMediaType) {
            val obj = GsonUtil.parseStrToObj(json, clazz)

            withContext(Dispatchers.Main)
            {
                if (job?.isCancelled == false) {
                    onSuccess(obj)
                }
                onFinish()
            }
            return
        }
        withContext(Dispatchers.Main)
        {
            if (job?.isCancelled == false) {
                onSuccess(Primitives.wrap(clazz).cast(json))
            }
            onFinish()
        }

    }
}