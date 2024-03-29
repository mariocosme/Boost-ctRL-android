package pt.cosmik.boostctrl.ui.teams.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.viewpagerindicator.LinePageIndicator
import org.koin.android.viewmodel.ext.android.viewModel
import pt.cosmik.boostctrl.MainActivity
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.models.Team
import pt.cosmik.boostctrl.ui.common.BaseFragment
import pt.cosmik.boostctrl.ui.common.BoostCtrlSmallViewPagerAdapter
import pt.cosmik.boostctrl.ui.common.KeyValueListAdapter
import pt.cosmik.boostctrl.ui.common.SocialsListAdapter
import pt.cosmik.boostctrl.ui.person.PersonFragmentDirections
import pt.cosmik.boostctrl.utils.BoostCtrlAnalytics


class TeamFragment : BaseFragment() {

    private val vm: TeamViewModel by viewModel()

    private var loadingBar: ProgressBar? = null
    private var viewPager: ViewPager? = null
    private var linePageIndicator: LinePageIndicator? = null
    private var teamDescription: TextView? = null
    private var socialsText: TextView? = null

    private var dividerItemDeco: DividerItemDecoration? = null
    private var teamGeneralDetailsRecyclerView: RecyclerView? = null
    private val teamGeneralDetailsListAdapter = KeyValueListAdapter()
    private var teamRosterRecyclerView: RecyclerView? = null
    private val teamRosterListAdapter = TeamRosterListAdapter()
    private var socialsRecyclerView: RecyclerView? = null
    private val socialsAdapter = SocialsListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?  ): View? {
        return inflater.inflate(R.layout.fragment_team_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingBar = view.findViewById(R.id.loading_bar)
        viewPager = view.findViewById(R.id.view_pager)
        linePageIndicator = view.findViewById(R.id.page_indicator)
        teamDescription = view.findViewById(R.id.text_team_desc)
        socialsText = view.findViewById(R.id.text_socials)

        dividerItemDeco = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        context?.let { context ->
            ContextCompat.getDrawable(context, R.drawable.bg_list_news_item_separator)?.let { dividerItemDeco?.setDrawable(it) }
        }

        teamGeneralDetailsListAdapter.context = context
        teamGeneralDetailsRecyclerView = view.findViewById<RecyclerView>(R.id.team_general_details_recycler_view)?.apply {
            setHasFixedSize(true)
            dividerItemDeco?.let { addItemDecoration(it) }
            layoutManager = LinearLayoutManager(context)
            adapter = teamGeneralDetailsListAdapter
        }

        teamRosterListAdapter.context = context
        teamRosterRecyclerView = view.findViewById<RecyclerView>(R.id.team_roster_recycler_view)?.apply {
            setHasFixedSize(true)
            dividerItemDeco?.let { addItemDecoration(it) }
            layoutManager = LinearLayoutManager(context)
            adapter = teamRosterListAdapter
        }

        disposables.add(teamRosterListAdapter.onItemClickEvent().subscribe {
            vm.processEvent(TeamViewModel.TeamFragmentEvent.SelectedRosterItem(it))
        })

        socialsAdapter.context = context
        socialsRecyclerView = view.findViewById<RecyclerView>(R.id.socials_recycler_view)?.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 3)
            adapter = socialsAdapter
        }

        disposables.add(socialsAdapter.onItemClickEvent().subscribe {
            vm.processEvent(TeamViewModel.TeamFragmentEvent.SelectedTeamSocial(it))
        })

        vm.viewState.observe(this, Observer {
            loadingBar?.visibility = if (it.isLoading) View.VISIBLE else View.GONE
            it.barTitle?.let { barTitle -> (activity as MainActivity).setActionBarTitle(barTitle) }
            it.teamGeneralDetailItems?.let { items -> teamGeneralDetailsListAdapter.setItems(items) }
            it.teamRosterPlayerItems?.let { items -> teamRosterListAdapter.setItems(items) }
            it.teamSocialItems?.let { items ->
                if (items.isNotEmpty()) socialsText?.visibility = View.VISIBLE
                socialsAdapter.setItems(items)
            }
            it.teamImages?.let { images ->
                if (viewPager?.adapter == null) {
                    viewPager?.adapter = BoostCtrlSmallViewPagerAdapter(context!!, images)
                    linePageIndicator?.setViewPager(viewPager)

                    if (images.isNotEmpty()) viewPager?.visibility = View.VISIBLE
                    if (images.size > 1) linePageIndicator?.visibility = View.VISIBLE
                }
            }
            teamDescription?.text = it.teamDescription
        })

        vm.viewEffect.observe(this, Observer {
            when (it) {
                is TeamViewModel.TeamFragmentViewEffect.ShowError -> showErrorMessage(it.error)
                is TeamViewModel.TeamFragmentViewEffect.PresentPersonFragment -> findNavController().navigate(PersonFragmentDirections.actionGlobalPersonFragment(it.person))
                is TeamViewModel.TeamFragmentViewEffect.OpenActivity -> startActivity(it.intent)
            }
        })

        vm.processEvent(TeamViewModel.TeamFragmentEvent.ViewCreated(arguments?.get("team") as? Team, context))
    }

    override fun onResume() {
        super.onResume()
        vm.viewState.value?.barTitle?.let { BoostCtrlAnalytics.instance.trackScreen("TeamFragment: $it") }
    }

    override fun showErrorMessage(message: String) {
        (activity as MainActivity).showMessageInSnackBar(message)
    }

    override fun getActionBarTitle(): String = context?.getString(R.string.person) ?: ""

    override fun onDestroyView() {
        super.onDestroyView()
        removeObservers()
    }

    override fun removeObservers() {
        vm.viewState.removeObservers(this)
        vm.viewEffect.removeObservers(this)
    }
}

