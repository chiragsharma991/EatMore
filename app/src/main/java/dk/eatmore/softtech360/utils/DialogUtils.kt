package dk.eatmore.softtech360.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import dk.eatmore.softtech360.R
import android.support.v4.app.FragmentActivity
import android.widget.ArrayAdapter



object DialogUtils {

    fun openDialog(context: Context, msg: String, btnPositive : String, btnNegative: String, onDialogClickListener: OnDialogClickListener) {
        val builder = AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle)
        builder.setMessage(msg)
        builder.setPositiveButton(btnPositive) { _, _ ->
            onDialogClickListener.onPositiveButtonClick(0)
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

    fun createListDialog(activity: FragmentActivity?, list : ArrayList<String>, onDialogClickListener: OnDialogClickListener) {

        val builderSingle = AlertDialog.Builder(activity!!)
      //  builderSingle.setIcon(R.drawable.order)
        builderSingle.setTitle("Select One Reason:")

        val arrayAdapter = ArrayAdapter<String>(activity!!, android.R.layout.select_dialog_item)
        for (i in 0..list.size-1){
            arrayAdapter.add(list.get(i))

        }
        builderSingle.setNegativeButton("cancel") { dialog, which -> dialog.dismiss() }
        builderSingle.setAdapter(arrayAdapter) { dialog, position ->
            val strName = arrayAdapter.getItem(position)
            val builderInner = AlertDialog.Builder(activity!!)
            builderInner.setMessage(strName)
            builderInner.setTitle("Selected reason is:")
            builderInner.setPositiveButton("Ok") { dialog, which ->
                dialog.dismiss ()
                onDialogClickListener.onPositiveButtonClick(position)
            }
            builderInner.show()
        }
        builderSingle.show()


    }






    interface OnDialogClickListener {
        fun onPositiveButtonClick(position : Int)
        fun onNegativeButtonClick()
    }
}
