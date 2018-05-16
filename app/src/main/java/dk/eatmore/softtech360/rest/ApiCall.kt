package dk.eatmore.softtech360.rest

import com.google.gson.JsonObject
import dk.eatmore.softtech360.model.Order
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import org.json.JSONObject



class ApiCall {
   // username,password_hash,type,login_type

    companion object {

        private fun getApiInterface(): ApiInterface {
            return ApiClient.getClient()!!.create(ApiInterface::class.java)
        }

        fun login(username : String, password_hash : String, type : String,login_type : String) : Call<JsonObject> {
            return getApiInterface().setLogin(username, password_hash, type,login_type)
        }

        fun myOrder(to : String, from : String, r_key : String,r_token : String) : Call<JsonObject> {

            val paramObject = JSONObject()
            paramObject.put("r_key", "fcARlrbZFXYee1W6eYEIA0VRlw7MgV4o07042017114812")
            paramObject.put("r_token", "w5oRqFiAXTBB3hwpixAORbg_BwUj0EMQ07042017114812")

            return getApiInterface().myOrder( paramObject.toString())
        }


        fun toRequestBody(value: String): RequestBody {
            val body = RequestBody.create(MediaType.parse("text/plain"), value)
            return body
        }
    }




}