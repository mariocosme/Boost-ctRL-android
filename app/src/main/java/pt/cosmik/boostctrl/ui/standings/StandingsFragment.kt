package pt.cosmik.boostctrl.ui.standings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pt.cosmik.boostctrl.MainActivity
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.ui.common.BaseFragment

class StandingsFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?  ): View? {
        return inflater.inflate(R.layout.fragment_standings, container, false)
    }

    override fun showErrorMessage(message: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getActionBarTitle(): String = context?.getString(R.string.standings) ?: ""

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setActionBarTitle(getActionBarTitle())
    }

}