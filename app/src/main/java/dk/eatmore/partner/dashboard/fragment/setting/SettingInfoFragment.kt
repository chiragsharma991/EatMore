package dk.eatmore.partner.dashboard.fragment.setting

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.gson.JsonObject
import dk.eatmore.partner.R
import dk.eatmore.partner.dashboard.fragment.order.RecordOfToday
import dk.eatmore.partner.dashboard.main.MainActivity
import dk.eatmore.partner.model.GetReason
import dk.eatmore.partner.model.OpeninghoursItem
import dk.eatmore.partner.rest.ApiCall
import dk.eatmore.partner.storage.PreferenceUtil
import dk.eatmore.partner.utils.BaseFragment
import dk.eatmore.partner.utils.DialogUtils
import kotlinx.android.synthetic.main.fragment_settinginfo.*
import kotlinx.android.synthetic.main.layout_comment_box.view.*
import kotlinx.android.synthetic.main.layout_progressbar.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import android.widget.CompoundButton
import android.os.PowerManager
import android.content.Context.POWER_SERVICE
import android.support.v4.content.ContextCompat
import android.widget.Toast
import dk.eatmore.partner.utils.Custom_data


class SettingInfoFragment : BaseFragment(), View.OnClickListener {

    var r_key = ""
    var r_token = ""
    var set_reopen_rest_clickable:Boolean =false



    override fun onClick(v: View?) {
        reasonToolbar()
        set_reason_view.visibility = View.VISIBLE
        set_general_view.visibility = View.GONE
        progress_bar.visibility = View.VISIBLE

        callAPI(ApiCall.allRecords(r_key = r_key, r_token = r_token), object : BaseFragment.OnApiCallInteraction {
            override fun <T> onSuccess(body: T?) {
                // all list of reasons.
                if ((body as GetReason).status) {
                    var list: List<OpeninghoursItem> = (body as GetReason).Openinghours!!
                    val listOfreason = ArrayList<String>()
                    for (i in 0..list.size - 1) {
                        listOfreason.add(list.get(i).reason)
                    }
                    val arrayAdapter  = ArrayAdapter(
                            activity,
                            android.R.layout.simple_list_item_1,
                            listOfreason)
                    list_view.adapter = arrayAdapter
                    list_view.setOnItemClickListener{ _: AdapterView<*>, _: View, position: Int, _: Long ->
                        addeditReasonDialog(getString(R.string.edit_reject_reason),
                                listOfreason.get(position),
                                list.get(position).or_id,
                                "UPDATE")
                    }
                }else{
                    set_reason_view.visibility = View.GONE
                    set_general_view.visibility = View.GONE
                    progress_bar.visibility = View.GONE

                }
            }
            override fun onFail(error : Int) {

                when(error){
                    404 ->{
                        showSnackBar(getString(R.string.error_404))
                        progress_bar.visibility = View.GONE
                        log(RecordOfToday.TAG, "api call failed...")
                    }
                    100 ->{
                        showSnackBar(getString(R.string.internet_not_available))

                    }
                }




            }
        })

    }

    override fun getLayout(): Int {
        return R.layout.fragment_settinginfo
    }


    override fun initView(view: View?, savedInstanceState: Bundle?) {

        generalToolbar()
        r_key = PreferenceUtil.getString(PreferenceUtil.R_KEY, "")!!
        r_token = PreferenceUtil.getString(PreferenceUtil.R_TOKEN, "")!!
        set_reason_view.visibility = View.GONE
        set_general_view.visibility = View.VISIBLE
        progress_bar.visibility = View.GONE
        set_reject_view.setOnClickListener(this)
        set_keepscreen_switch.setChecked(if(PreferenceUtil.getBoolean(PreferenceUtil.KEEP_SCREEN_ON,false)) true else false)
        set_keepscreen_on.setOnClickListener{
            if(PreferenceUtil.getBoolean(PreferenceUtil.KEEP_SCREEN_ON,false)){
                PreferenceUtil.putValue(PreferenceUtil.KEEP_SCREEN_ON,false)
                PreferenceUtil.save()
                set_keepscreen_switch.setChecked(false)
                (activity as MainActivity).keepScreenOn(false)
                Toast.makeText(context,R.string.wake_lock_off,Toast.LENGTH_SHORT).show()

            }
            else {
                PreferenceUtil.putValue(PreferenceUtil.KEEP_SCREEN_ON,true)
                PreferenceUtil.save()
                set_keepscreen_switch.setChecked(true)
                (activity as MainActivity).keepScreenOn(true)
                Toast.makeText(context,R.string.wake_lock_on,Toast.LENGTH_SHORT).show()
            }
        }

        closeRestaurant()


    }

