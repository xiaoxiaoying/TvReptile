package com.xiaoxiaoying.spider.ui.dialog

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.aiear.basepack.progress.ProgressCancelListener
import com.xiaoxiaoying.spider.widget.loading.LoadingIndicatorView
import com.orhanobut.logger.Logger
import com.xiaoxiaoying.spider.R
import com.xiaoxiaoying.spider.http.config.HttpDialogConfig
import com.xiaoxiaoying.spider.utils.SizeUtils.dipToPix


class HttpDialog(
    private val dialogConfig: HttpDialogConfig = HttpDialogConfig()
) : DialogFragment() {

    companion object {
        private const val WHAT_PROGRESS = 90
    }

    init {
        isCancelable = true
    }

    var mProgressCancelListener: ProgressCancelListener? = null
    private var loading: LoadingIndicatorView? = null
    private var hint: TextView? = null
    var isDimEnabled: Boolean = true

    private val handler = object : Handler(Looper.myLooper() ?: Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                WHAT_PROGRESS -> {
                    val sb = msg.obj
                    if (sb !is String)
                        return
                    hint?.text = sb
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AppCompatDialog(requireContext(), R.style.Http_Dialog)
        dialog.setCanceledOnTouchOutside(false)
        val window = dialog.window
        if (window != null) {
            val attr = window.attributes
            attr.width = WindowManager.LayoutParams.WRAP_CONTENT
            attr.dimAmount = dialogConfig.dimAmount
            window.attributes = attr
//            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.dialog_http, container, false)

    /**
     * 文件上传进度
     */
    private fun uploadFileProgress(count: Int, position: Int, totalLen: Long, byteLen: Long) {
        Logger.i("count = $count position = $position total length = $totalLen  byte length = $byteLen")
        val p = byteLen / totalLen.toFloat() * 100
        val sb = StringBuilder()
        sb.append(p.toInt())
        sb.append("%")
        if (count > 1) {
            sb.append("  ")
            sb.append(position + 1)
            sb.append("/")
            sb.append(count)
        }

        val msg = handler.obtainMessage()
        msg.what = WHAT_PROGRESS
        msg.obj = sb.toString()
        handler.sendMessage(msg)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(Color.TRANSPARENT)

        dialogConfig.uploadFileProgress = this::uploadFileProgress

        loading = view.findViewById(R.id.loading)
        hint = view.findViewById(R.id.hint)
        ViewGroup.LayoutParams.WRAP_CONTENT
        val params = loading?.layoutParams
        params?.width = requireContext().dipToPix(dialogConfig.width)
        params?.height = requireContext().dipToPix(dialogConfig.height)
        loading?.layoutParams = params
        loading?.setIndicatorColor(dialogConfig.indicatorColor)
        val viewParams = view.layoutParams
        viewParams?.width = dialogConfig.backWidth
        viewParams?.height = dialogConfig.backHeight
        view.layoutParams = viewParams

        if (dialogConfig.backgroundRes != Int.MAX_VALUE) {
            view.setBackgroundResource(dialogConfig.backgroundRes)
        } else view.setBackgroundColor(dialogConfig.backgroundColor)
        hint?.visibility = if (dialogConfig.isUploadFile)
            View.VISIBLE else View.GONE
        hint?.textSize = dialogConfig.textSize
        hint?.text = dialogConfig.hintText
        hint?.setTextColor(dialogConfig.textColor)

//        loading?.visibility = View.GONE
//        val img = view.findViewById(R.id.loadingImg)
//        Glide.with(img)
//            .load(R.drawable.loading)
//            .into(img)

        dialog?.setOnCancelListener {
            mProgressCancelListener?.onCancelProgress()
        }
    }

    fun setIndicatorColor(color: Int) {
        if (color != 0)
            loading?.setIndicatorColor(color)
    }

    override fun onStart() {
        if (!isDimEnabled) {
            val window = dialog?.window
            if (window != null) {
                val attr = window.attributes
                attr.width = WindowManager.LayoutParams.WRAP_CONTENT
                attr.dimAmount = 0.0f
                window.attributes = attr
                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            }
        }
        super.onStart()
    }

    override fun showNow(manager: FragmentManager, tag: String?) {
        super.showNow(manager, tag)
    }

    override fun show(manager: FragmentManager, tag: String?) {
        loading?.show()
        val beginTransaction = manager.beginTransaction()
        val fragment = manager.findFragmentByTag(tag)
        if (fragment != null) {
            beginTransaction.remove(fragment)
        }
 
        beginTransaction.add(this, tag)
        beginTransaction.commitAllowingStateLoss()
//        super.show(manager, tag) ming 注释 解决偶尔会出现Can not perform this action after onSaveInstanceState异常
    }

    fun dismissProgress() {
        loading?.hide()
        try {
            super.dismissAllowingStateLoss()
        } catch (e: Exception) {

        }
    }

    override fun dismiss() {

        loading?.hide()
        super.dismissAllowingStateLoss()

    }

    override fun onDestroyView() {
        handler.removeMessages(WHAT_PROGRESS)
        super.onDestroyView()
    }

}