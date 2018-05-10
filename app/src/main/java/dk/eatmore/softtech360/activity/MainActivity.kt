package dk.eatmore.softtech360.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import dk.eatmore.softtech360.rest.ApiInterface
import dk.eatmore.softtech360.model.UserDetail_model
import dk.eatmore.softtech360.R
import dk.eatmore.softtech360.rest.ApiClient
import dk.eatmore.softtech360.testing.Test_two
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val TAG = this.javaClass.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        mathmetics()
    }

    private fun mathmetics() {

        val apiService = ApiClient.getClient()!!.create(ApiInterface::class.java)


        val call = apiService.getTopRatedMovies()
        call!!.enqueue(object : Callback<List<UserDetail_model>> {
            override fun onResponse(call: Call<List<UserDetail_model>>, response: Response<List<UserDetail_model>>) {
                Log.e(TAG, "Number of movies received: " + response.body()!!.size)
            }

            override fun onFailure(call: Call<List<UserDetail_model>>, t: Throwable) {
                // Log error here since request failed
                Log.e(TAG, t.toString())
            }
        })


    }
}