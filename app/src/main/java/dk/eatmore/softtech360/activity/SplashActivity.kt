package dk.eatmore.softtech360.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import dk.eatmore.softtech360.R
import dk.eatmore.softtech360.utils.BaseActivity
import dk.eatmore.softtech360.utils.ImageLoader
import kotlinx.android.synthetic.main.activity_splash.*
import android.view.animation.Animation



class SplashActivity : BaseActivity() {


    override fun getLayout(): Int {
        return R.layout.activity_splash
    }

    override fun init(savedInstancedState: Bundle?) {

        fullScreen()
        val animation = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.zoom_out)
        img_logo.startAnimation(animation)
 /*       ImageLoader.loadImageFromResource(this, R.drawable.bg_gift_cards, spl_logo)
        spl_logo.translationY = -resources.getDimension(R.dimen._100sdp)
        spl_logo.alpha = 0F


        spl_logo.animate().alpha(1F).translationY(resources.getDimension(R.dimen._1sdp)).duration = 1000*/

      //  ImageLoader.loadImageFromResource(this, R.drawable.splash_bg, img_splash)

        Handler().postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 3000)

    }


}
