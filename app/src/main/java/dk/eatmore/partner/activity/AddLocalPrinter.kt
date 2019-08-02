package dk.eatmore.partner.activity

import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.epson.epos2.Epos2Exception
import com.epson.epos2.discovery.DeviceInfo
import com.epson.epos2.discovery.Discovery
import com.epson.epos2.discovery.DiscoveryListener
import com.epson.epos2.discovery.FilterOption
import dk.eatmore.partner.R
import dk.eatmore.partner.dashboard.fragment.order.OrderDetails
import dk.eatmore.partner.dashboard.fragment.setting.AddPrinter
import dk.eatmore.partner.storage.PreferenceUtil
import dk.eatmore.partner.utils.BaseActivity
import dk.eatmore.partner.utils.EpsonUtils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.addprinter.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.row_print_item.*
import java.util.ArrayList
import java.util.HashMap

class AddLocalPrinter : BaseActivity(){

    var r_key = ""
    var r_token = ""
    var set_reopen_rest_clickable: Boolean = false
    private lateinit var mAdapter: AddPrinterAdapter
    private var mPrinterList: ArrayList<HashMap<String, String>> = ArrayList()
    private  var mFilterOption: FilterOption?=null
    private var showempty_view : Boolean =true


    companion object {

        val TAG = "AddLocalPrinter"
        fun newInstance(): AddLocalPrinter {
            return AddLocalPrinter()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addprinter)
        init(savedInstanceState)

    }

    fun init(savedInstancedState: Bundle?) {

        initToolbar()
        printer_error.visibility= View.GONE
        Handler().postDelayed({
            if(showempty_view)
                printer_error.visibility= View.VISIBLE
        },1000)

        r_key = PreferenceUtil.getString(PreferenceUtil.R_KEY, "")!!
        r_token = PreferenceUtil.getString(PreferenceUtil.R_TOKEN, "")!!
        mAdapter = AddPrinterAdapter(mPrinterList, object : AddPrinterAdapter.AdapterListener {
            override fun itemClicked(position: Int) {
                PreferenceUtil.putValue(PreferenceUtil.LOCAL_PRINTER_ADDRESS,mPrinterList[position]["Target"]!! )
                PreferenceUtil.save()
                mAdapter.notifyDataSetChanged()
            }
        })
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = mAdapter

        mFilterOption = FilterOption()
        mFilterOption!!.setDeviceType(Discovery.TYPE_PRINTER)
        mFilterOption!!.setEpsonFilter(Discovery.FILTER_NAME)
        try {
            Discovery.start(this, mFilterOption, mDiscoveryListener)
        } catch (e: Exception) {
            EpsonUtils.showException(e, "start", this)
        }



    }



    private fun initToolbar() {

        img_toolbar_back.setImageResource(R.drawable.ic_back)
        txt_toolbar.text = "Printer"
        img_toolbar_back.setOnClickListener {
          finish()
        }

    }


    private val mDiscoveryListener = object: DiscoveryListener {
        override fun onDiscovery(deviceInfo: DeviceInfo) {
            Log.e(TAG,"listner---")
            runOnUiThread(object:Runnable {
                @Synchronized public override fun run() {
                    val item = HashMap<String, String>()
                    item.put("PrinterName", deviceInfo.getDeviceName())
                    item.put("Target", deviceInfo.getTarget())
                    mPrinterList.add(item)
                    log(TAG,"ip :"+deviceInfo.getTarget())
                    // PreferenceUtil.putValue(PreferenceUtil.LOCAL_PRINTER_ADDRESS,deviceInfo.getTarget() )
                    // PreferenceUtil.save()
                    showempty_view=false
                    printer_error.visibility=View.GONE
                    mAdapter.notifyDataSetChanged()
                }
            })
        } }


    override fun onDestroy() {
        super.onDestroy()

        while (true) {
            try {
                Discovery.stop()
                break
            } catch (e: Epos2Exception) {
                if (e.errorStatus != Epos2Exception.ERR_PROCESSING) {
                    break
                }
            }
        }
        mFilterOption = null
    }



 }

internal class AddPrinterAdapter(val value: ArrayList<HashMap<String, String>>, val callback: AdapterListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var listner: AdapterListener
    private var list = value


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var vh: RecyclerView.ViewHolder? = null
        this.listner = callback
        val itemview = LayoutInflater.from(parent.context).inflate(R.layout.row_print_item, parent, false)
        vh = MyViewHolder(itemview)
        return vh
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.init(list[position])
            holder.itemview.setOnClickListener{
                listner.itemClicked(position)
            }

        }

    }


    class MyViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun init(value: HashMap<String, String>) {
            printer_type.text = value["PrinterName"]
            printer_address.text = value["Target"]
            if(PreferenceUtil.getString(PreferenceUtil.LOCAL_PRINTER_ADDRESS, "")!!.equals("")){
                checkmark.visibility=View.GONE
                connected.visibility=View.GONE
            }else{
                checkmark.visibility=View.VISIBLE
                connected.visibility=View.VISIBLE
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface AdapterListener {
        fun itemClicked(parentPosition: Int)
    }


}


