package dk.eatmore.softtech360.activity

import android.app.Application
import dk.eatmore.softtech360.storage.PreferenceUtil
import android.support.multidex.MultiDex
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.BuildConfig
import com.crashlytics.android.core.CrashlyticsCore
import dk.eatmore.softtech360.utils.AppLifecycleHandler
import dk.eatmore.softtech360.utils.Custom_data
import dk.eatmore.softtech360.utils.LifeCycleDelegate
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.fragment_settinginfo.*
import java.util.*


class EatMoreApplication : Application(), LifeCycleDelegate {


    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        PreferenceUtil.init(this)
        val lifeCycleHandler = AppLifecycleHandler(this)
        registerLifecycleHandler(lifeCycleHandler)

        //Fabric.with(this, Crashlytics()) //start log fabric crashlytics
        Fabric.with(this,  Crashlytics.Builder().core(CrashlyticsCore.Builder().disabled( BuildConfig.DEBUG).build()).build()); //stop log fabric crashlytics
        //  Crashlytics.getInstance().crash()



    }


    override fun onAppBackgrounded() {
        val deviceLocale = Locale.getDefault().language

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