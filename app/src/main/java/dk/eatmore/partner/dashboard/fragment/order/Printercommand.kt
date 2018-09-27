package dk.eatmore.partner.dashboard.fragment.order

import android.app.ProgressDialog
import android.util.Log
import com.epson.epos2.printer.Printer
import com.epson.epos2.printer.PrinterStatusInfo
import com.epson.epos2.printer.ReceiveListener
import dk.eatmore.partner.R
import dk.eatmore.partner.model.DataItem
import dk.eatmore.partner.model.ProductDetails
import dk.eatmore.partner.rest.ApiCall
import dk.eatmore.partner.utils.BaseFragment
import dk.eatmore.partner.utils.EpsonUtils

abstract class Printercommand :BaseFragment(), ReceiveListener {
    private lateinit var data: List<DataItem>
    var mPrinter: Printer?=null
    private lateinit var progressBar: ProgressDialog



    fun fetchOrderDetails( r_key: String, r_token: String, order_id: String,isaccepted : Boolean) {

         if (isInternetAvailable()) {
             callAPI(ApiCall.viewRecords(r_key, r_token, order_id), object : BaseFragment.OnApiCallInteraction {

                 override fun <T> onSuccess(body: T?) {

                     if ((body as ProductDetails).status) {
                         log(RecordOfToday.TAG, "fetchOrderDetails--- " + isaccepted)
                         data = (body as ProductDetails).data!!
                         runPrintReceiptSequence(data,isaccepted)

                     } else {

                     }

                 }

                 override fun onFail(error: Int) {
                     when (error) {
                         404 -> {
                             showSnackBar(getString(R.string.error_404))

                         }
                         100 -> {
                             showSnackBar(getString(R.string.internet_not_available))
                         }

                     }
                 }

             })
         }else{
             showSnackBar(getString(R.string.internet_not_available))

         }

    }

    fun runPrintReceiptSequence(data: List<DataItem>,isaccepted : Boolean): Boolean {
        setprogressdialog()

        if (!initializeObject()) {
            log(RecordOfToday.TAG,"initializeObject failed---")
            progressBar.dismiss()
            return false
        }

        if (!EpsonUtils.createReceiptData(data,mPrinter!!,isaccepted,context)) {
            finalizeObject()
            log(RecordOfToday.TAG,"createReceiptData failed---")
            progressBar.dismiss()
            return false
        }

        if (!EpsonUtils.printData(mPrinter,context!!)) {
            finalizeObject()
            log(RecordOfToday.TAG,"printData failed---")
            progressBar.dismiss()
            return false
        }

   /*     if (!isPrinterconnected()) {
            Thread(Runnable { disconnectPrinter() }).start()

            //finalizeObject()
            log(RecordOfToday.TAG,"printData failed")
            return false
        }
        Log.e("print :","success...")
        try {
            mPrinter!!.disconnect()
        } catch (e: Exception) {
            activity!!.runOnUiThread(Runnable { EpsonUtils.showException(e, "disconnect", context!!) })
        }
*/
        return true
    }


     fun initializeObject(): Boolean {

        try {
            mPrinter = Printer(6,0,context)
        } catch (e: Exception) {
            EpsonUtils.showException(e, "Printer", context!!)
            return false
        }
        mPrinter!!.setReceiveEventListener(this)

        return true
    }

    private fun finalizeObject() {
        if (mPrinter == null) {
            return
        }

        mPrinter!!.clearCommandBuffer()

        mPrinter!!.setReceiveEventListener(null)

        mPrinter = null
    }

    fun isPrinterconnected() :Boolean{
        return EpsonUtils.isPrinterOnline(mPrinter,context!!)
    }

    private fun disconnectPrinter() {
        if (mPrinter == null) {
            return
        }

        try {
            mPrinter!!.endTransaction()
        } catch (e: Exception) {
            activity!!.runOnUiThread(Runnable { EpsonUtils.showException(e, "endTransaction", context!!) })
        }

        try {
            mPrinter!!.disconnect()
        } catch (e: Exception) {
            activity!!.runOnUiThread(Runnable { EpsonUtils.showException(e, "disconnect", context!!) })
        }

        finalizeObject()
    }

    override fun onPtrReceive(printerObj: Printer?, code: Int, status: PrinterStatusInfo?, printJobId: String?) {

        activity!!.runOnUiThread(Runnable {
            EpsonUtils.showResult(code, EpsonUtils.makeErrorMessage(status!!,context!!), context!!)
            EpsonUtils.dispPrinterWarnings(status,context!!)
            progressBar.dismiss()
            Thread(Runnable { disconnectPrinter() }).start()
        })
    }



    fun setprogressdialog(){

        progressBar = ProgressDialog(context)
        progressBar.setCancelable(false)
        progressBar.setMessage(getString(R.string.printing))
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressBar.setProgressNumberFormat(null)
        progressBar.setProgressPercentFormat(null)
        progressBar.isIndeterminate=true
        progressBar.show()
    }


}