package com.xiaoxiaoying.spider.core

import android.content.Context

/**
 * create time 2022/11/18
 * @author xiaoxiaoying
 */
abstract class Spider @JvmOverloads constructor(
    val context: Context,
    val extend: String = ""
) {

    /**
     * 首页数据内容
     * @param filter 是否开启筛选
     */
    open fun homeContent(filter: Boolean = false): String = ""

    /**
     * 首页最近更新数据 如果[homeContent]中不包含首页最近更新视频的数据 可以使用这个接口返回
     */
    open fun homeVideoContent(): String = ""




}