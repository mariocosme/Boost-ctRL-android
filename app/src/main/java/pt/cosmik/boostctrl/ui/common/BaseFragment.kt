package pt.cosmik.boostctrl.ui.common

import android.view.Menu
import android.view.MenuInflater
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import pt.cosmik.boostctrl.R

abstract class BaseFragment: Fragment(), BaseFragmentInterface {

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        // Setting the menu items color
        context?.let { context ->
            for (i in 0 until menu.size()) {
                val drawable = menu.getItem(i).icon.mutate()
                DrawableCompat.setTint(drawable.mutate(), ContextCompat.getColor(context, R.color.colorIceberg))
            }
        }
    }

}

interface BaseFragmentInterface {
    fun showErrorMessage(message: String)
    fun getActionBarTitle(): String
}