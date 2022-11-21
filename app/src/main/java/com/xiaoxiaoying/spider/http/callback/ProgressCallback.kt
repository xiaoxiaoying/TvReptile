package com.xiaoxiaoying.spider.http.callback

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.aiear.basepack.progress.ProgressCancelListener
import com.xiaoxiaoying.spider.http.config.HttpDialogConfig
import com.xiaoxiaoying.spider.http.progress.ProgressDialogHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope

/**
 * create time 2022/11/18
 * @author xiaoxiaoying
 */
abstract class ProgressCallback<T> @JvmOverloads constructor(
    private val isShowDialog: Boolean = false,
    context: Context? = null,
    dialogCancelable: Boolean = true,
    private val owner: LifecycleOwner? = null,
    dialogConfig: HttpDialogConfig = HttpDialogConfig(),
    override val scope: CoroutineScope = GlobalScope
) : BaseCallback<T>(scope) {
    private var mProgressDialogHandler: ProgressDialogHandler? = null
    private val cancelListener = object : ProgressCancelListener {
        override fun onCancelProgress() {
            cancel()
        }

    }
    private val lifecycleObserver: DefaultLifecycleObserver = object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            cancelListener.onCancelProgress()
        }
    }

    init {
        if (isShowDialog && context != null) {
            mProgressDialogHandler =
                ProgressDialogHandler(
                    context,
                    cancelListener,
                    dialogCancelable,
                    dialogConfig = dialogConfig
                )
        }

        owner?.lifecycle?.addObserver(lifecycleObserver)
        onStart()
    }

    /**
     * 订阅开始时调用
     * 显示ProgressDialog
     */
    private fun onStart() {
        if (!isShowDialog)
            return
        showProgressDialog()
    }

    private fun showProgressDialog() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler!!.obtainMessage(ProgressDialogHandler.SHOW_PROGRESS_DIALOG)
                .sendToTarget()
        }
    }

    private fun dismissProgressDialog() {
        mProgressDialogHandler?.removeMessages(ProgressDialogHandler.SHOW_PROGRESS_DIALOG)
        mProgressDialogHandler?.sendEmptyMessageDelayed(
            ProgressDialogHandler.DISMISS_PROGRESS_DIALOG,
            0
        )
    }


    override fun onFinish() {
        dismiss()
        super.onFinish()


    }

    private fun dismiss() {

        dismissProgressDialog()
        owner?.lifecycle?.removeObserver(lifecycleObserver)

    }


}