package dk.eatmore.softtech360.activity

import android.app.Application
import dk.eatmore.softtech360.storage.PreferenceUtil
import android.support.multidex.MultiDex


class EatMoreApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        PreferenceUtil.init(this)
    }
}