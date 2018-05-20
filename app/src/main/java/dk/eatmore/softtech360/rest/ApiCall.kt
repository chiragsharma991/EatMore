package dk.eatmore.softtech360.rest

import com.google.gson.JsonObject
import dk.eatmore.softtech360.model.Order
import retrofit2.Call
import dk.eatmore.softtech360.dashboard.fragment.Task
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

            /*     {
                     "r_token":"w5oRqFiAXTBB3hwpixAORbg_BwUj0EMQ07042017114812",
                     "r_key":"fcARlrbZFXYee1W6eYEIA0VRlw7MgV4o07042017114812",
                     "order_to":"2018-05-18",
                     "order_from":"2018-05-18"


                 }*/



            val postParam = JsonObject()
            postParam.addProperty("r_token",r_token)
            postParam.addProperty("r_key",r_key)
            postParam.addProperty("order_to",to)
            postParam.addProperty("order_from",from)






            return getApiInterface().myOrder(postParam)
        }


        /*  fun     myOedr(map : HashMap<String, Any>): Call<Order> {
              val a= HashMap<String, Any>()
              map.put("r_token","w5oRqFiAXTBB3hwpixAORbg_BwUj0EMQ0704201711481")
              map.put("r_key","fcARlrbZFXYee1W6eYEIA0VRlw7MgV4o07042017114812")
              map.put("order_to","2018-05-18")
              map.put("order_from","2018-05-08")

              return getApiInterface().myOrder(a)
          }*/

    }




}