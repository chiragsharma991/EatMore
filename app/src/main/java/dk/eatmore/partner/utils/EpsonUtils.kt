package dk.eatmore.partner.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import com.epson.epos2.Epos2Exception
import com.epson.epos2.Epos2CallbackCode
import com.epson.epos2.printer.Printer
import com.epson.epos2.printer.PrinterStatusInfo
import dk.eatmore.partner.R
import dk.eatmore.partner.dashboard.fragment.order.OrderDetails
import dk.eatmore.partner.model.DataItem
import dk.eatmore.partner.storage.PreferenceUtil
import java.text.SimpleDateFormat

object EpsonUtils {


    private val TAG : String="EpsonUtils"

    fun createReceiptData(data: List<DataItem>?, mPrinter: Printer?,isaccepted : Boolean, context: Context?): Boolean {

        when(isaccepted){

            true ->  {
                return acceptedreceipt(data,mPrinter,context)
            }

            false -> {
                return rejectedreceipt(data,mPrinter,context)
            }
        }
    }

    private fun rejectedreceipt(data: List<DataItem>?,mPrinter: Printer?, context: Context?) :Boolean{

        val logoData = BitmapFactory.decodeResource(context!!.resources, R.drawable.eatmorelogo_print)
        var textData: StringBuilder = StringBuilder()

        if (mPrinter == null) {
            return false
        }

        try {
            mPrinter.addTextAlign(Printer.ALIGN_CENTER)
            mPrinter.addImage(logoData, 0, 0,
                    logoData.width,
                    logoData.height,
                    Printer.COLOR_1,
                    Printer.MODE_MONO,
                    Printer.HALFTONE_DITHER,
                    Printer.PARAM_DEFAULT.toDouble(),
                    Printer.COMPRESS_AUTO)

          //  mPrinter.addFeedLine(1)
            textData.append("\n***************  REJECTED  *************\n\n")
            textData.append(data!!.get(0).restaurant_id+"\n")
            textData.append(context.getString(R.string.order_coming_from)+"\n")
            if (data.get(0).restaurant_site != null) textData.append(data.get(0).restaurant_site+"\n")
            textData.append("----------------------------------------\n")

            textData.append(context.getString(R.string.order_no) + "${data.get(0).order_no}"+"\n")
            mPrinter.addText(textData.toString())
            textData.delete(0, textData.length)
            mPrinter.addTextStyle(0,0,1,Printer.COLOR_1)
            textData.append(if (data.get(0).shipping.capitalize().toUpperCase() == "DELIVERY") context.getString(R.string.delivery) else context.getString(R.string.pick_up))
            textData.append("\n")
            val calculatedTime = ConversionUtils.getCalculatedTime(data.get(0).expected_time, "yyyy-MM-dd HH:mm:ss")
            val expected_time = SimpleDateFormat("HH:mm:ss yyyy-MM-dd").format(calculatedTime)
            textData.append(expected_time+"\n")
            textData.append("----------------------------------------\n")
            mPrinter.addText(textData.toString())
            textData.delete(0, textData.length)


            // Add product details:
            for (i in 0..data.get(0).order_products_details!!.size - 1) {
                mPrinter.addTextStyle(0,0,1,Printer.COLOR_1)
                mPrinter.addTextAlign(Printer.ALIGN_CENTER)
                //--- product name ---
                val item :String= padLine(
                        partOne = data.get(0).order_products_details!!.get(i).quantity + " x " +
                                data.get(0).order_products_details!!.get(i).products.product_no +
                                data.get(0).order_products_details!!.get(i).products.p_name,
                        partTwo = ConversionUtils.convertCurrencyToDanish(data.get(0).order_products_details!!.get(i).p_price),
                        columnsPerLine = 40
                )
                textData.append(item +"\n")
                mPrinter.addText(textData.toString())
                textData.delete(0, textData.length)
                mPrinter.addTextStyle(0,0,0,Printer.COLOR_1)
                mPrinter.addTextAlign(Printer.ALIGN_LEFT)

                // --- ingradient ---
                if (data.get(0).order_products_details?.get(i)?.removed_ingredients?.size != null) {

                    for (j in 0..data.get(0).order_products_details!!.get(i).removed_ingredients!!.size - 1) {
                        textData.append("        -" + data.get(0).order_products_details!!.get(i).removed_ingredients!!.get(j).ingredient_name+"\n")
                    }
                }


                // --- attributes ---
                if (data.get(0).order_products_details!!.get(i).products.is_attributes == "1") {

                    // with extratoppings
                    for (k in 0..data.get(0).order_products_details!!.get(i).ordered_product_attributes!!.size - 1) {

                        // Add attribute Header---
                        textData.append("        "+data.get(0).order_products_details!!.get(i).ordered_product_attributes!!.get(k).attribute_value_name+"\n")

                        // Add attribute of sizes---
                        for (l in 0..data.get(0).order_products_details!!.get(i).ordered_product_attributes!!.get(k).order_product_extra_topping_group!!.size - 1) {
                            textData.append("        +" + data.get(0).order_products_details!!.get(i).ordered_product_attributes!!.get(k).order_product_extra_topping_group!!.get(l).ingredient_name+"\n")
                        }
                    }


                } else {

                    // Without extratoppings -- Add attribute of sizes---

                    for (l in 0..data.get(0).order_products_details!!.get(i).order_product_extra_topping_group!!.size - 1) {
                        textData.append("        +" + data.get(0).order_products_details!!.get(i).order_product_extra_topping_group!!.get(l).ingredient_name+"\n")
                    }
                }
                mPrinter.addText(textData.toString())
                textData.delete(0, textData.length)

            }


            //Add price:
            mPrinter.addTextAlign(Printer.ALIGN_CENTER)
            textData.append("----------------------------------------\n")
            if (data.get(0).order_total != null && data.get(0).order_total != "0" && data.get(0).order_total != "0.00") {
                textData.append(padLine(context.getString(R.string.subtotal),ConversionUtils.convertCurrencyToDanish(data.get(0).order_total!!),40)+"\n")
            }
            if (data.get(0).upto_min_shipping != null && data.get(0).upto_min_shipping != "0.00" && data.get(0).upto_min_shipping != "0") {
                textData.append(padLine(context.getString(R.string.restupto),ConversionUtils.convertCurrencyToDanish(data.get(0).upto_min_shipping!!),40)+"\n")
            }
            if (data.get(0).shipping_costs != null && data.get(0).shipping_costs != "0" && data.get(0).shipping_costs != "0.00") {
                textData.append( padLine(context.getString(R.string.shipping),ConversionUtils.convertCurrencyToDanish(data.get(0).shipping_costs!!),40)+"\n")
            }

            if (data.get(0).additional_charge != null && data.get(0).additional_charge != "0" && data.get(0).additional_charge != "0.00") {
                textData.append(padLine(context.getString(R.string.additional),ConversionUtils.convertCurrencyToDanish(data.get(0).additional_charge!!),40)+"\n")
            }
            if (data.get(0).discount_amount != null && data.get(0).discount_amount != "0" && data.get(0).discount_amount != "0.00") {
                textData.append(padLine(context.getString(R.string.discount),ConversionUtils.convertCurrencyToDanish(data.get(0).discount_amount!!),40)+"\n")
            }
            mPrinter.addText(textData.toString())
            textData.delete(0, textData.length)

            if (data.get(0).total_to_pay != null && data.get(0).total_to_pay != "0" && data.get(0).total_to_pay != "0.00") {
                mPrinter.addFeedLine(1)
                mPrinter.addTextStyle(0,0,1,Printer.COLOR_1)
                textData.append(padLine(context.getString(R.string.total),ConversionUtils.convertCurrencyToDanish(data.get(0).total_to_pay!!),40)+"\n")
                mPrinter.addText(textData.toString())
                textData.delete(0, textData.length)
            }
            mPrinter.addTextStyle(0,0,0,Printer.COLOR_1)
            textData.append("----------------------------------------\n")
            mPrinter.addText(textData.toString())
            textData.delete(0, textData.length)

            //Not Paid:
            mPrinter.addTextStyle(0,0,1,Printer.COLOR_1)
            mPrinter.addTextSize(2,1)
            textData.append(if (data.get(0).payment_status.capitalize().toUpperCase() == "NOT PAID") context.getString(R.string.not_paid) else context.getString(R.string.paid))
            textData.append("\n")
            mPrinter.addText(textData.toString())
            textData.delete(0, textData.length)
            mPrinter.addTextSize(1,1)
            textData.append(if (data.get(0).paymethod == "1") context.getString(R.string.online_payment) else context.getString(R.string.cash_payment)+"\n")
            mPrinter.addText(textData.toString())
            textData.delete(0, textData.length)
            mPrinter.addTextStyle(0,0,0,Printer.COLOR_1)
            textData.append("----------------------------------------\n")
            mPrinter.addText(textData.toString())
            textData.delete(0, textData.length)


            //Address and name:
            textData.append(data.get(0).first_name+"\n")
            textData.append(data.get(0).address+"\n")
            mPrinter.addText(textData.toString())
            textData.delete(0, textData.length)
            mPrinter.addTextStyle(0,0,1,Printer.COLOR_1)
            textData.append(context.getString(R.string.phone)+" "+data.get(0).telephone_no+"\n")
            mPrinter.addText(textData.toString())
            textData.delete(0, textData.length)
            mPrinter.addTextStyle(0,0,0,Printer.COLOR_1)
            textData.append(context.getString(R.string.pre_order)+" "+data.get(0).previous_order+"\n")
            textData.append(context.getString(R.string.distance_km)+" "+data.get(0).distance+"\n")
            if (data.get(0).shipping_remark != null) {
                textData.append("${context.getString(R.string.note)} ${data.get(0).shipping_remark}"+"\n")
            }
            textData.append("----------------------------------------\n")
            mPrinter.addText(textData.toString())
            textData.delete(0, textData.length)



            //Accepted time:
            mPrinter.addTextSize(2,1)
            mPrinter.addTextStyle(0,0,1,Printer.COLOR_1)
            textData.append(context.getString(R.string.rejected_reason))
            textData.append("\n")
                if (data.get(0).reject_reason != null) {
                    textData.append(data.get(0).reject_reason+"\n")
                }
            mPrinter.addText(textData.toString())
            textData.delete(0, textData.length)

            //Powered by:
            mPrinter.addTextStyle(0,0,0,Printer.COLOR_1)
            mPrinter.addTextSize(1,1)
            textData.append("----------------------------------------\n")
            mPrinter.addText(textData.toString())
            textData.delete(0, textData.length)
            textData.append("Powered by - EatMore.dk")
            textData.append("\n")
            mPrinter.addText(textData.toString())
            textData.delete(0, textData.length)
            mPrinter.addFeedLine(1)
            mPrinter.addText(textData.toString())
            textData.delete(0, textData.length)
            Log.e("print receipt success","---")

            //cut the page:
            try{
                mPrinter.addCut(Printer.CUT_FEED)
            }catch (e : Exception){
                Log.e(TAG,"cut exception---"+e.message)
            }
        }catch (e : Exception){
            Log.e(TAG,e.message)
            return false
        }

        return true



    }

