package dk.eatmore.softtech360.activity

import android.app.PendingIntent.getActivity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.util.Log
import android.view.View
import dk.eatmore.softtech360.R
import dk.eatmore.softtech360.utils.BaseActivity
import dk.eatmore.softtech360.utils.ImageLoader
import kotlinx.android.synthetic.main.activity_splash.*
import android.view.animation.Animation
import android.widget.Button
import android.widget.TextView
import dk.eatmore.softtech360.dashboard.main.MainActivity
import dk.eatmore.softtech360.storage.PreferenceUtil
import dk.eatmore.softtech360.utils.DialogUtils
import org.json.JSONObject
import org.jsoup.nodes.Document
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.net.URLConnection
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException


class SplashActivity : BaseActivity() {


 /*   override fun getLayout(): Int {
        return R.layout.activity_splash
    }*/

    private var currentVersionName: String?=null
    private var latestVersionName: String?=null


    companion object {

        val TAG = "SplashActivity"
        fun newInstance(): SplashActivity {
            return SplashActivity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        init(savedInstanceState)


    }

     fun init(savedInstancedState: Bundle?) {

        fullScreen()
        getCurrentVersion()
        val animation = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.zoom_out)
        img_logo.startAnimation(animation)





         /*       ImageLoader.loadImageFromResource(this, R.drawable.bg_gift_cards, spl_logo)
        spl_logo.translationY = -resources.getDimension(R.dimen._100sdp)
        spl_logo.alpha = 0F


        spl_logo.animate().alpha(1F).translationY(resources.getDimension(R.dimen._1sdp)).duration = 1000*/

      //  ImageLoader.loadImageFromResource(this, R.drawable.splash_bg, img_splash)

        Handler().postDelayed({
           // GetLatestVersion().execute()
            moveToLogin()


        }, 3000)

    }



    private fun getCurrentVersion() {

        try {

            val pm = this.packageManager
            val pInfo = pm.getPackageInfo(this.packageName, 0)

            currentVersionName = pInfo.versionName
            Log.e(TAG, "currentVersionName : $currentVersionName")


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private inner class GetLatestVersion : AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String): String? {

            try {
            log(TAG,"do in background...")
                var playStoreAppVersion: String? = null
                var inReader: BufferedReader? = null
                var uc: URLConnection? = null
                val doc: Document? = null
                val currentVersion_PatternSeq = "<div[^>]*?>Current\\sVersion</div><span[^>]*?>(.*?)><div[^>]*?>(.*?)><span[^>]*?>(.*?)</span>"
                val appVersion_PatternSeq = "htlgb\">([^<]*)</s"
                val urlData = StringBuilder()
                //Play Store - URL
                val urlOfAppFromPlayStore = "https://play.google.com/store/apps/details?id=dk.eatmore.asbendospizza"

                val url = URL("https://play.google.com/store/apps/details?id=dk.eatmore.rns")//+ this@SplashActivity.getPackageName())
                uc = url.openConnection()
                if (uc == null) {
                    return null
                }
                uc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                inReader = BufferedReader(InputStreamReader(uc.getInputStream()))
                if (null != inReader) {
                    var str = ""
                    while ((inReader.readLine()) != null) {
                        str =inReader.readLine()
                        urlData.append(str)
                    }
                }
                // Get the current version pattern sequence
                val versionString = getAppVersion(currentVersion_PatternSeq, urlData.toString())
                if (null == versionString) {
                    return null
                } else {
                    // get version from "htlgb">X.X.X</span>
                    playStoreAppVersion = getAppVersion(appVersion_PatternSeq, versionString)
                    latestVersionName = playStoreAppVersion
                    Log.e(TAG, "latestVersionCode : $latestVersionName")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "error from play store: " + e.message)
            }

            return latestVersionName

        }

        private fun getAppVersion(patternString: String, inputString: String): String? {
            try {
                //Create a pattern
                val pattern = Pattern.compile(patternString) ?: return null

                //Match the pattern string in provided string
                val matcher = pattern.matcher(inputString)
                if (null != matcher && matcher.find()) {
                    return matcher.group(1)
                }

            } catch (ex: PatternSyntaxException) {

                ex.printStackTrace()
            }

            return null
        }


        override fun onPostExecute(version: String) {
            super.onPostExecute(version)

            try {

                   if (currentVersionName != null && latestVersionName != null) {

                       if (checkForUpdate(currentVersionName!!, latestVersionName!!))
                           versionUpdate(latestVersionName!!)
                       else{
                          moveToLogin()
                       }
                   }


            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "on Exception... : ${e.message} ")

            }


    }



        fun checkForUpdate(existingVersion: String, newVersion: String): Boolean {
            if (TextUtils.isEmpty(existingVersion) || TextUtils.isEmpty(newVersion)) {
                return false
            }

            if (existingVersion.equals(newVersion, ignoreCase = true)) {
                return false
            }

            var newVersionIsGreater = false
            val existingVersionArray = existingVersion.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val newVersionArray = newVersion.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            val maxIndex = Math.max(existingVersionArray.size, newVersionArray.size)
            for (i in 0 until maxIndex) {
                var newValue: Int
                var excValue: Int
                try {
                    excValue = Integer.parseInt(existingVersionArray[i])
                } catch (e: ArrayIndexOutOfBoundsException) {
                    excValue = 0
                }

                try {
                    newValue = Integer.parseInt(newVersionArray[i])
                } catch (e: ArrayIndexOutOfBoundsException) {
                    newValue = 0
                }
                Log.e(TAG, "checkForUpdate - old values is - $excValue and new values is $newValue")

                if (excValue < newValue) {
                    newVersionIsGreater = true
                    continue
                }
            }
            return newVersionIsGreater
        }


}

    fun versionUpdate(versionName: String) {


        DialogUtils.openDialog ( this,"${getString(R.string.app_name)} $versionName", getString(R.string.new_update),
                getString(R.string.go_on_store),getString(R.string.dismiss), ContextCompat.getColor(this,R.color.black), object : DialogUtils.OnDialogClickListener {
            override fun onPositiveButtonClick(position: Int) {
              //  startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(urlOfAppFromPlayStore)))
                moveToLogin()
            }

            override fun onNegativeButtonClick() {
                moveToLogin()

            }
        })

    }

    private fun moveToLogin() {
        if(PreferenceUtil.getString(PreferenceUtil.USER_NAME,"")==""){
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        else{
            startActivity(Intent(this, MainActivity::class.java))

        }
    }

}
