package com.xiaoxiaoying.spider.http

import com.orhanobut.logger.Logger
import okhttp3.*
import okhttp3.internal.EMPTY_RESPONSE
import okio.Buffer
import java.util.*

/**
 * @author xiaoxiaoying
 * @date 2021/6/9
 */
object Interceptor {
    private const val TAG = "HTTP"

    fun logT() {
        Logger.t(TAG)
    }

    @JvmStatic
    fun interceptor(chain: okhttp3.Interceptor.Chain): Response {
        val newBuilder = chain.request().newBuilder()
        val startTime = System.currentTimeMillis()

        var request: Request = newBuilder.build()
        if (!isNetworkConnected()) {
            request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build()
        }
        requestLog(request)
        val response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            getNetworkError(request, 502, "网络不可用")
        }
        val newResponse = responseLog(response, startTime)
        return newResponse ?: response
    }

    fun requestLog(request: Request) {
        try {
            val builder = request.newBuilder().build()
            val rtype = builder.body?.contentType().toString().lowercase(Locale.getDefault())
            val url = request.url.toUrl().toString()
            logT()
            Logger.i("url = $url \ncontent type = $rtype\nrequest header \n${builder.headers}")
            val body = builder.newBuilder().build().body ?: return
            if (rtype.contains("multipart/form-data;", ignoreCase = true))
                return
            when (body) {
                is FormBody -> {
                    val content = StringBuilder()
                    for (i in 0 until body.size) {
                        val value = body.value(i)
                        content.append("${body.name(i)} : $value \n")
                    }
                    logT()
                    if (rtype.contains("json"))
                        Logger.json(
                            content.toString()
                        )
                    else if (isPlaintext(rtype)) {
                        Logger.i(content.toString())
                    }
                }

                else -> {
                    val buffer = Buffer()
                    body.writeTo(buffer)
                    val string = buffer.readString(Charsets.UTF_8)
                    logT()
                    if (rtype.contains("json"))
                        Logger.json(string)
                    else {
                        Logger.i(string)
                    }
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
            logT()
            Logger.e("e = ${e.message}")
        }
    }

    fun responseLog(response: Response?, startTime: Long): Response? {

        if (response == null)
            return null

        try {
            val sb = StringBuilder()

            val body = response.body ?: return response

            val source = body.source()
            source.request(Long.MAX_VALUE)
            val buffer = source.buffer


            val type = body.contentType()?.toString()?.lowercase(Locale.getDefault()) ?: ""
            sb.append("mediaType: $type\n")
            val urlResponse = response.request.url.toString()
            sb.append("response url: $urlResponse\n")

            sb.append("response time: ${System.currentTimeMillis() - startTime} \n")

            val charset = Charsets.UTF_8
            val content = buffer.clone().readString(charset)
            logT()
            sb.append("body: $content")
            Logger.d(sb.toString())
            logT()
            when {
                type.contains("json") -> {
                    try {
                        Logger.json(content)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Logger.i(content)
                    }

                }
                type.contains("xml") -> {
                    Logger.xml(content)
                }
                else -> Logger.i(content)
            }


        } catch (e: Exception) {
            e.printStackTrace()
            logT()
            Logger.e("e : ${e.message}")
        }
        return response
    }

    private fun isPlaintext(type: String): Boolean =
        type.contains("x-www-form-urlencoded") ||
                type.contains("xml") ||
                type.contains("html")

    private fun isNetworkConnected(): Boolean {
//        val mConnectivityManager =
//            BaseApplication.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val mNetworkInfo = mConnectivityManager.activeNetworkInfo
        return true
    }

    private fun getNetworkError(request: Request, code: Int = 404, message: String): Response {
        return Response.Builder()
            .code(code)
            .message(message)
            .protocol(Protocol.HTTP_1_1)
            .body(EMPTY_RESPONSE)
            .request(request)
            .build()
    }
}