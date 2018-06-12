package dk.eatmore.partner.utils


import android.content.Context

import com.leo.simplearcloader.ArcConfiguration
import com.leo.simplearcloader.SimpleArcDialog
import com.leo.simplearcloader.SimpleArcLoader

import dk.eatmore.partner.R

class ProgressDialaogView {
    private var mSimpleArcDialog: SimpleArcDialog? = null
    private var configuration: ArcConfiguration? = null
    internal var theme_color: String? = null

    fun getProgressDialog(mContext: Context): SimpleArcDialog? {
        mSimpleArcDialog = SimpleArcDialog(mContext)
        mSimpleArcDialog!!.setCancelable(false)
        configuration = ArcConfiguration(mContext)
        configuration!!.loaderStyle = SimpleArcLoader.STYLE.COMPLETE_ARC
        configuration!!.text = mContext.getString(R.string.please_wait)
        configuration!!.setAnimationSpeedWithIndex(SimpleArcLoader.SPEED_FAST)
        val int_colr = R.color.theme_color
        val color = intArrayOf(int_colr, int_colr)
        configuration!!.colors = color
        mSimpleArcDialog!!.setConfiguration(configuration)

        return mSimpleArcDialog
    }

    fun getProgressDialog(mContext: Context, msg: String?): SimpleArcDialog? {
        mSimpleArcDialog = SimpleArcDialog(mContext)
        mSimpleArcDialog!!.setCancelable(false)
        configuration = ArcConfiguration(mContext)
        configuration!!.loaderStyle = SimpleArcLoader.STYLE.COMPLETE_ARC
        if (msg != null)
            configuration!!.text = msg
        else
            configuration!!.text = mContext.getString(R.string.please_wait)
        configuration!!.setAnimationSpeedWithIndex(SimpleArcLoader.SPEED_FAST)
        val int_colr = R.color.theme_color
        val color = intArrayOf(int_colr, int_colr)
        configuration!!.colors = color
        mSimpleArcDialog!!.setConfiguration(configuration)

        return mSimpleArcDialog
    }

    fun dismissSimpleArcDialog(mSimpleArcDialog: SimpleArcDialog?) {
        if (mSimpleArcDialog != null && mSimpleArcDialog.isShowing)
            mSimpleArcDialog.dismiss()
    }

    fun setMessage(msg: String) {
        configuration!!.text = msg
    }
}
