package pt.cosmik.boostctrl.ui.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import org.koin.android.viewmodel.ext.android.viewModel
import pt.cosmik.boostctrl.MainActivity
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.external.tournament_brackets.Fragment.BracketsFragment
import pt.cosmik.boostctrl.ui.common.BaseFragment
import pt.cosmik.boostctrl.utils.BoostCtrlAnalytics

class InfoFragment : BaseFragment() {

    private val vm: InfoViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?  ): View? {
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.viewState.observe(this, Observer { viewState ->

        })

        fragmentManager?.beginTransaction()?.add(R.id.brackets, BracketsFragment())?.commit()
        vm.processEvent(InfoViewModel.InfoFragmentEvent.ViewCreated)
    }

    override fun showErrorMessage(message: String) {
        (activity as MainActivity).showMessageInSnackBar(message)
    }

    override fun getActionBarTitle(): String = context?.getString(R.string.info) ?: ""

    override fun onResume() {
        super.onResume()
        BoostCtrlAnalytics.instance.trackScreen("InfoFragment")
        (activity as MainActivity).setActionBarTitle(getActionBarTitle())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeObservers()
    }

    override fun removeObservers() {
        vm.viewState.removeObservers(this)
        vm.viewEffect.removeObservers(this)
    }
}