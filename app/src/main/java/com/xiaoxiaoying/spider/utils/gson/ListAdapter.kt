package com.xiaoxiaoying.spider.utils.gson

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.orhanobut.logger.Logger
import java.lang.reflect.Type

/**
 * create time 2022/7/28
 * @author xiaoxiaoying
 */
class ListAdapter : JsonDeserializer<List<Any>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): List<Any> {
        json ?: return emptyList()
        if (json.isJsonArray) {
            return GsonUtil.gson().fromJson(json, typeOfT)
        }
        try {
            val string = json.asString ?: "[]"
            return GsonUtil.gson().fromJson(string, typeOfT)
        } catch (e: Exception) {
            Logger.e(e, "e")
        }

        return emptyList()
    }
}