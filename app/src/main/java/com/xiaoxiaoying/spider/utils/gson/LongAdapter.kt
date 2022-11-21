package com.xiaoxiaoying.spider.utils.gson

import com.google.gson.*
import com.orhanobut.logger.Logger
import java.lang.reflect.Type

/**
 * @author xiaoxiaoying
 * @date 2021/12/13
 */
class LongAdapter : JsonSerializer<Long>, JsonDeserializer<Long> {
    override fun serialize(
        src: Long?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement = JsonPrimitive(src)

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Long {

        return try {
           json?.asLong ?: 0
        } catch (e: Exception) {
            Logger.e(e, "e")
            0
        }
    }
}