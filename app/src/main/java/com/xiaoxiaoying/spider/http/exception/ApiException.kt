package com.xiaoxiaoying.spider.http.exception

/**
 * create time 2022/11/18
 * @author xiaoxiaoying
 */
class ApiException(
    val resultCode: Int,
    val detailMessage: String? = ""
) {
    override fun toString(): String {
        return "$resultCode : $detailMessage"
    }
}