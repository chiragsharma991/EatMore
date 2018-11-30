package dk.eatmore.partner.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import dk.eatmore.partner.BuildConfig
import dk.eatmore.partner.R
import dk.eatmore.partner.rest.ApiClient
import dk.eatmore.partner.rest.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.os.Build
import com.google.gson.Gson
import com.google.gson.JsonObject
import dk.eatmore.partner.dashboard.main.RestaurantClosed


abstract class BaseFragment : Fragment() {

    abstract fun getLayout(): Int
    abstract fun initView(view: View?, savedInstanceState: Bundle?)
    lateinit var displayMetrics: DisplayMetrics
    abstract fun handleBackButton() : Boolean



    override fun onAttach(context: Context?) {
        super.onAttach(context)
        displayMetrics = DisplayMetrics()
        getActivityBase().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getLayout(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view, savedInstanceState)
    }

    fun addFragment(container: Int, fragment: Fragment, tag: String) {
        hideKeyboard()
        childFragmentManager.beginTransaction().add(container, fragment, tag).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(tag).commit()
    }

    fun isPermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            if (context!!.checkSelfPermission(android.Manifest.permission.CALL_PHONE) === PackageManager.PERMISSION_GRANTED) {
                return true
            } else {
                ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.CALL_PHONE), 1)
                return false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true
        }
    }

    fun popFragment(): Boolean {
        var isPop = false
        try {
            if (childFragmentManager.backStackEntryCount > 0) {
                /**
                 * Check Filter Fragment Appear or not, Filter Type Fragment Also
                 */
                hideKeyboard()

                var fragment = childFragmentManager.findFragmentByTag(childFragmentManager.getBackStackEntryAt(childFragmentManager.backStackEntryCount - 1).name)
                if(fragment != null && fragment is BaseFragment){
                    if(fragment.handleBackButton()){
                        isPop = true
                        childFragmentManager.popBackStack()
                    }else{
                        isPop = true
                    }
                }else {
                    isPop = true
                    childFragmentManager.popBackStack()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return isPop
    }

  /*  fun popFragment(parentFragment: Fragment?) {
        try {
            if (parentFragment is AdminDashboardContainerFragment) {
                parentFragment.popFragment()
            } else if (parentFragment is ProductListFragment) {
                parentFragment.popFragment()
            }else if (parentFragment is RetailerDashboardFragment) {
                parentFragment.popFragment()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/

    fun popAllFragment() {


  /*      for (i in 0 until childFragmentManager.backStackEntryCount) {
            childFragmentManager.popBackStack()
        }*/
    }

    fun popWithTag(tag : String) {
        childFragmentManager.popBackStack(tag,0)
    }

    fun pop() {
        childFragmentManager.popBackStack()
    }

    fun isInternetAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
    }

    fun showSnackBar(string: String) {
        if (view != null && userVisibleHint)
            Snackbar.make(view!!, string, Snackbar.LENGTH_SHORT).show()
    }

    fun showSnackbarLogin() {
        /* val mSnackbar = Snackbar
                 .make(view!!, getString(R.string.you_are_not_login__), Snackbar.LENGTH_LONG)
                 .setAction(getString(R.string.sign_in)) {
                     *//**
         * putExtra for it comes from ProfileScreen
         *//*
                    startActivityForResult(Intent(activity, PreLoginActivity::class.java).putExtra(Constants.EXTRA_FROM_PROFILE, true), REQUEST_CODE_LOGIN)
                }
        mSnackbar.show()*/
    }



    fun getActivityBase(): Activity {
        return activity!!
    }

    fun log(tag: String, msg: String) {
        if (BuildConfig.DEBUG)
            Log.e(tag, msg)
    }

    /**
     * API CAll START
     */

    fun getApiInterface(): ApiInterface {
        return ApiClient.getClient()!!.create(ApiInterface::class.java)
    }

    /**
     * @param 100: No network connection.
     * @param 404: Server problem.
     * @param 400: Request being canceled.
     */


    fun <T> callAPI(call: Call<T>, onAliCallInteraction: OnApiCallInteraction) {
        if (isInternetAvailable()) {
            call.enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    try {
                        if (response.isSuccessful) {

                            log("response.body----",response.body().toString())
                            val gson= Gson()
                            val json=gson.toJson(response.body()) // convert body to normal json
                            var convertedObject = gson.fromJson(json, JsonObject::class.java) // convert into Jsonobject
                            log("response.convertedObject----",convertedObject.toString())

                            if(convertedObject.has(Constants.WHOLE_SYSTEM)){
                                if(convertedObject.get(Constants.WHOLE_SYSTEM).isJsonNull){
                                    onAliCallInteraction.onSuccess(response.body())
                                }else{
                                    if((convertedObject.get(Constants.WHOLE_SYSTEM).asBoolean== true) || (convertedObject.get(Constants.RESTAURANT_APP_ANDROID).asBoolean== true) ){
                                        onAliCallInteraction.onFail(404)
                                        val intent = Intent(activity, RestaurantClosed::class.java)
                                        val bundle = Bundle()
                                        bundle.putString(Constants.MESSAGE_TITLE,convertedObject.get(Constants.MESSAGE_TITLE).asString)
                                        bundle.putString(Constants.MESSAGE_DETAILS,convertedObject.get(Constants.MESSAGE_DETAILS).asString)
                                        intent.putExtras(bundle)
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(intent)
                                    }else{
                                        onAliCallInteraction.onSuccess(response.body())
                                    }
                                }
                            }else{
                                onAliCallInteraction.onSuccess(response.body())

                            }


                            //onAliCallInteraction.onSuccess(response.body())



                        } else {
                            var mErrorBody: String = response.errorBody()!!.string()
                            onAliCallInteraction.onFail(404)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    log("Base", ""+t.message)
                    if(call.isCanceled)
                    onAliCallInteraction.onFail(400)
                    else
                    onAliCallInteraction.onFail(100)

                }
            })
        } else {
            onAliCallInteraction.onFail(100)
           // showSnackBar(getString(R.string.internet_not_available))
        }
    }

    interface OnApiCallInteraction {
        //  100 > network not foune  : 404 > server error.
        fun <T> onSuccess(body: T?)
        fun onFail(error : Int)
    }

    /**
     * API Call END
     */

    fun hideKeyboard() {
        if (view != null) {
            val imm = getActivityBase().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view!!.windowToken, 0)
            //imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }

    fun getHeight(): Int {
        return displayMetrics.heightPixels
    }

    fun getWidth(): Int {
        return displayMetrics.widthPixels
    }


    fun getCalculatedDate(dateFormat: String, days: Int): String {
        val cal = Calendar.getInstance()
        val s = SimpleDateFormat(dateFormat)
        cal.add(Calendar.DAY_OF_YEAR, days)
        return s.format(Date(cal.timeInMillis))
    }


}