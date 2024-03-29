package dk.eatmore.partner.activity

import android.app.Application
import dk.eatmore.partner.storage.PreferenceUtil
import android.support.multidex.MultiDex
import com.crashlytics.android.Crashlytics
import dk.eatmore.partner.utils.AppLifecycleHandler
import dk.eatmore.partner.utils.LifeCycleDelegate
import io.fabric.sdk.android.Fabric
import java.util.*


class EatMoreApplication : Application(), LifeCycleDelegate {


    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        PreferenceUtil.init(this)
        val lifeCycleHandler = AppLifecycleHandler(this)
        registerLifecycleHandler(lifeCycleHandler)

        Fabric.with(this, Crashlytics()) //start log fabric crashlytics
        //Fabric.with(this,  Crashlytics.Builder().core(CrashlyticsCore.Builder().disabled( BuildConfig.DEBUG).build()).build()); //stop log fabric crashlytics
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