    private fun acceptedreceipt(data: List<DataItem>?,mPrinter: Printer?, context: Context?) : Boolean {
        val logoData = BitmapFactory.decodeResource(context!!.resources, R.drawable.eatmorelogo_print)
        var textData: StringBuilder = StringBuilder()

        if (mPrinter == null) {
            return false
        }

        try {
            mPrinter.addTextAlign(Printer.ALIGN_CENTER)
            mPrinter.addImage(logoData, 0, 0,
                    logoData.width,
                    logoData.height,
                    Printer.COLOR_1,
                    Printer.MODE_MONO,
                    Printer.HALFTONE_DITHER,
                    Printer.PARAM_DEFAULT.toDouble(),
                    Printer.COMPRESS_AUTO)

            mPrinter.addFeedLine(1)
            textData.append(data!!.get(0).restaurant_id+"\n")
            textData.append(context.getString(R.string.order_coming_from)+"\n")
            if (data.get(0).restaurant_site != null) textData.append(data.get(0).restaurant_site+"\n")
            textData.append("----------------------------------------\n")

            textData.append(context.getString(R.string.order_no) + "${data.get(0).order_no}"+"\n")
            mPrinter.addText(textData.toString())
            textData.delete(0, textData.length)
            mPrinter.addTextStyle(0,0,1,Printer.COLOR_1)
            textData.append(if (data.get(0).shipping.capitalize().toUpperCase() == "DELIVERY") context.getString(R.string.delivery) else context.getString(R.string.pick_up))
            textData.append("\n")
            val calculatedTime = ConversionUtils.getCalculatedTime(data.get(0).expected_time, "yyyy-MM-dd HH:mm:ss")
            val expected_time = SimpleDateFormat("HH:mm:ss yyyy-MM-dd").format(calculatedTime)
            textData.append(expected_time+"\n")
            textData.append(if (data.get(0).requirement?.capitalize()?.toUpperCase() == "ASAP") context.getString(R.string.asap) else context.getString(R.string.preorder))
            textData.append("\n")
            textData.append("----------------------------------------\n")
            mPrinter.addText(textData.toString())
            textData.delete(0, textData.length)


            // Add product details:
            for (i in 0..data.get(0).order_products_details!!.size - 1) {
                mPrinter.addTextStyle(0,0,1,Printer.COLOR_1)
                mPrinter.addTextAlign(Printer.ALIGN_CENTER)
                //--- product name ---
                val item :String= padLine(
                        partOne = data.get(0).order_products_details!!.get(i).quantity + " x " +
                                data.get(0).order_products_details!!.get(i).products.product_no +
                                data.get(0).order_products_details!!.get(i).products.p_name,
                        partTwo = ConversionUtils.convertCurrencyToDanish(data.get(0).order_products_details!!.get(i).p_price),
                        columnsPerLine = 40
                )
                textData.append(item +"\n")
                mPrinter.addText(textData.toString())
                textData.delete(0, textData.length)
                mPrinter.addTextStyle(0,0,0,Printer.COLOR_1)
                mPrinter.addTextAlign(Printer.ALIGN_LEFT)

                // --- ingradient ---
                if (data.get(0).order_products_details?.get(i)?.removed_ingredients?.size != null) {

                    for (j in 0..data.get(0).order_products_details!!.get(i).removed_ingredients!!.size - 1) {
                        textData.append("        -" + data.get(0).order_products_details!!.get(i).removed_ingredients!!.get(j).ingredient_name+"\n")
                    }
                }


                // --- attributes ---
                if (data.get(0).order_products_details!!.get(i).products.is_attributes == "1") {

                    // with extratoppings
                    for (k in 0..data.get(0).order_products_details!!.get(i).ordered_product_attributes!!.size - 1) {

                        // Add attribute Header---
                        textData.append("        "+data.get(0).order_products_details!!.get(i).ordered_product_attributes!!.get(k).attribute_value_name+"\n")

                        // Add attribute of sizes---
                        for (l in 0..data.get(0).order_products_details!!.get(i).ordered_product_attributes!!.get(k).order_product_extra_topping_group!!.size - 1) {
                            textData.append("        +" + data.get(0).order_products_details!!.get(i).ordered_product_attributes!!.get(k).order_product_extra_topping_group!!.get(l).ingredient_name+"\n")
                        }
                    }


                } else {

                    // Without extratoppings -- Add attribute of sizes---

                    for (l in 0..data.get(0).order_products_details!!.get(i).order_product_extra_topping_group!!.size - 1) {
                        textData.append("        +" + data.get(0).order_products_details!!.get(i).order_product_extra_topping_group!!.get(l).ingredient_name+"\n")
                    }
                }
                mPrinter.addText(textData.toString())
                textData.delete(0, textData.length)

            }


            //Add price:
            mPrinter.addTextAlign(Printer.ALIGN_CENTER)
            textData.append("----------------------------------------\n")
            if (data.get(0).order_total != null && data.get(0).order_total != "0" && data.get(0).order_total != "0.00") {
                textData.append(padLine(context.getString(R.string.subtotal),ConversionUtils.convertCurrencyToDanish(data.get(0).order_total!!),40)+"\n")
            }
            if (data.get(0).upto_min_shipping != null && data.get(0).upto_min_shipping != "0.00" && data.get(0).upto_min_shipping != "0") {
                textData.append(padLine(context.getString(R.string.restupto),ConversionUtils.convertCurrencyToDanish(data.get(0).upto_min_shipping!!),40)+"\n")
            }
            if (data.get(0).shipping_costs != null && data.get(0).shipping_costs != "0" && data.get(0).shipping_costs != "0.00") {
                textData.append( padLine(context.getString(R.string.shipping),ConversionUtils.convertCurrencyToDanish(data.get(0).shipping_costs!!),40)+"\n")
            }

            if (data.get(0).additional_charge != null && data.get(0).additional_charge != "0" && data.get(0).additional_charge != "0.00") {
                textData.append(padLine(context.getString(R.string.additional),ConversionUtils.convertCurrencyToDanish(data.get(0).additional_charge!!),40)+"\n")
            }
            if (data.get(0).discount_amount != null && data.get(0).discount_amount != "0" && data.get(0).discount_amount != "0.00") {
                textData.append(padLine(context.getString(R.string.discount),ConversionUtils.convertCurrencyToDanish(data.get(0).discount_amount!!),40)+"\n")
            }
            mPrinter.addText(textData.toString())
            textData.delete(0, textData.length)

            if (data.get(0).total_to_pay != null && data.get(0).total_to_pay != "0" && data.get(0).total_to_pay != "0.00") {
                mPrinter.addFeedLine(1)
                mPrinter.addTextStyle(0,0,1,Printer.COLOR_1)
                textData.append(padLine(context.getString(R.string.total),ConversionUtils.convertCurrencyToDanish(data.get(0).total_to_pay!!),40)+"\n")
                mPrinter.addText(textData.toString())
                textData.delete(0, textData.length)
            }

            //Address and name:
            mPrinter.addTextStyle(0,0,0,Printer.COLOR_1)
            textData.append("----------------------------------------\n")
            textData.append(data.get(0).first_name+"\n")
            textData.append(data.get(0).address+"\n")
            mPrinter.addText(textData.toString())
            textData.delete(0, textData.length)
            mPrinter.addTextStyle(0,0,1,Printer.COLOR_1)
            textData.append(context.getString(R.string.phone)+" "+data.get(0).telephone_no+"\n")
            mPrinter.addText(textData.toString())
            textData.delete(0, textData.length)
            mPrinter.addTextStyle(0,0,0,Printer.COLOR_1)
            textData.append(context.getString(R.string.pre_order)+" "+data.get(0).previous_order+"\n")
            textData.append(context.getString(R.string.distance_km)+" "+data.get(0).distance+"\n")
            if (data.get(0).shipping_remark != null) {
                textData.append("${context.getString(R.string.note)} ${data.get(0).shipping_remark}"+"\n")
            }
            textData.append("----------------------------------------\n")
            mPrinter.addText(textData.toString())
            textData.delete(0, textData.length)

            //Not Paid:
            mPrinter.addTextStyle(0,0,1,Printer.COLOR_1)
            mPrinter.addTextSize(2,1)
            textData.append(if (data.get(0).payment_status.capitalize().toUpperCase() == "NOT PAID") context.getString(R.string.not_paid) else context.getString(R.string.paid))
            textData.append("\n")
            mPrinter.addText(textData.toString())
            textData.delete(0, textData.length)
            mPrinter.addTextSize(1,1)
            textData.append(if (data.get(0).paymethod == "1") context.getString(R.string.online_payment) else context.getString(R.string.cash_payment)+"\n")
            mPrinter.addText(textData.toString())
            textData.delete(0, textData.length)
            mPrinter.addTextStyle(0,0,0,Printer.COLOR_1)
            textData.append("----------------------------------------\n")
            mPrinter.addText(textData.toString())
            textData.delete(0, textData.length)

            //Accepted time:
            mPrinter.addTextSize(2,1)
            mPrinter.addTextStyle(0,0,1,Printer.COLOR_1)
            textData.append(if (data.get(0).order_status.capitalize().toUpperCase() == "ACCEPTED") context.getString(R.string.accepted_time)
            else if (data.get(0).order_status.capitalize().toUpperCase() == "REJECTED") context.getString(R.string.rejected_time) else data.get(0).order_status)
            textData.append("\n")

            if (data.get(0).order_status == "Accepted") {
                val calculatedTime = ConversionUtils.getCalculatedTime(data.get(0).pickup_delivery_time, "yyyy-MM-dd HH:mm:ss")
                val pickup_delivery_time = SimpleDateFormat("HH:mm:ss yyyy-MM-dd").format(calculatedTime)
                textData.append(pickup_delivery_time+"\n")
            } else {
                if (data.get(0).reject_reason != null) {
                    textData.append(data.get(0).reject_reason+"\n")
                }
            }
            mPrinter.addText(textData.toString())
            textData.delete(0, textData.length)

            //Powered by:
            mPrinter.addTextStyle(0,0,0,Printer.COLOR_1)
            mPrinter.addTextSize(1,1)
            textData.append("----------------------------------------\n")
            mPrinter.addText(textData.toString())
            textData.delete(0, textData.length)
            textData.append("Powered by - EatMore.dk")
            textData.append("\n")
            mPrinter.addText(textData.toString())
            textData.delete(0, textData.length)
            mPrinter.addFeedLine(1)
            mPrinter.addText(textData.toString())
            textData.delete(0, textData.length)
            Log.e("print receipt success","---")

            //cut the page:
            try{
                mPrinter.addCut(Printer.CUT_FEED)
            }catch (e : Exception){
                Log.e(TAG,"cut exception---"+e.message)
            }
        }catch (e : Exception){
            Log.e(TAG,e.message)
            return false
        }

        return true

    }


