package dk.eatmore.softtech360.rest

import com.google.gson.JsonObject
import dk.eatmore.softtech360.model.Order
import retrofit2.Call
import dk.eatmore.softtech360.dashboard.fragment.Task
import dk.eatmore.softtech360.model.GetReason
import org.json.JSONObject
import java.io.UnsupportedEncodingException


class ApiCall {
   // username,password_hash,type,login_type

    companion object {

        private fun getApiInterface(): ApiInterface {
            return ApiClient.getClient()!!.create(ApiInterface::class.java)
        }

        fun login(username : String, password_hash : String, type : String,login_type : String) : Call<JsonObject> {
            return getApiInterface().setLogin(username, password_hash, type,login_type)
        }

        fun myOrder(to: String, from: String, r_key: String, r_token: String) : Call<Order> {
            val postParam = JsonObject()
            postParam.addProperty("r_token",r_token)
            postParam.addProperty("r_key",r_key)
            postParam.addProperty("order_to",to)
            postParam.addProperty("order_from",from)
            return getApiInterface().myOrder(postParam)
        }

        fun allRecords(r_key: String, r_token: String) : Call<GetReason> {
            return getApiInterface().allRecords(r_token,r_key)
        }

        fun rejectOrders(r_key: String, r_token: String,order_no: String, reason: String, order_status: String) : Call<JsonObject> {
            return getApiInterface().rejectOrders(r_token,r_key,order_no,reason,order_status)
        }

        fun acceptOrders(r_key: String, r_token: String, order_no: String, pickup_delivery_time: String, order_status: String) : Call<JsonObject> {
            return getApiInterface().acceptOrders(r_token,r_key,order_no,pickup_delivery_time,order_status)
        }

        fun createOrder(r_key: String, r_token: String,reason: String, action_by: String) : Call<JsonObject> {
            return getApiInterface().createOrder(r_token,r_key,reason,action_by)
        }

        fun updateOrder(r_key: String, r_token: String,reason: String, action_by: String, or_id : String) : Call<JsonObject> {
            return getApiInterface().updateOrder(r_token,r_key,reason,action_by,or_id)
        }
    }




}