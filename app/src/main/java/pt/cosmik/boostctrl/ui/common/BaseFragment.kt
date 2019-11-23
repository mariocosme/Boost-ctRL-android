package pt.cosmik.boostctrl.ui.common

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import pt.cosmik.boostctrl.BuildConfig
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.utils.Constants

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (BuildConfig.DEBUG) Log.d(Constants.LOG_TAG, "${this::class.java}: onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG) Log.d(Constants.LOG_TAG, "${this::class.java}: onCreate")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (BuildConfig.DEBUG) Log.d(Constants.LOG_TAG, "${this::class.java}: onViewCreated")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (BuildConfig.DEBUG) Log.d(Constants.LOG_TAG, "${this::class.java}: onActivityCreated")
    }

    override fun onStart() {
        super.onStart()
        if (BuildConfig.DEBUG) Log.d(Constants.LOG_TAG, "${this::class.java}: onStart")
    }

    override fun onResume() {
        super.onResume()
        if (BuildConfig.DEBUG) Log.d(Constants.LOG_TAG, "${this::class.java}: onResume")
    }

    override fun onPause() {
        super.onPause()
        if (BuildConfig.DEBUG) Log.d(Constants.LOG_TAG, "${this::class.java}: onPause")
    }

    override fun onStop() {
        super.onStop()
        if (BuildConfig.DEBUG) Log.d(Constants.LOG_TAG, "${this::class.java}: onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (BuildConfig.DEBUG) Log.d(Constants.LOG_TAG, "${this::class.java}: onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (BuildConfig.DEBUG) Log.d(Constants.LOG_TAG, "${this::class.java}: onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        if (BuildConfig.DEBUG) Log.d(Constants.LOG_TAG, "${this::class.java}: onDetach")
    }

}

interface BaseFragmentInterface {
    fun showErrorMessage(message: String)
    fun getActionBarTitle(): String
}