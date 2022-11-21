package com.xiaoxiaoying.spider.http.config

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.xiaoxiaoying.spider.R
import com.xiaoxiaoying.spider.utils.SizeUtils.dipToPix
import java.io.Serializable

/**
 * @author xiaoxiaoying
 * @date 2021/8/9
 */
class HttpDialogConfig : Serializable {
    /**
     * 动画弹框宽度
     */
    var width: Int = 35

    /**
     * 高度
     */
    var height: Int = 35

    /**
     * 背景宽度
     * -1 充满屏幕
     * -2 包裹
     */
    var backWidth: Int = -2

    /**
     * 背景高度
     */
    var backHeight: Int = -2

    /**
     * 颜色
     */
    var indicatorColor: Int = Color.parseColor("#222222")

    /**
     * 背景颜色
     * 如果 [backgroundRes] != [Int.MAX_VALUE]
     * 则优先使用 [backgroundRes]
     */
    @ColorInt
    var backgroundColor: Int = Color.TRANSPARENT

    /**
     * 背景资源ID
     */
    @DrawableRes
    var backgroundRes: Int = Int.MAX_VALUE

    var textSize: Float = 12f

    /**
     * 默认显示的文字
     */
    var hintText: String = ""

    @ColorInt
    var textColor: Int = Color.WHITE

    var dimAmount: Float = 0.01f

    /**
     * 上传文件使用
     * count 上传个数
     * position 当前上传的第几个
     * totalLen 当前上传文件总长度
     * byteLen 当前上传文件已上传长度
     */
    var uploadFileProgress: ((count: Int, position: Int, totalLen: Long, byteLen: Long) -> Unit)? =
        null

    var isUploadFile: Boolean = false

    companion object {
        /**
         * 上传文件使用dialog 样式
         *
         */
        @JvmStatic
        fun Context.getUploadFileConfig(): HttpDialogConfig {
            val config = HttpDialogConfig()
            config.backgroundRes = R.drawable.shape_http_dialog_upload
            config.width = 50
            config.height = 50
            config.indicatorColor = Color.WHITE
            config.backWidth = dipToPix(120)
            config.backHeight = dipToPix(120)
            config.isUploadFile = true
            return config
        }

        @JvmStatic
        fun Context.getDigitalHttpConfig(
            isShowHint: Boolean = false,
            hintText: String = ""
        ): HttpDialogConfig {
            val config = HttpDialogConfig()
            config.backgroundRes = R.drawable.shape_http_dialog_upload
            config.width = 35
            config.height = 35
            config.dimAmount = 0.4f
            config.indicatorColor = Color.WHITE
            config.backWidth = dipToPix(160)
            config.backHeight = dipToPix(120)
            config.isUploadFile = isShowHint
            config.hintText = hintText
            return config
        }

        @JvmStatic
        fun getHttpWhiteConfig(): HttpDialogConfig {
            val config = HttpDialogConfig()
            config.width = 35
            config.height = 35
            config.indicatorColor = Color.WHITE
            return config
        }
    }
}