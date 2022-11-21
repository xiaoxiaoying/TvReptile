package com.xiaoxiaoying.spider.http.progress

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.aiear.basepack.progress.ProgressCancelListener
import com.xiaoxiaoying.spider.http.config.HttpDialogConfig
import com.xiaoxiaoying.spider.ui.dialog.HttpDialog

class ProgressDialogHandler constructor(
    private val context: Context? = null,
    private val mProgressCancelListener: ProgressCancelListener? = null,
    private val cancelable: Boolean = true,
    private val dimEnabled: Boolean = true,
    private val dialogConfig: HttpDialogConfig = HttpDialogConfig()
) : Handler(Looper.myLooper() ?: Looper.getMainLooper()) {
    private var pd: HttpDialog? = null


    companion object {
        const val SHOW_PROGRESS_DIALOG = 1
        const val DISMISS_PROGRESS_DIALOG = 2
    }

    private fun initProgressDialog(color: Int = 0, cancelable: Boolean = true) {
        if (pd == null) {
            pd = HttpDialog(dialogConfig)
            pd!!.mProgressCancelListener = mProgressCancelListener
            pd!!.isCancelable = cancelable
            pd!!.isDimEnabled = dimEnabled
        }
        pd?.isCancelable = cancelable

        val manager = when (context) {
            is FragmentActivity -> context.supportFragmentManager
            is Fragment -> (context as Fragment).childFragmentManager
            else -> null
        }

        if (manager != null && !pd!!.isAdded) {
            pd?.show(manager, "httpDialog")
        }
    }

    private fun dismissProgressDialog() {
        if (pd == null)
            return
        if (!pd!!.isAdded) {
            return
        }
        if (!pd!!.isHidden) {
            pd!!.dismissProgress()
            pd = null
        }
    }

    override fun handleMessage(msg: Message) {
        when (msg.what) {
            SHOW_PROGRESS_DIALOG -> {
                initProgressDialog()
            }
            DISMISS_PROGRESS_DIALOG -> dismissProgressDialog()
        }
    }
}