    fun padLine( partOne: String?, partTwo: String?, columnsPerLine: Int): String {
        var partOne = partOne
        var partTwo = partTwo
        if (partOne == null) {
            partOne = ""
        }
        if (partTwo == null) {
            partTwo = ""
        }
        val concat: String
        if (partOne.length + partTwo.length > columnsPerLine) {
            concat = "$partOne $partTwo"
        } else {
            val padding = columnsPerLine - (partOne.length + partTwo.length)
            concat = partOne + repeat(" ", padding) + partTwo
        }
        return concat
    }

    /** utility: string repeat  */
    fun repeat(str: String, i: Int): String {
        return String(CharArray(i)).replace("\u0000", str)
    }




    fun showException(e: Exception, method: String, context: Context) {
        var msg = ""
        if (e is Epos2Exception) {
            msg = String.format(
                    "%s\n\t%s\n%s\n\t%s",
                    context.getString(R.string.title_err_code),
                    getEposExceptionText(e.errorStatus,context),
                    context.getString(R.string.title_err_method),
                    method)
        } else {
            msg = e.toString()
        }
        // set default msg
        show(context.getString(R.string.please_check_printer), context)
    }

    fun showResult(code: Int, errMsg: String, context: Context) {
        var msg = ""
        if (errMsg.isEmpty()) {
            msg = String.format(
                    "\t%s\n\t%s\n",
                    context.getString(R.string.title_msg_result),
                    getCodeText(code))
        } else {
            msg = String.format(
                    "\t%s\n\t%s\n\n\t%s\n\t%s\n",
                    context.getString(R.string.title_msg_result),
                    getCodeText(code),
                    context.getString(R.string.title_msg_description),
                    errMsg)
        }
      //  show(msg, context)
    }