    private fun closeRestaurant() {

        set_reopen_rest_txt.text= if(PreferenceUtil.getBoolean(PreferenceUtil.KSTATUS,true)) getString(R.string.close_rest) else getString(R.string.reopen_rest)
        if(MainActivity.kShouldAllowCloseRestDay){
            set_reopen_rest_clickable=true
            set_reopen_rest_txt.setTextColor(ContextCompat.getColor(context!!,R.color.black))

        }else{
            set_reopen_rest_clickable=false
            set_reopen_rest_txt.setTextColor(ContextCompat.getColor(context!!,R.color.border_gray))
        }
        set_reopen_rest.setOnClickListener{

            if(set_reopen_rest_clickable){


                if(PreferenceUtil.getBoolean(PreferenceUtil.KSTATUS,true)){
                    DialogUtils.openDialog ( context!!,getString(R.string.would_you_like_toclose), getString(R.string.are_you_sure_toclose),
                            getString(R.string.yes),getString(R.string.cancel), ContextCompat.getColor(context!!,R.color.theme_color), object : DialogUtils.OnDialogClickListener {

                        override fun onPositiveButtonClick(position: Int) {

                            callAPI( ApiCall.closedRestDay(r_key,r_token),object : BaseFragment.OnApiCallInteraction{

                                    override fun <T> onSuccess(body: T?) {
                                        val json= body as JsonObject  // please be mind you are using jsonobject(Gson)

                                        if (json.get("status").asBoolean) {
                                            set_reopen_rest_txt.text=getString(R.string.reopen_rest)
                                            set_reopen_rest_clickable=true
                                            set_reopen_rest_txt.setTextColor(ContextCompat.getColor(context!!,R.color.black))
                                            PreferenceUtil.putValue(PreferenceUtil.KSTATUS,false)
                                            PreferenceUtil.save()
                                            showSnackBar(json.get("msg").asString)
                                        }
                                    }

                                    override fun onFail(error : Int) {
                                        log(MainActivity.TAG,"error "+error)
                                        showSnackBar(getString(R.string.error_404))

                                    }
                                })

                        }

                        override fun onNegativeButtonClick() {

                        }
                    })

                }else{

                    DialogUtils.openDialog ( context!!,getString(R.string.would_you_like_toreopen), getString(R.string.are_you_sure_toreopen),
                            getString(R.string.yes),getString(R.string.cancel), ContextCompat.getColor(context!!,R.color.theme_color), object : DialogUtils.OnDialogClickListener {
                        override fun onPositiveButtonClick(position: Int) {


                                callAPI( ApiCall.resetRestDay(r_key,r_token),object : BaseFragment.OnApiCallInteraction{

                                    override fun <T> onSuccess(body: T?) {
                                        val json= body as JsonObject  // please be mind you are using jsonobject(Gson)

                                        if (json.get("status").asBoolean) {
                                            set_reopen_rest_txt.text=getString(R.string.close_rest)
                                            set_reopen_rest_clickable=true
                                            set_reopen_rest_txt.setTextColor(ContextCompat.getColor(context!!,R.color.black))
                                            PreferenceUtil.putValue(PreferenceUtil.KSTATUS,true)
                                            PreferenceUtil.save()
                                            showSnackBar(json.get("msg").asString)

                                        }
                                    }

                                    override fun onFail(error : Int) {
                                        log(MainActivity.TAG,"error "+error)
                                        showSnackBar(getString(R.string.error_404))

                                    }
                                })




                        }

                        override fun onNegativeButtonClick() {

                        }
                    })

                }



            }
        }


    }

    companion object {
        val TAG= "SET"
        fun newInstance() : SettingInfoFragment {
            return SettingInfoFragment()
        }


    }





