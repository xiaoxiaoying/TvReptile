package com.xiaoxiaoying.spider.utils.gson

import com.google.gson.*
import java.lang.reflect.Type

/**
 * @author xiaoxiaoying
 * @date 2021/12/13
 */
class IntAdapter : JsonSerializer<Int>, JsonDeserializer<Int> {
    override fun serialize(
        src: Int?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement = JsonPrimitive(src)

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Int {

        return try {
            json?.asInt ?: 0
        } catch (e: Exception) {
            0
        }
    }
}