    fun showMsg(msg: String, context: Context) {
        show(msg, context)
    }

    private fun show(msg: String, context: Context) {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setMessage(msg)
        alertDialog.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, whichButton -> return@OnClickListener })
        alertDialog.create()
        alertDialog.show()
    }

    private fun getEposExceptionText(state: Int, context: Context): String {
        var return_text = ""
        when (state) {
            Epos2Exception.ERR_PARAM -> return_text = "ERR_PARAM"
            Epos2Exception.ERR_CONNECT -> return_text = "ERR_CONNECT"
            Epos2Exception.ERR_TIMEOUT -> return_text = "ERR_TIMEOUT"
            Epos2Exception.ERR_MEMORY -> return_text = "ERR_MEMORY"
            Epos2Exception.ERR_ILLEGAL -> return_text = "ERR_ILLEGAL"
            Epos2Exception.ERR_PROCESSING -> return_text = "ERR_PROCESSING"
            Epos2Exception.ERR_NOT_FOUND -> return_text = "ERR_NOT_FOUND"
            Epos2Exception.ERR_IN_USE -> return_text = "ERR_IN_USE"
            Epos2Exception.ERR_TYPE_INVALID -> return_text = "ERR_TYPE_INVALID"
            Epos2Exception.ERR_DISCONNECT -> return_text = "ERR_DISCONNECT"
            Epos2Exception.ERR_ALREADY_OPENED -> return_text = "ERR_ALREADY_OPENED"
            Epos2Exception.ERR_ALREADY_USED -> return_text = "ERR_ALREADY_USED"
            Epos2Exception.ERR_BOX_COUNT_OVER -> return_text = "ERR_BOX_COUNT_OVER"
            Epos2Exception.ERR_BOX_CLIENT_OVER -> return_text = "ERR_BOX_CLIENT_OVER"
            Epos2Exception.ERR_UNSUPPORTED -> return_text = "ERR_UNSUPPORTED"
            Epos2Exception.ERR_FAILURE -> return_text = "ERR_FAILURE"
            else -> return_text = String.format("%d", state)
        }
        return return_text
    }

    private fun getCodeText(state: Int): String {
        var return_text = ""
        when (state) {
            Epos2CallbackCode.CODE_SUCCESS -> return_text = "PRINT_SUCCESS"
            Epos2CallbackCode.CODE_PRINTING -> return_text = "PRINTING"
            Epos2CallbackCode.CODE_ERR_AUTORECOVER -> return_text = "ERR_AUTORECOVER"
            Epos2CallbackCode.CODE_ERR_COVER_OPEN -> return_text = "ERR_COVER_OPEN"
            Epos2CallbackCode.CODE_ERR_CUTTER -> return_text = "ERR_CUTTER"
            Epos2CallbackCode.CODE_ERR_MECHANICAL -> return_text = "ERR_MECHANICAL"
            Epos2CallbackCode.CODE_ERR_EMPTY -> return_text = "ERR_EMPTY"
            Epos2CallbackCode.CODE_ERR_UNRECOVERABLE -> return_text = "ERR_UNRECOVERABLE"
            Epos2CallbackCode.CODE_ERR_FAILURE -> return_text = "ERR_FAILURE"
            Epos2CallbackCode.CODE_ERR_NOT_FOUND -> return_text = "ERR_NOT_FOUND"
            Epos2CallbackCode.CODE_ERR_SYSTEM -> return_text = "ERR_SYSTEM"
            Epos2CallbackCode.CODE_ERR_PORT -> return_text = "ERR_PORT"
            Epos2CallbackCode.CODE_ERR_TIMEOUT -> return_text = "ERR_TIMEOUT"
            Epos2CallbackCode.CODE_ERR_JOB_NOT_FOUND -> return_text = "ERR_JOB_NOT_FOUND"
            Epos2CallbackCode.CODE_ERR_SPOOLER -> return_text = "ERR_SPOOLER"
            Epos2CallbackCode.CODE_ERR_BATTERY_LOW -> return_text = "ERR_BATTERY_LOW"
            Epos2CallbackCode.CODE_ERR_TOO_MANY_REQUESTS -> return_text = "ERR_TOO_MANY_REQUESTS"
            Epos2CallbackCode.CODE_ERR_REQUEST_ENTITY_TOO_LARGE -> return_text = "ERR_REQUEST_ENTITY_TOO_LARGE"
            else -> return_text = String.format("%d", state)
        }
        return return_text
    }


     fun printData(mPrinter : Printer?, context: Context): Boolean {
        if (mPrinter == null) {
            return false
        }

        if (!connectPrinter(mPrinter,context)) {
            return false
        }

        val status = mPrinter.getStatus()

        dispPrinterWarnings(status,context)

        if (!isPrintable(status)) {
            EpsonUtils.showMsg(makeErrorMessage(status,context),context)
            try {
                mPrinter.disconnect()
            } catch (ex: Exception) {
                // Do nothing
            }

            return false
        }

        try {
            mPrinter.sendData(Printer.PARAM_DEFAULT)
        } catch (e: Exception) {
            EpsonUtils.showException(e, "sendData", context)
            try {
                mPrinter.disconnect()
            } catch (ex: Exception) {
                // Do nothing
            }

            return false
        }

        return true
    }

     fun connectPrinter(mPrinter : Printer?, context: Context): Boolean {
        var isBeginTransaction = false

        if (mPrinter == null) {
            return false
        }

        try {
            // mPrinter!!.connect("TCP:64:EB:8C:2C:B7:A3", Printer.PARAM_DEFAULT)
            mPrinter.connect(PreferenceUtil.getString(PreferenceUtil.LOCAL_PRINTER_ADDRESS, "").toString().trim(), Printer.PARAM_DEFAULT)
            Log.e(OrderDetails.TAG,"ip is: "+ PreferenceUtil.getString(PreferenceUtil.LOCAL_PRINTER_ADDRESS, "").toString().trim())

        } catch (e: Exception) {
            EpsonUtils.showException(e, "connect", context)
            Log.e("printer ip exception ","---"+e.message)
            return false
        }

        try {
            mPrinter.beginTransaction()
            isBeginTransaction = true
        } catch (e: Exception) {
            EpsonUtils.showException(e, "beginTransaction", context)
        }

        if (isBeginTransaction == false) {
            try {
                mPrinter.disconnect()
            } catch (e: Epos2Exception) {
                // Do nothing
                return false
            }

        }

        return true
    }

     fun isPrinterOnline (mPrinter : Printer?, context: Context): Boolean {
        var isBeginTransaction = false

        if (mPrinter == null) {
            return false
        }

        try {
            // mPrinter!!.connect("TCP:64:EB:8C:2C:B7:A3", Printer.PARAM_DEFAULT)
            Log.e("connected ip :",""+PreferenceUtil.getString(PreferenceUtil.LOCAL_PRINTER_ADDRESS,""))

            mPrinter.connect(PreferenceUtil.getString(PreferenceUtil.LOCAL_PRINTER_ADDRESS,""),Printer.PARAM_DEFAULT)

        } catch (e: Exception) {
            Log.e("printer ip exception ","---"+e.message)
            return false
        }

        try {
            mPrinter.beginTransaction()
            isBeginTransaction = true
        } catch (e: Exception) {
        }

        if (isBeginTransaction == false) {
            try {
                mPrinter.disconnect()
            } catch (e: Epos2Exception) {
                // Do nothing
                return false
            }
        }
        return true
    }

     fun dispPrinterWarnings(status: PrinterStatusInfo?,context: Context) {
        var warningsMsg = ""

        if (status == null) {
            return
        }

        if (status.paper == Printer.PAPER_NEAR_END) {
            warningsMsg += context.getString(R.string.handlingmsg_warn_receipt_near_end)
        }

        if (status.batteryLevel == Printer.BATTERY_LEVEL_1) {
            warningsMsg += context.getString(R.string.handlingmsg_warn_battery_near_end)
        }
         Log.e("printer warnings: ",""+warningsMsg)
     //   Toast.makeText(context,warningsMsg, Toast.LENGTH_SHORT).show()
    }

     fun isPrintable(status: PrinterStatusInfo?): Boolean {
        if (status == null) {
            return false
        }

        if (status.connection == Printer.FALSE) {
            return false
        } else if (status.online == Printer.FALSE) {
            return false
        } else {
        }//print_black available

        return true
    }

     fun makeErrorMessage(status: PrinterStatusInfo,context: Context): String {
        var msg = ""

        if (status.online == Printer.FALSE) {
            msg += context.getString(R.string.handlingmsg_err_offline)
        }
        if (status.connection == Printer.FALSE) {
            msg += context.getString(R.string.handlingmsg_err_no_response)
        }
        if (status.coverOpen == Printer.TRUE) {
            msg += context.getString(R.string.handlingmsg_err_cover_open)
        }
        if (status.paper == Printer.PAPER_EMPTY) {
            msg += context.getString(R.string.handlingmsg_err_receipt_end)
        }
        if (status.paperFeed == Printer.TRUE || status.panelSwitch == Printer.SWITCH_ON) {
            msg += context.getString(R.string.handlingmsg_err_paper_feed)
        }
        if (status.errorStatus == Printer.MECHANICAL_ERR || status.errorStatus == Printer.AUTOCUTTER_ERR) {
            msg += context.getString(R.string.handlingmsg_err_autocutter)
            msg += context.getString(R.string.handlingmsg_err_need_recover)
        }
        if (status.errorStatus == Printer.UNRECOVER_ERR) {
            msg += context.getString(R.string.handlingmsg_err_unrecover)
        }
        if (status.errorStatus == Printer.AUTORECOVER_ERR) {
            if (status.autoRecoverError == Printer.HEAD_OVERHEAT) {
                msg += context.getString(R.string.handlingmsg_err_overheat)
                msg += context.getString(R.string.handlingmsg_err_head)
            }
            if (status.autoRecoverError == Printer.MOTOR_OVERHEAT) {
                msg += context.getString(R.string.handlingmsg_err_overheat)
                msg += context.getString(R.string.handlingmsg_err_motor)
            }
            if (status.autoRecoverError == Printer.BATTERY_OVERHEAT) {
                msg += context.getString(R.string.handlingmsg_err_overheat)
                msg += context.getString(R.string.handlingmsg_err_battery)
            }
            if (status.autoRecoverError == Printer.WRONG_PAPER) {
                msg += context.getString(R.string.handlingmsg_err_wrong_paper)
            }
        }
        if (status.batteryLevel == Printer.BATTERY_LEVEL_0) {
            msg += context.getString(R.string.handlingmsg_err_battery_real_end)
        }

        return msg
    }

    private fun disconnectPrinter(mPrinter : Printer?, context: Context) {
        if (mPrinter == null) {
            return
        }

        try {
            mPrinter.endTransaction()
        } catch (e: Exception) {
            (context as OrderDetails).activity!!.runOnUiThread(Runnable { EpsonUtils.showException(e, "endTransaction", context!!) })
        }

        try {
            mPrinter.disconnect()
        } catch (e: Exception) {
            (context as OrderDetails).activity!!.runOnUiThread(Runnable { EpsonUtils.showException(e, "disconnect", context!!) })
        }

       // finalizeObject()
    }



}
