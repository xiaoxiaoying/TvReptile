package com.xiaoxiaoying.spider.utils

import android.text.TextUtils
import androidx.core.net.toUri
import org.json.JSONObject
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.regex.Pattern

/**
 * create time 2022/11/18
 * @author xiaoxiaoying
 */
object Util {
    private val pattern = Pattern.compile("[^-0-9.]")
    private val VIP_WEBSITES = arrayOf(
        "iqiyi.com",
        "v.qq.com",
        "youku.com",
        "le.com",
        "tudou.com",
        "mgtv.com",
        "sohu.com",
        "acfun.cn",
        "bilibili.com",
        "baofeng.com",
        "pptv.com"
    )

    private val snifferMatch =
        Pattern.compile("http((?!http).){26,}?\\.(m3u8|mp4)\\?.*|http((?!http).){26,}\\.(m3u8|mp4)|http((?!http).){26,}?/m3u8\\?pt=m3u8.*|http((?!http).)*?default\\.ixigua\\.com/.*|http((?!http).)*?cdn-tos[^\\?]*|http((?!http).)*?/obj/tos[^\\?]*|http.*?/player/m3u8play\\.php\\?url=.*|http.*?/player/.*?[pP]lay\\.php\\?url=.*|http.*?/playlist/m3u8/\\?vid=.*|http.*?\\.php\\?type=m3u8&.*|http.*?/download.aspx\\?.*|http.*?/api/up_api.php\\?.*|https.*?\\.66yk\\.cn.*|http((?!http).)*?netease\\.com/file/.*")

    const val UaWinChrome =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.54 Safari/537.36"

    /**
     * 适配2.0.6的调用应用内解析列表的支持, 需要配合直连分析一起使用，参考cjt影视和极品直连
     */
    @JvmStatic
    fun String.isVip(): Boolean {
        try {
            val host = toUri().host
            VIP_WEBSITES.forEachIndexed { index, s ->
                if (host?.contains(s) == true) {
                    if (index == 0) {
                        return contains("iqiyi.com/a_") || contains("iqiyi.com/w_") || contains("iqiyi.com/v_")
                    }
                    return true
                }
            }
            return false
        } catch (e: Exception) {
            return false
        }
    }

    @JvmStatic
    fun String.isVideoFormat(): Boolean {
        if (!snifferMatch.matcher(this).find()) {
            return false
        }

        if (contains("cdn-tos") && contains("js"))
            return false
        return true
    }

    @JvmStatic
    fun String.isBlackVodUrl(): Boolean = contains("973973.xyz") || contains(".fit:")

    @JvmStatic
    fun String.fixUrl(base: String): String {
        try {
            when {
                startsWith("//") -> {
                    val scheme = base.toUri().scheme
                    return "$scheme:$this"
                }

                !contains("://") -> {
                    val uri = base.toUri()
                    return "${uri.scheme}://${uri.host}$this"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return this
    }

    @Throws
    @JvmStatic
    fun String.fixJsonVodHeader(headers: JSONObject? = null, input: String): JSONObject {
        var json = headers
        if (json == null)
            json = JSONObject()

        when {
            input.contains("www.mgtv.com") ||
                    contains("titan.mgtv") -> {
                json.put("Referer", " ")
                json.put("User-Agent", " Mozilla/5.0")
            }

            input.contains("bilibili") -> {
                json.put("Referer", " https://www.bilibili.com/")
                json.put("User-Agent", " $UaWinChrome")
            }
        }

        return json
    }

    @Throws
    @JvmStatic
    fun String.jsonParse(input: String): JSONObject? {
        val jsonPlayData = JSONObject(this)
        var url = ""
        if (jsonPlayData.has("url"))
            url = jsonPlayData.getString("url")

        if (url.startsWith("//")) {
            url = "https:$url"
        }
        if (!url.startsWith("http"))
            return null

        if (url == input && (url.isVip() || !url.isVideoFormat())) {
            return null
        }

        if (url.isBlackVodUrl())
            return null
        var headers = JSONObject()
        val agent = jsonPlayData.optString("user-agent")
        if (!TextUtils.isEmpty(agent)) {
            headers.put("User-Agent", " $agent")
        }

        val referer = jsonPlayData.optString("referer")
        if (!TextUtils.isEmpty(referer)) {
            headers.put("Referer", " $referer")
        }

        headers = url.fixJsonVodHeader(headers, input)
        return JSONObject().apply {
            put("header", headers)
            put("url", url)
        }

    }


    /**
     * 格式化金额,
     * @method
     * @param value 金额内容
     * @param decimalPlace 需要保留的小数位 默认-1 就是 [Double] 类型后面有几位小数就保留几位 最大为2 位
     * @return 金额
     */
    @JvmStatic
    fun String.decimalFormatMoney(

        decimalPlace: Int = -1,
        hasComma: Boolean = false
    ): String {

        if (TextUtils.isEmpty(this))
            return "0"

        return decimalFormatMoney(decimalPlace.getFormatMoneyType(this, hasComma)) ?: ""
    }

    private fun Int.getFormatMoneyType(value: String?, hasComma: Boolean = false): String {
        value ?: return "0.0"
        val sb = StringBuilder()
        val len = value.getDoubleLen()
        val length = if (this < 0) {
            if (len > 2) {
                2
            } else len
        } else this
        if (hasComma) {
            sb.append("#,##")
        }
        sb.append("0")
        if (length > 0) {
            sb.append(".")
        }
        repeat(length) {
            sb.append("0")
        }
        return sb.toString()
    }

    private fun String.decimalFormatMoney(formatType: String): String {
        val format = DecimalFormat(formatType)
        return format.format(BigDecimal(formatMoney(formatType)))
    }

    @JvmStatic
    fun Float.decimalFormatMoney(decimalPlace: Int = -1, hasComma: Boolean = false): String {
        return toBigDecimal().toPlainString().decimalFormatMoney(decimalPlace, hasComma)
    }

    /**
     * 格式化
     * 保留几位小数
     * @param decimalPlace 需要保留的小数位 默认-1 就是 ` Double ` 类型后面有几位小数就保留几位 最大为2 位
     */
    @JvmStatic
    fun Double.decimalFormatMoney(decimalPlace: Int = -1, hasComma: Boolean = false): String {

        return toBigDecimal().toPlainString().decimalFormatMoney(decimalPlace, hasComma)
    }

    /**
     * 获取有几位小数
     */
    @JvmStatic
    fun String.getDoubleLen(): Int {

        if (!contains("."))
            return 0
        return substring(indexOf(".") + 1, length).length
    }

    @JvmStatic
    fun String.decimalFormat(): String {
        val len = getDoubleLen()
        return replace(len.getReplace(), "")
    }


    private fun Int.getGroup(): Int {
        val count = this / 3
        val m = this % 3
        return (count + if (m != 0)
            1 else 0) - 1
    }

    private fun Int.getReplace(): String {
        if (this <= 0)
            return ""
        val sb = StringBuilder()
        sb.append(".")
        repeat(this) {
            sb.append("0")
        }
        return sb.toString()
    }

    /**
     * 去除非数字内容，如：0.12元，0,01，1,11
     * @param value 格式化金额类
     * @return
     */
    fun String.formatMoney(formatType: String): String {

        if (TextUtils.isEmpty(this)) {
            return formatType
        }
        val money = pattern.matcher(this).replaceAll("").trim()
        if ("" == money) {
            return formatType
        }
        return money
    }
}