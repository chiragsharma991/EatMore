package dk.eatmore.softtech360.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import dk.eatmore.softtech360.R

object DialogUtils {

    fun openDialog(context: Context, msg: String, btnPositive : String, btnNegative: String, onDialogClickListener: OnDialogClickListener) {
        val builder = AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle)
        builder.setMessage(msg)
        builder.setPositiveButton(btnPositive) { _, _ ->
            onDialogClickListener.onPositiveButtonClick()
        }
        /**
         * If blank then dont show negative button
         */
        if(btnNegative != "") {
            builder.setNegativeButton(btnNegative, { _, _ ->
                onDialogClickListener.onNegativeButtonClick()
            })
        }
        val alert = builder.create()
        alert.show()

        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context,R.color.colorSecondaryText))
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context,R.color.theme_color))
    }


    fun createDialog(context: Context, layout : Int) : AlertDialog {
        val builder = AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle_Transparent)
        val li = LayoutInflater.from(context)
        val view = li.inflate(layout, null)
        builder.setView(view)
        var dialog = builder.create()
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    fun showProgressDialog(){

    }


    interface OnDialogClickListener {
        fun onPositiveButtonClick()
        fun onNegativeButtonClick()
    }
}
