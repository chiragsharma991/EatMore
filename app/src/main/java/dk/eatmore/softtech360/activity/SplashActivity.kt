package dk.eatmore.softtech360.activity

import android.app.PendingIntent.getActivity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import dk.eatmore.softtech360.R
import dk.eatmore.softtech360.utils.BaseActivity
import dk.eatmore.softtech360.utils.ImageLoader
import kotlinx.android.synthetic.main.activity_splash.*
import android.view.animation.Animation
import dk.eatmore.softtech360.dashboard.main.MainActivity
import dk.eatmore.softtech360.storage.PreferenceUtil


class SplashActivity : BaseActivity() {


 /*   override fun getLayout(): Int {
        return R.layout.activity_splash
    }*/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        init(savedInstanceState)


    }

     fun init(savedInstancedState: Bundle?) {

        fullScreen()
        val animation = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.zoom_out)
        img_logo.startAnimation(animation)




         /*       ImageLoader.loadImageFromResource(this, R.drawable.bg_gift_cards, spl_logo)
        spl_logo.translationY = -resources.getDimension(R.dimen._100sdp)
        spl_logo.alpha = 0F


        spl_logo.animate().alpha(1F).translationY(resources.getDimension(R.dimen._1sdp)).duration = 1000*/

      //  ImageLoader.loadImageFromResource(this, R.drawable.splash_bg, img_splash)

        Handler().postDelayed({
            if(PreferenceUtil.getString(PreferenceUtil.USER_NAME,"")==""){
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            }
            else{
              startActivity(Intent(this, MainActivity::class.java))

            }
        }, 3000)

    }





}
