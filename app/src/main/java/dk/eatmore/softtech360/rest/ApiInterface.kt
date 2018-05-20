package dk.eatmore.softtech360.rest

import com.google.gson.JsonObject
import dk.eatmore.softtech360.dashboard.fragment.Task
import dk.eatmore.softtech360.model.Order
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface ApiInterface {



    @FormUrlEncoded
    @POST("User/user/login")
    fun setLogin(
            @Field("username") username: String ,
            @Field("password_hash") password_hash: String,
            @Field("type") type: String,
            @Field("login_type") login_type: String): Call<JsonObject>


    @POST("PosOrder/order/all-orders")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    fun myOrder(@Body  jsonObject: JsonObject): Call<Order>

/*
    @Headers("Content-Type: text/html;charset=UTF-8")
    @POST("PosOrder/order/all-orders")
    fun myOrder(@Body bidy : HashMap<String, Any>): Call<Order>
*/





/*
    @FormUrlEncoded
    @POST("PosOrder/order/all-orders")
    fun myOrder(
            @Field("order_to") order_to: String,
            @Field("order_from") order_from: String,
            @Field("r_key") r_key: String,
            @Field("r_token") r_token: String): Call<Order>
*/


}