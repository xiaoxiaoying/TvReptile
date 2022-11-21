package com.xiaoxiaoying.spider.utils.gson

import com.google.gson.*
import java.lang.reflect.Type

/**
 * @author xiaoxiaoying
 * @date 2022/2/14
 */
class StringAdapter : JsonSerializer<String>, JsonDeserializer<String> {
    override fun serialize(
        src: String?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement = JsonPrimitive(src)

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): String {

        json ?: return ""



        return try {
            when (json) {
                is JsonPrimitive -> {
                    when {
                        json.isNumber -> json.toString()

                        else -> json.asString ?: ""
                    }
                }
                else -> Gson().fromJson(json, typeOfT)
            }

        } catch (e: Exception) {
            ""
        }
    }
}