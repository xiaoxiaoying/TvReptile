package com.xiaoxiaoying.spider.utils.gson

import com.google.gson.*
import com.orhanobut.logger.Logger
import com.xiaoxiaoying.spider.utils.Util.formatMoney
import java.lang.reflect.Type

/**
 * @author xiaoxiaoying
 * @date 2021/12/13
 */
class DoubleAdapter : JsonSerializer<Double>, JsonDeserializer<Double> {
    override fun serialize(
        src: Double?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement = JsonPrimitive(src)

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Double {


        return try {
            json?.asString?.formatMoney("")?.toDouble() ?: .0
        } catch (e: Exception) {
            Logger.e(e, "e")
            .0
        }
    }
}