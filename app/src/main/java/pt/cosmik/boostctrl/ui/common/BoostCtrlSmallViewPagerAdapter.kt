package pt.cosmik.boostctrl.ui.common

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import pt.cosmik.boostctrl.R

class BoostCtrlSmallViewPagerAdapter(val context: Context, private val images: List<String> ): PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any): Boolean = `object` == view

    override fun getCount(): Int = images.size

    @SuppressLint("InflateParams")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(context).inflate(R.layout.small_image_view_pager_item, null)
        val imageView = view.findViewById<ImageView>(R.id.image)
        Glide.with(context).load(images[position]).into(imageView)
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

}