    private fun generalToolbar() {

      //  toolbar.removeAllViewsInLayout()
        img_toolbar_back.setImageResource(R.drawable.ic_menu)
        txt_toolbar.text = getString(R.string.settings)
        img_toolbar_back.setOnClickListener {

            if( set_general_view.visibility == View.VISIBLE){
                var mainActivity = getActivityBase() as MainActivity
                mainActivity.isOpenDrawer(true)
            }else{
                toolbar.menu.clear()
                img_toolbar_back.setImageResource(R.drawable.ic_menu)
                txt_toolbar.text = getString(R.string.settings)
                set_reason_view.visibility = View.GONE
                set_general_view.visibility = View.VISIBLE
                progress_bar.visibility = View.GONE
            }


        }

        /*if(PreferenceUtil.getBoolean(PreferenceUtil.IS_LOGIN, false))
            toolbar.inflateMenu(R.menu.home_menu)*/


    }

    private fun reasonToolbar() {
        toolbar.inflateMenu(R.menu.add_reason)
        txt_toolbar.text = getString(R.string.reject_reasons)
        img_toolbar_back.setImageResource(R.drawable.ic_back)
        toolbar.setOnMenuItemClickListener { item ->
            when(item.itemId){
                R.id.action_add -> {
                    addeditReasonDialog(getString(R.string.add_reject_reason), "","", getString(R.string.add))
                }
            }
            true
        }


    }

    private fun addeditReasonDialog(title: String, selectedReason: String, or_id: String, case: String){

        val li = LayoutInflater.from(activity)
        val view = li.inflate(R.layout.layout_comment_box, null)
        var dialog= DialogUtils.createDialog(activity!!,view)
        dialog.setOnDismissListener{ hideKeyboard()  }
        view.dialog_txt_error!!.visibility = View.GONE
        view.dialog_edt_comment.setText( if(selectedReason != "") selectedReason else "")
        view.dialog_txt_title.text = title
        view.dialog_txt_ok.setOnClickListener{
            if (TextUtils.isEmpty(view.dialog_edt_comment.text.trim().toString()))
                view.dialog_txt_error.visibility = View.VISIBLE
            else{
                dialog.dismiss()
                when(case){
                    "UPDATE" ->{

                        callAPI(ApiCall.updateOrder(r_key = r_key, r_token = r_token,
                                reason =view.dialog_edt_comment.text.trim().toString(),
                                action_by = PreferenceUtil.getString(PreferenceUtil.USER_ID, "").toString()
                                ,or_id = or_id ),
                                object : BaseFragment.OnApiCallInteraction {

                                    override fun <T> onSuccess(body: T?) {
                                        // all list of reasons.
                                        val json= body as JsonObject  // please be mind you are using jsonobject(Gson)
                                        if (json.get("status").asBoolean) {
                                            showSnackBar(json.get("msg").asString)
                                        }else{
                                            showSnackBar(getString(R.string.error_404))
                                        }
                                    }
                                    override fun onFail(error : Int) {

                                        when(error){
                                            404 ->{
                                                showSnackBar(getString(R.string.error_404))
                                            }
                                            100 ->{
                                                showSnackBar(getString(R.string.internet_not_available))
                                            }
                                        }



                                    }
                                })
                    }
                    "ADD"    ->{
                        callAPI(ApiCall.createOrder(r_key = r_key, r_token = r_token,
                                reason =view.dialog_edt_comment.text.trim().toString(),
                                action_by = PreferenceUtil.getString(PreferenceUtil.USER_ID, "").toString()),
                                object : BaseFragment.OnApiCallInteraction {

                                    override fun <T> onSuccess(body: T?) {
                                        // all list of reasons.
                                        val json= body as JsonObject  // please be mind you are using jsonobject(Gson)
                                        if (json.get("status").asBoolean) {
                                            showSnackBar(json.get("msg").asString)
                                        }else{
                                            showSnackBar(getString(R.string.error_404))
                                        }
                                    }
                                    override fun onFail(error : Int) {

                                        when(error){
                                            404 ->{
                                                showSnackBar(getString(R.string.error_404))
                                            }
                                            100 ->{
                                                showSnackBar(getString(R.string.internet_not_available))
                                            }
                                        }



                                    }
                                })
                    }
                }


            }

        }
        dialog.show()

    }


    override fun handleBackButton(): Boolean {
        return false

    }

}