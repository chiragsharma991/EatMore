package dk.eatmore.partner.rest

import com.google.gson.JsonObject
import dk.eatmore.partner.dashboard.main.MainActivity
import dk.eatmore.partner.dashboard.main.OrderCounter
import dk.eatmore.partner.model.Order
import retrofit2.Call
import dk.eatmore.partner.model.GetReason
import dk.eatmore.partner.model.ProductDetails
import dk.eatmore.partner.storage.PreferenceUtil
import dk.eatmore.partner.utils.Constants


class ApiCall {
   // username,password_hash,type,login_type

    companion object {

        private fun getApiInterface(): ApiInterface {
            return ApiClient.getClient()!!.create(ApiInterface::class.java)
        }

        fun login(username : String, password_hash : String, type : String,login_type : String) : Call<JsonObject> {
            return getApiInterface().setLogin(username, password_hash, type,login_type, PreferenceUtil.getString(PreferenceUtil.LANGUAGE,"")!!,Constants.RESTAURANT_APP_ANDROID)
        }

        fun myOrder(to: String, from: String, r_key: String, r_token: String) : Call<Order> {
            val postParam = JsonObject()
            postParam.addProperty("r_token",r_token)
            postParam.addProperty("r_key",r_key)
            postParam.addProperty("order_to",to)
            postParam.addProperty("order_from",from)
            postParam.addProperty("language",PreferenceUtil.getString(PreferenceUtil.LANGUAGE,"")!!)
            postParam.addProperty(Constants.APP,Constants.RESTAURANT_APP_ANDROID)
            return getApiInterface().myOrder(postParam)
        }

        fun allRecords(r_key: String, r_token: String) : Call<GetReason> {
            return getApiInterface().allRecords(r_token,r_key,PreferenceUtil.getString(PreferenceUtil.LANGUAGE,"")!!,Constants.RESTAURANT_APP_ANDROID)
        }

        fun rejectOrders(r_key: String, r_token: String,order_no: String, reason: String, order_status: String) : Call<JsonObject> {
            return getApiInterface().rejectOrders(r_token,r_key,order_no,reason,order_status,PreferenceUtil.getString(PreferenceUtil.LANGUAGE,"")!!,Constants.RESTAURANT_APP_ANDROID)
        }

        fun acceptOrders(r_key: String, r_token: String, order_no: String, pickup_delivery_time: String, order_status: String) : Call<JsonObject> {
            return getApiInterface().acceptOrders(r_token,r_key,order_no,pickup_delivery_time,order_status,PreferenceUtil.getString(PreferenceUtil.LANGUAGE,"")!!,Constants.RESTAURANT_APP_ANDROID)
        }

        fun createOrder(r_key: String, r_token: String,reason: String, action_by: String) : Call<JsonObject> {
            return getApiInterface().createOrder(r_token,r_key,reason,action_by,PreferenceUtil.getString(PreferenceUtil.LANGUAGE,"")!!,Constants.RESTAURANT_APP_ANDROID)
        }

        fun updateOrder(r_key: String, r_token: String,reason: String, action_by: String, or_id : String) : Call<JsonObject> {
            return getApiInterface().updateOrder(r_token,r_key,reason,action_by,or_id,PreferenceUtil.getString(PreferenceUtil.LANGUAGE,"")!!,Constants.RESTAURANT_APP_ANDROID)
        }

        fun viewRecords (r_key: String, r_token: String,order_no: String) : Call<ProductDetails> {
            return getApiInterface().viewRecords(r_token,r_key,order_no,PreferenceUtil.getString(PreferenceUtil.LANGUAGE,"")!!,Constants.RESTAURANT_APP_ANDROID)
        }

        fun sendFcmToken (r_key: String, r_token: String, fcmToken: String, device_type : String , user_id : String) : Call<JsonObject> {
            return getApiInterface().sendFcmToken(r_token,r_key,fcmToken,device_type,user_id,Constants.RESTAURANT_APP_ANDROID,"track_one.mp3")
        }

        fun orderCounter (r_key: String, r_token: String) : Call<OrderCounter> {
            return getApiInterface().orderCounter(r_token,r_key,Constants.RESTAURANT_APP_ANDROID)
        }
        fun closedRestaurant (r_key: String, r_token: String) : Call<JsonObject> {
            return getApiInterface().closedRestaurant(r_token,r_key,PreferenceUtil.getString(PreferenceUtil.LANGUAGE,"")!!,Constants.RESTAURANT_APP_ANDROID)
        }
        fun closedRestDay (r_key: String, r_token: String) : Call<JsonObject> {
            return getApiInterface().closedRestDay(r_token,r_key,PreferenceUtil.getString(PreferenceUtil.LANGUAGE,"")!!,Constants.RESTAURANT_APP_ANDROID)
        }
        fun resetRestDay (r_key: String, r_token: String) : Call<JsonObject> {
            return getApiInterface().resetRestDay(r_token,r_key,PreferenceUtil.getString(PreferenceUtil.LANGUAGE,"")!!,Constants.RESTAURANT_APP_ANDROID)
        }
    }




}