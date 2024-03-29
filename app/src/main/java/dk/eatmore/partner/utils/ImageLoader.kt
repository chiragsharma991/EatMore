package dk.eatmore.partner.utils

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

object ImageLoader {


    /**
     * Load image with round shape
     */
    fun loadImageCircleFromResource(context: Context, resourceId: Int, placeholderImg: Int, imageView: ImageView) {
        Glide.with(context).load(resourceId).apply(RequestOptions.circleCropTransform().placeholder(placeholderImg)).into(imageView)
    }

    fun loadImageCircleFromResource(context: Context, resourceId: Int, imageView: ImageView) {
        Glide.with(context).load(resourceId).apply(RequestOptions.circleCropTransform()).into(imageView)
    }

    fun loadImageCircleFromUrl(context: Context, url: String, imageView: ImageView) {
        Glide.with(context).load(url).apply(RequestOptions.circleCropTransform()).into(imageView)
    }

    fun loadImageCircleFromUrl(context: Context, url: String, placeholderImg: Int, imageView: ImageView) {
        Glide.with(context).load(url)
                .apply(RequestOptions.circleCropTransform().placeholder(placeholderImg).error(placeholderImg))
                .into(imageView)
    }

    fun loadImageFromUrlClearCathc(context: Context, url: String, placeholderImg: Int, imageView: ImageView){
        Glide.with(context).load(url)
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                        .placeholder(placeholderImg).error(placeholderImg))
                .into(imageView)
    }


    /**
     * Load image with as it is
     */
    fun loadImageFromResource(context: Context, resourceId: Int, placeholderImg: Int, imageView: ImageView) {
        Glide.with(context).load(resourceId).apply(RequestOptions().placeholder(placeholderImg).error(placeholderImg))
                .into(imageView)
    }

    fun loadImageFromResource(context: Context, resourceId: Int, imageView: ImageView) {
        Glide.with(context).load(resourceId).apply(RequestOptions()).into(imageView)
    }

    fun loadImageFromUrl(context: Context, url: String, imageView: ImageView) {
        Glide.with(context).load(url).apply(RequestOptions()).into(imageView)
    }

    fun loadImageFromUrl(context: Context, url: String, placeholderImg: Int, imageView: ImageView) {
        Glide.with(context).load(url).apply(RequestOptions().placeholder(placeholderImg)).into(imageView)
    }

    /**
     * Load image from URI
     */
    fun loadImageCircleFromUri(context: Context, fromFile: Uri, imageView: ImageView) {
        Glide.with(context).load(fromFile).apply(RequestOptions.circleCropTransform()).into(imageView)
    }

    fun saveSocialImage(context: Context, path: String) {
    }

    fun loadImageFromUri(context: Context, fromFile: Uri, imageView: ImageView) {
        Glide.with(context).load(fromFile).apply(RequestOptions()).into(imageView)
    }

    fun loadImageFromUri(context: Context, fromFile: Uri, placeholderImg: Int, imageView: ImageView) {
        Glide.with(context).load(fromFile).apply(RequestOptions().placeholder(placeholderImg)).into(imageView)
    }
}