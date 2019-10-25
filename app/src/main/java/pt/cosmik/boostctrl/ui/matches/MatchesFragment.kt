package pt.cosmik.boostctrl.ui.matches

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.ui.common.BaseFragment

class MatchesFragment : BaseFragment() {

    private lateinit var matchesViewModel: MatchesViewModel

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        matchesViewModel = ViewModelProviders.of(this).get(MatchesViewModel::class.java)
        return inflater.inflate(R.layout.fragment_matches, container, false)
    }

    override fun showErrorMessage(message: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}