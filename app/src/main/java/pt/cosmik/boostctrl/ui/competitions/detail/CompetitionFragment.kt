package pt.cosmik.boostctrl.ui.competitions.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.viewpagerindicator.LinePageIndicator
import org.koin.android.viewmodel.ext.android.viewModel
import pt.cosmik.boostctrl.MainActivity
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.models.Competition
import pt.cosmik.boostctrl.models.Team
import pt.cosmik.boostctrl.ui.common.BaseFragment
import pt.cosmik.boostctrl.ui.common.BoostCtrlSmallViewPagerAdapter
import pt.cosmik.boostctrl.ui.person.PersonFragmentDirections
import pt.cosmik.boostctrl.ui.teams.detail.TeamFragmentDirections
import pt.cosmik.boostctrl.utils.BoostCtrlAnalytics


class CompetitionFragment : BaseFragment() {

    private val vm: CompetitionViewModel by viewModel()

    private var loadingBar: ProgressBar? = null
    private var imageView: ImageView? = null
    private var competitionDescription: TextView? = null

    private var dividerItemDeco: DividerItemDecoration? = null
    private var competitionGeneralDetailsRecyclerView: RecyclerView? = null
    private val competitionGeneralDetailsListAdapter = CompetitionGeneralDetailsListAdapter()
    private var competitionStandingsRecyclerView: RecyclerView? = null
    //private val competitionStandingsListAdapter = CompetitionStandingsListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?  ): View? {
        return inflater.inflate(R.layout.fragment_competition_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingBar = view.findViewById(R.id.loading_bar)
        imageView = view.findViewById(R.id.image_view)
        competitionDescription = view.findViewById(R.id.text_competition_desc)

        dividerItemDeco = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        context?.let { context ->
            ContextCompat.getDrawable(context, R.drawable.bg_list_news_item_separator)?.let { dividerItemDeco?.setDrawable(it) }
        }

        competitionGeneralDetailsListAdapter.context = context
        competitionGeneralDetailsRecyclerView = view.findViewById<RecyclerView>(R.id.competition_general_details_recycler_view)?.apply {
            setHasFixedSize(true)
            dividerItemDeco?.let { addItemDecoration(it) }
            layoutManager = LinearLayoutManager(context)
            adapter = competitionGeneralDetailsListAdapter
        }

        /*competitionStandingsListAdapter.context = context
        competitionStandingsRecyclerView = view.findViewById<RecyclerView>(R.id.competition_standings_recycler_view)?.apply {
            setHasFixedSize(true)
            dividerItemDeco?.let { addItemDecoration(it) }
            layoutManager = LinearLayoutManager(context)
            adapter = competitionStandingsListAdapter
        }

        disposables.add(competitionStandingsListAdapter.itemClickSubject.subscribe {
            vm.processEvent(CompetitionViewModel.CompetitionFragmentEvent.SelectedTeamItem(it))
        })*/

        vm.viewState.observe(this, Observer {
            loadingBar?.visibility = if (it.isLoading) View.VISIBLE else View.GONE
            it.barTitle?.let { barTitle -> (activity as MainActivity).setActionBarTitle(barTitle) }
            it.competitionGeneralDetailItems?.let { items -> competitionGeneralDetailsListAdapter.setItems(items) }
            //it.teamRosterPlayerItems?.let { items -> teamRosterListAdapter.setItems(items) }
            if (it.competitionImage != null && imageView != null) {
                imageView!!.visibility = View.VISIBLE
                Glide.with(context!!).load(it.competitionImage).into(imageView!!)
            }
            competitionDescription?.text = it.competitionDescription
        })

        vm.viewEffect.observe(this, Observer {
            when (it) {
                is CompetitionViewModel.CompetitionFragmentViewEffect.ShowError -> showErrorMessage(it.error)
                is CompetitionViewModel.CompetitionFragmentViewEffect.PresentTeamFragment -> findNavController().navigate(TeamFragmentDirections.actionGlobalTeamDetailFragment(it.team))
            }
        })

        vm.processEvent(CompetitionViewModel.CompetitionFragmentEvent.ViewCreated(arguments?.get("competition") as? Competition, context))
    }

    override fun onResume() {
        super.onResume()
        vm.viewState.value?.barTitle?.let { BoostCtrlAnalytics.instance.trackScreen("TeamFragment: $it") }
    }

    override fun showErrorMessage(message: String) {
        (activity as MainActivity).showMessageInSnackBar(message)
    }

    override fun getActionBarTitle(): String = context?.getString(R.string.competition_detail) ?: ""

    override fun onDestroyView() {
        super.onDestroyView()
        removeObservers()
    }

    override fun removeObservers() {
        vm.viewState.removeObservers(this)
        vm.viewEffect.removeObservers(this)
    }
}

