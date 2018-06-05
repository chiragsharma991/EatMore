package dk.eatmore.softtech360.activity

import android.app.Application
import dk.eatmore.softtech360.storage.PreferenceUtil
import android.support.multidex.MultiDex
import android.util.Log
import dk.eatmore.softtech360.utils.AppLifecycleHandler
import dk.eatmore.softtech360.utils.Custom_data
import dk.eatmore.softtech360.utils.LifeCycleDelegate
import kotlinx.android.synthetic.main.fragment_settinginfo.*
import java.util.*


class EatMoreApplication : Application(), LifeCycleDelegate {


    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        PreferenceUtil.init(this)
        val lifeCycleHandler = AppLifecycleHandler(this)
        registerLifecycleHandler(lifeCycleHandler)


    }


    override fun onAppBackgrounded() {
        val deviceLocale = Locale.getDefault().language
        if(PreferenceUtil.getString(PreferenceUtil.USER_NAME,"")!="")
        Custom_data.setWalkLock(false,this)
     //   PreferenceUtil.putValue(PreferenceUtil.KEEP_SCREEN_ON, false)

        if (deviceLocale.equals("da")) {
            PreferenceUtil.putValue(PreferenceUtil.LANGUAGE, deviceLocale)
            PreferenceUtil.save()
        } else {
            PreferenceUtil.putValue(PreferenceUtil.LANGUAGE, deviceLocale)
            PreferenceUtil.save()
        }

    }

    override fun onAppForegrounded() {

        val deviceLocale = Locale.getDefault().language
        Log.e("Activity life cycle---", "onAppForegrounded=" + deviceLocale)

        if (PreferenceUtil.getBoolean(PreferenceUtil.KEEP_SCREEN_ON, false) && PreferenceUtil.getString(PreferenceUtil.USER_NAME,"") !="" ){
            Custom_data.setWalkLock(true,this)
            PreferenceUtil.putValue(PreferenceUtil.KEEP_SCREEN_ON,true)
            PreferenceUtil.save()
        }

        if (deviceLocale.equals("da")) {
            PreferenceUtil.putValue(PreferenceUtil.LANGUAGE, deviceLocale)
            PreferenceUtil.save()
        } else {
            PreferenceUtil.putValue(PreferenceUtil.LANGUAGE, deviceLocale)
            PreferenceUtil.save()
        }

    }


    private fun registerLifecycleHandler(lifeCycleHandler: AppLifecycleHandler) {
        registerActivityLifecycleCallbacks(lifeCycleHandler)
        registerComponentCallbacks(lifeCycleHandler)
    }


}