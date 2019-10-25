package pt.cosmik.boostctrl.ui.teams

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import org.koin.android.viewmodel.ext.android.viewModel
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.ui.common.BaseFragment
import pt.cosmik.boostctrl.utils.Constants

class TeamsFragment : BaseFragment() {

    private val vm: TeamsViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_teams, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.processEvent(TeamsViewModel.TeamsFragmentEvent.CreatedView)

        vm.viewState.observe(this, Observer {
            Log.d(Constants.LOG_TAG, "is loading: ${it.isLoading}")
        })
    }

    override fun showErrorMessage(message: String) {
        // TODO
    }

}