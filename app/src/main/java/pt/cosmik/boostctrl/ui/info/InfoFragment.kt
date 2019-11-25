package pt.cosmik.boostctrl.ui.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pt.cosmik.boostctrl.MainActivity
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.ui.common.BaseFragment

class InfoFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?  ): View? {
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun showErrorMessage(message: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getActionBarTitle(): String = context?.getString(R.string.info) ?: ""

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setActionBarTitle(getActionBarTitle())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeObservers()
    }

    override fun removeObservers() {
//        vm.viewState.removeObservers(this)
//        vm.viewEffect.removeObservers(this)
    }
}