package pt.cosmik.boostctrl.ui.common

import androidx.fragment.app.Fragment

abstract class BaseFragment: Fragment() {
    protected abstract fun showErrorMessage(message: String)
    protected abstract fun getActionBarTitle(): String
}