package dk.eatmore.softtech360.rest

import com.google.gson.JsonObject
import dk.eatmore.softtech360.model.Order
import retrofit2.Call
import retrofit2.http.*


interface ApiInterface {



    @FormUrlEncoded
    @POST("User/user/login")
    fun setLogin(
            @Field("username") username: String,
            @Field("password_hash") password_hash: String,
            @Field("type") type: String,
            @Field("login_type") login_type: String): Call<JsonObject>

    @FormUrlEncoded
    @POST("PosOrder/order/all-orders")
    fun myOrder(
            @Field("order_to") order_to: String,
            @Field("order_from") order_from: String,
            @Field("r_key") r_key: String,
            @Field("r_token") r_token: String): Call<Order>


}