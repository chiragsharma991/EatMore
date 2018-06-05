package dk.eatmore.softtech360.utils

import android.content.Context
import android.os.PowerManager
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import dk.eatmore.softtech360.R
import dk.eatmore.softtech360.storage.PreferenceUtil
import kotlin.math.log

object Custom_data {

    private var pm: PowerManager? =null
    private var wl: PowerManager.WakeLock? =null

    fun setWalkLock(keep: Boolean, context: Context) :Boolean {

       // if(pm ==null){
            Log.e("keep screen ---","null")
            pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
           wl = pm!!.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "KEEP_SCREEN_ON")
     //   }

        if (keep) {
            Log.e("keep screen ---","true")


            wl!!.acquire()
            return true

        } else {
            Log.e("keep screen ---","false")

            try {

                wl!!.release()

            }catch (e: Exception){
                Log.e("error",e.message)
            }
            return true

        }

    }
}

