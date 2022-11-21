package com.xiaoxiaoying.spider.utils.gson

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.orhanobut.logger.Logger
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.Exception

/**
 * @author xiaoxiaoying
 * @date 2021/6/21
 */
object GsonUtil {
    @JvmStatic
    fun <T> parseStrToArray(str: String?, clazz: Class<T>): ArrayList<T>? {
        try {
            return buildGson().fromJson(str, object : ParameterizedType {
                override fun getRawType(): Type {
                    return ArrayList::class.java
                }

                override fun getOwnerType(): Type? {
                    return null
                }

                override fun getActualTypeArguments(): Array<Type> {
                    return arrayOf(clazz)
                }
            })
        } catch (e: Exception) {
            return null
        }

    }

    @JvmStatic
    fun <T> parseStrToList(str: String, clazz: Class<T>): List<T>? {
        return try {
            buildGson().fromJson(str, object : ParameterizedType {
                override fun getActualTypeArguments(): Array<Type> {
                    return arrayOf(clazz)
                }

                override fun getRawType(): Type = List::class.java

                override fun getOwnerType(): Type? = null

            })
        } catch (e: Exception) {
            Logger.e(e, "e")
            null
        }
    }

    @JvmStatic
    fun <T> parseStrToObj(str: String?, clazz: Class<T>): T? {
        return try {
            buildGson().fromJson(str, clazz)
        } catch (e: Exception) {
            Logger.e(e, "e")
            null
        }
    }

    fun buildGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(Int::class.java, IntAdapter())
            .registerTypeAdapter(Long::class.java, LongAdapter())
            .registerTypeAdapter(Double::class.java, DoubleAdapter())
            .registerTypeAdapter(String::class.java, StringAdapter())
            .registerTypeAdapter(List::class.java, ListAdapter())
            .create()
    }

    fun gson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(Int::class.java, IntAdapter())
            .registerTypeAdapter(Long::class.java, LongAdapter())
            .registerTypeAdapter(Double::class.java, DoubleAdapter())
            .registerTypeAdapter(String::class.java, StringAdapter())
            .create()
    }


    @JvmStatic
    fun Any.toJson(): String = Gson().toJson(this)
}