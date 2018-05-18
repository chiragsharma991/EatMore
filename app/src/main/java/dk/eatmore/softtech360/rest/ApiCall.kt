package dk.eatmore.softtech360.rest

import android.util.Log
import com.google.gson.JsonObject
import dk.eatmore.softtech360.model.Order
import retrofit2.Call
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log
import android.R.attr.password
import android.provider.Telephony.Carriers.PASSWORD
import okhttp3.FormBody
import okhttp3.RequestBody




class ApiCall {
   // username,password_hash,type,login_type

    companion object {

        private fun getApiInterface(): ApiInterface {
            return ApiClient.getClient()!!.create(ApiInterface::class.java)
        }

        fun login(username : String, password_hash : String, type : String,login_type : String) : Call<JsonObject> {
            return getApiInterface().setLogin(username, password_hash, type,login_type)
        }

        fun myOrder(to : String, from : String, r_key : String,r_token : String) : Call<Order> {

            /*     {
                     "r_token":"w5oRqFiAXTBB3hwpixAORbg_BwUj0EMQ07042017114812",
                     "r_key":"fcARlrbZFXYee1W6eYEIA0VRlw7MgV4o07042017114812",
                     "order_to":"2018-05-18",
                     "order_from":"2018-05-18"


                 }*/

            val postParam = JsonObject()
            postParam.addProperty("r_token","w5oRqFiAXTBB3hwpixAORbg_BwUj0EMQ07042017114812")
            postParam.addProperty("r_key","fcARlrbZFXYee1W6eYEIA0VRlw7MgV4o07042017114812")
            postParam.addProperty("order_to","2018-05-18")
            postParam.addProperty("order_from","2018-05-08")




            return getApiInterface().myOrder(postParam)
        }

    }




}