package pt.cosmik.boostctrl.ui.matches.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import org.koin.android.viewmodel.ext.android.viewModel
import pt.cosmik.boostctrl.MainActivity
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.models.UpcomingMatch
import pt.cosmik.boostctrl.ui.common.BaseFragment
import pt.cosmik.boostctrl.utils.BoostCtrlAnalytics


class MatchFragment : BaseFragment() {

    private val vm: MatchViewModel by viewModel()

    private var loadingBar: ProgressBar? = null
    private var dateText: TextView? = null
    private var homeTeamImg: ImageView? = null
    private var awayTeamImg: ImageView? = null
    private var tournamentImg: ImageView? = null
    private var homeTeamText: TextView? = null
    private var awayTeamText: TextView? = null
    private var tournamentText: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?  ): View? {
        return inflater.inflate(R.layout.fragment_match_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingBar = view.findViewById(R.id.loading_bar)
        dateText = view.findViewById(R.id.text_event_date)
        homeTeamImg = view.findViewById(R.id.image_view_home_team)
        awayTeamImg = view.findViewById(R.id.image_view_away_team)
        homeTeamText = view.findViewById(R.id.text_home_team)
        awayTeamText = view.findViewById(R.id.text_away_team)
        tournamentText = view.findViewById(R.id.text_tournament_name)
        tournamentImg = view.findViewById(R.id.image_view_tournament)

        vm.viewState.observe(this, Observer {
            loadingBar?.visibility = if (it.isLoading) View.VISIBLE else View.GONE
            it.barTitle?.let { barTitle -> (activity as MainActivity).setActionBarTitle(barTitle) }
            homeTeamText!!.text = it.homeTeamName
            awayTeamText!!.text = it.awayTeamName
            tournamentText!!.text = it.tournamentName
            dateText!!.text = it.matchDate
            Glide.with(context!!).load(it.homeTeamImage).into(homeTeamImg!!)
            Glide.with(context!!).load(it.awayTeamImage).into(awayTeamImg!!)
            Glide.with(context!!).load(it.tournamentImage).into(tournamentImg!!)
        })

        vm.viewEffect.observe(this, Observer {
            when (it) {
                is MatchViewModel.MatchFragmentViewEffect.ShowError -> showErrorMessage(it.error)
            }
        })

        vm.processEvent(MatchViewModel.MatchFragmentEvent.ViewCreated(arguments?.get("match") as? UpcomingMatch))
    }

    override fun onResume() {
        super.onResume()
        vm.viewState.value?.barTitle?.let { BoostCtrlAnalytics.instance.trackScreen("MatchFragment: $it") }
    }

    override fun showErrorMessage(message: String) {
        (activity as MainActivity).showMessageInSnackBar(message)
    }

    override fun getActionBarTitle(): String = context?.getString(R.string.match_detail) ?: ""

    override fun onDestroyView() {
        super.onDestroyView()
        removeObservers()
    }

    override fun removeObservers() {
        vm.viewState.removeObservers(this)
        vm.viewEffect.removeObservers(this)
    }
}

